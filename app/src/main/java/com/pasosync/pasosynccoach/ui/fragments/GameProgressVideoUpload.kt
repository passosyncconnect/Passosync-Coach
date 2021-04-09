package com.pasosync.pasosynccoach.ui.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.data.VideoProgressData
import com.pasosync.pasosynccoach.other.Constant
import com.pasosync.pasosynccoach.other.Constant.REQUEST_CODE_IMAGE_PICK


import kotlinx.android.synthetic.main.game_progress_vdeio_upload.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


private const val TAG = "GameProgressVideoUpload"

class GameProgressVideoUpload : Fragment(R.layout.game_progress_vdeio_upload) {


    var videoUri: Uri? = null
    var imageUriList = arrayListOf<Uri>()
    var positionOfImage = 0
    var videoProgressUri: Uri? = null
    var videoProgressUrl: String? = null
    var radioText = ""

    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog
    private val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()
    private val userGameVideoProgressCollectionRef =
        db.collection("UserDetails").document(user?.uid!!).collection(
            "GameProgressVideoDetails"
        )
    private val userPracticeVideoProgressectionRef =
        db.collection("UserDetails").document(user?.uid!!).collection(
            "PracticeProgressVideoDetails"
        )

    val userVideoProgressStorageReference =
        Firebase.storage.reference.child("UserVideoProgressData")



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.game_progress_vdeio_upload, container, false)
        setHasOptionsMenu(true)
        return v
    }

    fun makeView(): View? {
        val imageView = ImageView(requireContext())
        imageView.setBackgroundColor(-0x1000000)
        imageView.scaleType = ImageView.ScaleType.CENTER

        return imageView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = " "


        builder = AlertDialog.Builder(requireContext())
        imageSwitcher.setFactory { ImageView(requireContext()) }


        radioGroupVideo.setOnCheckedChangeListener { group, checkedId ->
            var rb = view.findViewById<RadioButton>(checkedId)
            if (rb != null) {
                Log.d(TAG, "onViewCreated: ${rb.text.toString()}")
                radioText = rb.text.toString()
                Log.d(TAG, "onViewCreated:$radioText")
            } else {
                radioText = "Game"
            }

        }

        try {
            val bundle=this.arguments
            val path=bundle!!.getString("trimming")
            videoProgressUri= Uri.parse(path)
        } catch (e: Exception) {
            Toast.makeText(requireContext(),e.message,Toast.LENGTH_SHORT).show()
        }
//        Toast.makeText(requireContext(),"logged",Toast.LENGTH_SHORT).show()


        pickVideo.setOnClickListener {
            pickVideoIntent()

        }

        pickImages.setOnClickListener {
            pickImageIntent()
        }

        previousImageSwitcher.setOnClickListener {
            if (positionOfImage > 0) {
                positionOfImage--
                imageSwitcher.setImageURI(imageUriList[positionOfImage])

            } else {
                Toast.makeText(requireContext(), "No more Images", Toast.LENGTH_SHORT).show()
            }

        }
        builder.setCancelable(false) // if you want user to wait for some process to finish,
        (activity as AppCompatActivity).supportActionBar?.title = " "

        builder.setView(R.layout.layout_uploading_dialog)
        dialog = builder.create()

        nextImageSwitcher.setOnClickListener {
            if (positionOfImage < imageUriList.size - 1) {

                positionOfImage++
                imageSwitcher.setImageURI(imageUriList[positionOfImage])

            } else {
                Toast.makeText(requireContext(), "No More Images", Toast.LENGTH_SHORT).show()
            }

        }

        btnUploadVideoProgress.setOnClickListener {
            if (videoProgressUri == null) {
                Toast.makeText(requireContext(), "Please Select a video", Toast.LENGTH_SHORT).show()
            } else {
                uploadVideoToStorage()
            }
        }

        btnUploadData.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                try {

                    val videourl = videoProgressUrl.toString()
                    val type = radioText.toString()
                    val id=userGameVideoProgressCollectionRef.document().id


                    val videoProgressData = VideoProgressData(videourl,type,id)

                    if (videoProgressUrl == null) {
                        Toast.makeText(
                            requireContext(),
                            "There is no video selected or uploaded",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {


                        if (type == "Game") {
//                            val data = userGameVideoProgressCollectionRef.add(videoProgressData)
                            val data=userGameVideoProgressCollectionRef.document(id).set(videoProgressData)
                            Snackbar.make(
                                requireView(),
                                "Uploaded successfully",
                                Snackbar.LENGTH_SHORT
                            )
                                .show()
                            findNavController().navigate(R.id.action_gameProgressVideoUpload_to_gameVideoFragment)


                        } else if (type == "Practice") {
                            val freedata = userPracticeVideoProgressectionRef.document(id).set(videoProgressData)
                            Snackbar.make(
                                requireView(),
                                "Uploaded successfully",
                                Snackbar.LENGTH_SHORT
                            )
                                .show()
                            findNavController().navigate(R.id.action_gameProgressVideoUpload_to_gameVideoFragment)


                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Please choose between \n Game and Practice",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    }


                } catch (e: Exception) {
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                }
            }


        }


    }


    private fun pickVideoIntent() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "video/mp4"
        startActivityForResult(intent, Constant.PICK_VIDEO)
    }

    private fun pickImageIntent() {
        val intent = Intent().apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            action = Intent.ACTION_GET_CONTENT

        }
        startActivityForResult(
            Intent.createChooser(intent, "Select Image(s)"),
            REQUEST_CODE_IMAGE_PICK
        )

    }


    private fun uploadVideoToStorage() = CoroutineScope(Dispatchers.IO).launch {
        try {
            videoProgressUri?.let {
                val videoFileName =
                    userVideoProgressStorageReference.child("Video${System.currentTimeMillis()}" + videoProgressUri?.lastPathSegment)
                videoFileName.putFile(it).addOnProgressListener {
                    dialog.show()

                }.addOnSuccessListener { taskSnapshot ->
                    videoFileName.downloadUrl.addOnSuccessListener {
                        videoProgressUrl = it.toString()
                        Log.d(TAG, "uploadLectureToStorage: $videoProgressUrl")


                    }
                }.await()

            }
            withContext(Dispatchers.Main) {
                dialog.dismiss()
                Toast.makeText(
                    requireContext(),
                    "Video Uploaded Successfully",
                    Toast.LENGTH_SHORT
                )
                    .show()
//                findNavController().navigate(R.id.action_gameProgressVideoUpload_to_progressVideoFragment)
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.PICK_VIDEO && resultCode == RESULT_OK
        ) {
            videoUri = data?.data

            data?.data?.let {
                videoProgressUri = it

                Toast.makeText(
                    requireContext(),
                    "Your video is Ready to upload",
                    Toast.LENGTH_SHORT
                ).show()
            }





        }

        if (requestCode == REQUEST_CODE_IMAGE_PICK && resultCode == RESULT_OK) {

            if (data?.clipData != null) {
                var count = data?.clipData!!.itemCount
                for (i in 0 until count) {

                    val imageUri = data.clipData!!.getItemAt(i).uri
                    imageUriList.add(imageUri)
                }

                imageSwitcher.setImageURI(imageUriList[0])
                positionOfImage = 0


            } else {
                val imageUri = data?.data
                imageUriList.add(imageUri!!)

                imageSwitcher.setImageURI(imageUriList[0])
                positionOfImage = 0


            }

        }


    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

}