package com.pasosync.pasosynccoach.ui.fragments

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.AdapterView
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.adapters.SpinnerCoachAdapter
import com.pasosync.pasosynccoach.data.CoachDetails
import com.pasosync.pasosynccoach.data.SpinnerItemForCoachDetails
import com.pasosync.pasosynccoach.data.TypesCoach
import com.pasosync.pasosynccoach.databinding.FargmentUpdateprofileBinding
import com.pasosync.pasosynccoach.other.Constant
import com.pasosync.pasosynccoach.other.Constant.PICK_VIDEO
import com.pasosync.pasosynccoach.other.Constant.REQUEST_CODE_IMAGE_PICK
import com.pasosync.pasosynccoach.other.Permissions
import com.pasosync.pasosynccoach.ui.MainActivity
import com.pasosync.pasosynccoach.ui.viewmodels.MainViewModels
import kotlinx.android.synthetic.main.activity_main.*

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


/**
 * A simple [Fragment] subclass.
 */

class UpdateProfileFragment : Fragment(R.layout.fargment_updateprofile), AdapterView.OnItemSelectedListener {
    private lateinit var binding: FargmentUpdateprofileBinding

    private val TAG: String = "UpdateProfile"
    lateinit var viewModel: MainViewModels
    private var userEmail: String = "e"
    lateinit var mediaController: MediaController
    val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()
    var spinnerText = ""
    private val coachDetailsCollectionRef =
        db.collection("CoachDetails").document(user?.email.toString())


