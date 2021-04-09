package com.pasosync.pasosynccoach.ui.fragments


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.adapters.SpinnerAdapter
import com.pasosync.pasosynccoach.data.Post
import com.pasosync.pasosynccoach.data.SpinnerItem
import com.pasosync.pasosynccoach.data.Types
import com.pasosync.pasosynccoach.other.Constant
import com.pasosync.pasosynccoach.other.Permissions

import kotlinx.android.synthetic.main.fragment_new_post_upload.*
import kotlinx.android.synthetic.main.post_toolbar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.*




class NewPostUploadFragment : Fragment(R.layout.fragment_new_post_upload),
    AdapterView.OnItemSelectedListener {
    private val auth = FirebaseAuth.getInstance()
    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog

    //private val viewModel:NewPostUploadViewModel by viewModels()
    private val firestore = FirebaseFirestore.getInstance()

    //private lateinit var cropContent: ActivityResultLauncher<String>
    private val posts = firestore.collection("Posts")
    private val postsCoach = firestore.collection("CoachLectureList")
        .document(auth.currentUser?.uid!!).collection("Post")
    var imageurlPost: String? = null


    var coachPic: String? = null
    var spinnerText = ""
    var newPostImageUri: Uri? = null

    val coachNewPostStorageReference = Firebase.storage.reference.child("CoachNewPostData")
    var newPostImageurl: String =""
    private val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()
    private val coachDetailsCollectionRef =
        db.collection("UserDetails").document(user?.uid!!)


    private var curImageUri: Uri? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        retrieveCoachDetails()
        setUpCustomSpinner()
        spinnerUploadingNewPost.onItemSelectedListener = this
        builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false)
        builder.setView(R.layout.layout_uploading_dialog)
        dialog = builder.create()

//        ivOpenCamera.setOnClickListener {
//            openCamera()
//        }
        tvOpenGallery.setOnClickListener {
            chooseImageForLecture()
        }

        //subscribeToObservers()

        ivOpenCamera.setOnClickListener {
            //chooseImageForLecture()
        }


//        ivPostImage.setOnClickListener {
//            cropContent.launch("image/*")
//        }
        tvUploadPost.setOnClickListener {
            if (confirmInput()) {
                uploadPostToFirestore()
            }

        }


    }


    private fun validateText(): Boolean {
        val email: String = etAddContentNewPost.text.toString().trim()
        return if (email.isEmpty()) {
            add_content_input.error = "Field can't be empty"
            dialog.dismiss()
            false
        } else {
            add_content_input.error = null
            true
        }
    }

    private fun confirmInput(): Boolean {
        if (!validateText()) {
            return false
        }
        return true
    }


    private fun uploadPostToFirestore() = CoroutineScope(Dispatchers.Main).launch {
        val description = etAddContentNewPost.text.toString()
        val timestamp = System.currentTimeMillis().toString()
        val uid = user?.uid!!
        val url = newPostImageurl

        val id = UUID.randomUUID().toString()
        val postCoaches = Post(
            id,
            uid!!,
            tvNewPostProfileName.text.toString(),
            coachPic!!,
            description,
            url!!,
            spinnerText,
            timestamp.toLong(),
            false,
            false,
            listOf(),
            user?.uid!!
        )
        val post = Post(
            id = id,
            authorUid = uid!!,
            text = description,
            imageUrl = url!!,
            type = spinnerText,
            coachEmail = user?.uid!!,
            date = timestamp.toLong()
        )

        val data = posts.document(id).set(post)
        val dataCoach = postsCoach.document(id).set(postCoaches)

        Snackbar.make(requireView(), "Uploaded successfully", Snackbar.LENGTH_SHORT)
            .show()
        findNavController().navigate(R.id.action_newPostUploadFragment_to_dashBoardFragment)


    }