    val UserIntro = Firebase.storage.reference.child("userIntroData")
    var userPicUri: Uri? = null
    var userVideoUri: Uri? = null
    var videourl: String? = null
    var picurl: String? = null
    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fargment_updateprofile, container, false)
        setHasOptionsMenu(true)
        val user = FirebaseAuth.getInstance().currentUser
        user.let {
            userEmail = user?.email.toString()
            Log.d(TAG, "onCreateView: $userEmail")
        }
        return v
    }

    private fun setUpCustomSpinner() {
        val adapter = SpinnerCoachAdapter(requireContext(), TypesCoach.list!!)

        binding.spinnerUpdateProfile.adapter=adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FargmentUpdateprofileBinding.bind(view)
        (toolbar as Toolbar?)?.setTitleTextColor(Color.WHITE)
        viewModel = (activity as MainActivity).viewmodel

        (activity as AppCompatActivity).supportActionBar?.title = " "
        binding.updateChooseBtn.setOnClickListener {
            if (userPicUri == null) {
                Toast.makeText(requireContext(), "Please Select an Image", Toast.LENGTH_SHORT)
                    .show()
            } else {
                dialog.show()
                uploadImageToStorage()
            }

        }
        binding.updateEmail.isEnabled = false

        setUpCustomSpinner()
        binding.spinnerUpdateProfile.onItemSelectedListener = this

        retrieveCoachDetailsInUpdate()
        mediaController = MediaController(requireContext())
        // update_video_view.setMediaController(mediaController)
        //  mediaController.setAnchorView(update_video_view)
        //  mediaController.setMediaPlayer(update_video_view)
        // this.update_video_view.setMediaController(mediaController)
        // update_video_view.requestFocus()

        // update_video_view.start()

        binding.updateCategory.setOnClickListener {
            dialog.show()
            uploadTypeToFirestore()


        }

        binding.updateProfileVideoPic.setOnClickListener {
            if (Permissions.hasWritingPermissions(requireContext())) {
                chooseVideoForNewPost()

            } else {
                Toast.makeText(
                    requireContext(),
                    "You need to accept the permissions",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }


        binding.updateProfile.setOnClickListener {
            if (confirmInput() or Permissions.hasWritingPermissions(requireContext())) {
                dialog.show()
//                uploadImageToStorage()
                uploadDataToFirestor()


            } else {
                Toast.makeText(requireContext(), "fill all details", Toast.LENGTH_SHORT).show()
            }


            //updateCoachDetails(getOldCoachDetails(),newCoachMap)


        }

        binding.updateProfilePic.setOnClickListener {
            picImageFromGallery()
        }



        binding.updateVideo.setOnClickListener {
            if (userVideoUri == null) {
                Toast.makeText(requireContext(), "Please Select a video", Toast.LENGTH_SHORT)
                    .show()
            } else {
                dialog.show()
                uploadVideoToStorage()
            }


        }

        builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false) // if you want user to wait for some process to finish,

        builder.setView(R.layout.layout_uploading_dialog)
        dialog = builder.create()

    }


    private fun validateEmail(): Boolean {
        val email: String = binding.updateEmailInput.editText?.text.toString().trim()
        return if (email.isEmpty()) {
            binding.updateEmailInput.error = "Field can't be empty"
            dialog.dismiss()
            false
        } else {
            binding.updateEmailInput.error = null
            true
        }
    }

    private fun validateName(): Boolean {
        val name: String = binding.updateNameInput.editText?.text.toString().trim()
        return if (name.isEmpty()) {
            binding.updateNameInput.error = "Field can't be empty"
            dialog.dismiss()
            false
        } else {
            binding.updateNameInput.error = null
            true
        }
    }

    private fun validateExperience(): Boolean {
        val experience: String = binding.updateExperienceInput.editText?.text.toString().trim()
        return if (experience.isEmpty()) {
            binding.updateExperienceInput.error = "Field can't be empty"
            dialog.dismiss()
            false
        } else {
            binding.updateExperienceInput.error = null
            true
        }
    }

    private fun validateMobile(): Boolean {
        val mobile: String = binding.updateMobileInput.editText?.text.toString().trim()
        return if (mobile.isEmpty() || mobile.length > 10 || mobile.length < 10) {
            binding.updateMobileInput.error = "invalid input"
            dialog.dismiss()
            false
        } else {
            binding.updateMobileInput.error = null
            true
        }
    }

    private fun validateAbout(): Boolean {
        val about: String = binding.updateAboutInput.editText?.text.toString().trim()
        return if (about.isEmpty()) {
            binding.updateAboutInput.error = "Field can't be empty"
            dialog.dismiss()
            false
        } else {
            binding.updateAboutInput.error = null
            true
        }
    }

    private fun confirmInput(): Boolean {
        if (!validateEmail() or !validateName() or !validateMobile() or !validateAbout() or !validateExperience()) {
            return false
        }
        return true
        dialog.show()
    }


    private fun chooseVideo(view: View?) {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_VIDEO)
    }


    private fun picImageFromGallery() {
        Intent(Intent.ACTION_GET_CONTENT).also {
            it.type = "image/*"
            startActivityForResult(it, REQUEST_CODE_IMAGE_PICK)
        }
    }

    private fun uploadVideoToStorage() = CoroutineScope(Dispatchers.IO).launch {
        try {
            userVideoUri?.let {
                val videoFileName =
                    UserIntro.child("Video${System.currentTimeMillis()}" + userVideoUri?.lastPathSegment)
                videoFileName.putFile(it).addOnProgressListener {
                    dialog.show()

                }.addOnSuccessListener { taskSnapshot ->
                    videoFileName.downloadUrl.addOnSuccessListener {
                        videourl = it.toString()
                        Log.d(TAG, "uploadVideoToStorage: $videourl")
                        coachDetailsCollectionRef.update("coachIntroVideoUri", videourl)
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
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }


    private fun uploadImageToStorage() = CoroutineScope(Dispatchers.IO).launch {
        try {
            userPicUri?.let {

                val filename = UserIntro.child("file" + userPicUri?.lastPathSegment)


                filename.putFile(it).addOnProgressListener { snapshot ->

                    val progress: Double =
                        (100.0 * snapshot.bytesTransferred / snapshot.totalByteCount)
                    binding.updateProgressBar.progress = progress.toInt()

                }.addOnSuccessListener { taskSnapshot ->
                    val handler = Handler()
                    handler.postDelayed({ binding.updateProgressBar.setProgress(0) }, 500)
                    filename.downloadUrl.addOnSuccessListener {
                        picurl = it.toString()
                        Log.d(TAG, "uploadImageToStorage: ${it.toString()}")
                        coachDetailsCollectionRef.update("coachProfilePicUri", picurl)

                    }

                }.await()
                withContext(Dispatchers.Main) {

                    dialog.dismiss()
                    Toast.makeText(
                        requireContext(),
                        "Profile pic Uploaded Successfully",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun chooseVideoForNewPost() {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_VIDEO)
    }

    private fun uploadTypeToFirestore()=CoroutineScope(Dispatchers.Main).launch {
        try{
            var coachType=spinnerText
            coachDetailsCollectionRef.update("coachType",coachType)
             Toast.makeText(requireContext(),"Changed Successfully",Toast.LENGTH_SHORT).show()
            dialog.dismiss()

        }catch (e:Exception){
             Toast.makeText(requireContext(),e.message,Toast.LENGTH_SHORT).show()
        }
    }


    private fun uploadDataToFirestor() = CoroutineScope(Dispatchers.Main).launch {
        try {
            var name = binding.updateName.text.toString()
            var email = binding.updateEmail.text.toString()
            var mobile = binding.updateMobile.text.toString()
            var about = binding.updateAbout.text.toString()

            var experience=binding.updateExperience.text.toString()
//            var coachVideoUri = videourl.toString()
//            var coachpicUri = picurl.toString()

//            val coachDetails =
//                CoachDetails(name, email, mobile, about, coachpicUri, coachVideoUri)

            coachDetailsCollectionRef.update("coachName", name)
            coachDetailsCollectionRef.update("coachEmail", email)
            coachDetailsCollectionRef.update("coachMobile", mobile)
            coachDetailsCollectionRef.update("coachAbout", about)

            coachDetailsCollectionRef.update("coachExperience",experience)


//            saveCoachDetails(coachDetails)
            dialog.dismiss()
            findNavController().navigate(R.id.action_updateProfileFragment2_to_profileFragment2)


        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }


    }


    private fun uploadVideo() = CoroutineScope(Dispatchers.IO).launch {
        try {
            userVideoUri?.let {
                val videoFileName = UserIntro.child("VideoFile" + userPicUri?.lastPathSegment)
                videoFileName.putFile(it).addOnProgressListener { snapshot ->

                }.addOnSuccessListener { taskSnapshot ->
                    videoFileName.downloadUrl.addOnSuccessListener {
                        Log.d(TAG, "uploadVideo:${it.toString()} ")
                        videourl = it.toString()
                        var name = binding.updateName.text.toString()
                        var email = binding.updateEmail.text.toString()
                        var mobile = binding.updateMobile.text.toString()
                        var about = binding.updateAbout.text.toString()
                        var coachVideoUri = videourl.toString()
                        var coachpicUri = picurl.toString()
                        val coachDetails =
                            CoachDetails(name, email, mobile, about, coachpicUri, coachVideoUri)
                        saveCoachDetails(coachDetails)
                        dialog.dismiss()
                        findNavController().navigate(R.id.action_updateProfileFragment2_to_profileFragment2)


                    }
                }.await()
                withContext(Dispatchers.Main) {
                    dialog.dismiss()
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_IMAGE_PICK) {
            data?.data?.let {
                userPicUri = it
                binding.updateProfilePic.setImageURI(it)
            }
        }

        if (resultCode == Activity.RESULT_OK && requestCode == PICK_VIDEO) {
            data?.data?.let {
                userVideoUri = it
                binding.updateProfileVideoPic.setImageResource(R.drawable.checkedsmall)
                Toast.makeText(
                    requireContext(),
                    "Your video is Ready to upload",
                    Toast.LENGTH_SHORT
                ).show()
                // update_video_view.setVideoURI(userVideoUri)
            }
        }
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)


    private fun retrieveCoachDetailsInUpdate() = CoroutineScope(
        Dispatchers.IO
    ).launch {
        try {
            var profile_name: String? = null
            var profile_email: String? = null
            var profile_mobile: String? = null
            var profile_about: String? = null
            var profile_user_pic: String? = null
            var profile_user_video: String? = null
            var experience_coach:String?=null
            val querySnapshot = coachDetailsCollectionRef.get().addOnSuccessListener { document ->
                val about = document.getString("coachAbout")
                val email = document.getString("coachEmail")
                val mobile = document.getString("coachMobile")
                val profileName = document.getString("coachName")
                val profileUserPic = document.getString("coachProfilePicUri")
                val profileVideo = document.getString("coachIntroVideoUri")
                val experience=document.getString("coachExperience")
                profile_name = profileName
                profile_about = about
                profile_mobile = mobile
                profile_email = user?.email
                profile_user_pic = profileUserPic
                profile_user_video = profileVideo
                experience_coach=experience
            }.await()
            withContext(Dispatchers.Main) {
                binding.updateName.text = profile_name?.toEditable()
                binding.updateEmail.text = profile_email?.toEditable()
                binding.updateAbout.text = profile_about?.toEditable()
                binding.updateMobile.text = profile_mobile?.toEditable()
                binding.updateExperience.text=experience_coach?.toEditable()
                // update_video_view.setVideoURI(profile_user_video?.toUri())
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }

        }


    }


    private fun saveCoachDetails(coachDetails: CoachDetails) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                coachDetailsCollectionRef.set(coachDetails).await()
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "data uploaded", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                }

            }

        }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val clickedItem: SpinnerItemForCoachDetails = parent?.getItemAtPosition(position) as SpinnerItemForCoachDetails
        val clickedCountryName: String =clickedItem.TypeName

        spinnerText = clickedCountryName
        Log.d(TAG, "onItemSelected: ${spinnerText}")



    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

}