//    private fun subscribeToObservers() {
//
//        viewModel.curImageUri.observe(viewLifecycleOwner) {
//            curImageUri = it
//           // btnSetPostImage.isVisible = false
//            Glide.with(requireContext()).load(curImageUri).into(ivNewPostUpload)
//           // glide.load(curImageUri).into(ivPostImage)
//        }
//
//        viewModel.createPostStatus.observe(viewLifecycleOwner, Event.EventObserver(
//            onError = {
//               // createPostProgressBar.isVisible = false
//                snackbar(it)
//            },
//            onLoading = { //createPostProgressBar.isVisible = true }
//            }
//        ) {
//           // createPostProgressBar.isVisible = false
//            findNavController().popBackStack()
//        })
//    }


    private fun chooseImageForLecture() {
        Intent(Intent.ACTION_GET_CONTENT).also {
            it.type = "image/*"
            startActivityForResult(it, Constant.REQUEST_CODE_IMAGE_PICK)
        }
    }


    private fun setUpCustomSpinner() {
        val adapter = SpinnerAdapter(requireContext(), Types.list!!)

        spinnerUploadingNewPost.adapter = adapter
    }


    private fun openCamera() {
        if (Permissions.hasWritingPermissions(requireContext())) {

            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
                startActivityForResult(it, Constant.REQUEST_CODE_OPEN_CAMERA)
            }


        } else {
            Toast.makeText(
                requireContext(),
                "Make sure you accept the permissions",
                Toast.LENGTH_SHORT
            ).show()
        }


    }

    private fun retrieveCoachDetails() = CoroutineScope(Dispatchers.Main).launch {

        try {
            val retrieve = coachDetailsCollectionRef.get().addOnSuccessListener {
                val pic = it.getString("userPicUri")
                val name = it.getString("userName")
                tvNewPostProfileName.text = name
                coachPic = pic
                Glide.with(requireContext()).load(pic).into(new_post_circular_profile)

            }


        } catch (e: Exception) {
        }


    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_new_post_upload, container, false)
        setHasOptionsMenu(true)
        return v

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }


    private fun uploadImageToStorage() = CoroutineScope(Dispatchers.IO).launch {
        try {


            newPostImageUri?.let {
                val imageFileName =
                    coachNewPostStorageReference.child("Image${System.currentTimeMillis()}" + newPostImageUri?.lastPathSegment)
                imageFileName.putFile(it).addOnProgressListener {
                    dialog.show()

                }.addOnSuccessListener { taskSnapshot ->
                    imageFileName.downloadUrl.addOnSuccessListener {
                        newPostImageurl = it.toString()

                        // Log.d(TAG, "uploadLectureDataToStorage: $imageurlLecture")
                    }
                }.await()
            }
            withContext(Dispatchers.Main) {
                dialog.dismiss()
                Toast.makeText(
                    requireContext(),
                    "Image Uploaded Successfully",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_OPEN_CAMERA) {
//            data?.data?.let {
//                newPostImageUri=it
//
//                Log.d(TAG, "onActivityResult: $newPostImageUri")
//              //  new_post_image.setImageResource(R.drawable.imageafterupload)
//          ivNewPostUpload.setImageURI(newPostImageUri)
//            }

//            val bmp=data?.extras.let {
//                it!!.get("data")
//                ivNewPostUpload.setImageBitmap(it as Bitmap)
//            }

            val photo = data!!.extras!!.get("data") as Bitmap

            ivNewPostUpload.setImageBitmap(photo)
//            newPostImageUri = getImageUri(requireContext(), photo)
//            ivNewPostUpload.setImageURI(newPostImageUri)


        }

        if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_IMAGE_PICK) {
            data?.data?.let {
                newPostImageUri = it


                //  new_post_image.setImageResource(R.drawable.imageafterupload)
                ivNewPostUpload.setImageURI(newPostImageUri)
                uploadImageToStorage()

            }
        }


    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            System.currentTimeMillis().toString(),
            null
        )
        return Uri.parse(path)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        val text = parent!!.getItemAtPosition(position).toString()

        val clickedItem: SpinnerItem = parent.getItemAtPosition(position) as SpinnerItem
        val clickedName: String = clickedItem.TypeName

        spinnerText = clickedName



    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }


}