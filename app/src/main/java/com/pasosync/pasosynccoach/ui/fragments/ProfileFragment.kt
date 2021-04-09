package com.pasosync.pasosynccoach.ui.fragments


import android.R.attr
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseAuth.getInstance
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.databinding.FragmentProfileBinding
import com.pasosync.pasosynccoach.other.Constant
import com.pasosync.pasosynccoach.ui.LoginActivity
import com.pasosync.pasosynccoach.ui.MainActivity
import com.pasosync.pasosynccoach.ui.viewmodels.MainViewModels
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImage.activity
import com.theartofdev.edmodo.cropper.CropImage.getActivityResult
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


/**
 * A simple [Fragment] subclass.
 */


private const val TAG = "ProfileFragment"
class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private lateinit var binding: FragmentProfileBinding
    lateinit var viewModel: MainViewModels

    lateinit var auth: FirebaseAuth
    var userPicUri: Uri? = null
    var picurl: String? = null
    lateinit var mAuthListener: AuthStateListener
    lateinit var mediaController: MediaController
    val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()
    private val coachDetailsCollectionRef =
        db.collection("UserDetails").document(user?.uid!!)
    val UserIntro = Firebase.storage.reference.child("userIntroData")
    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog

    private lateinit var cropContent: ActivityResultLauncher<Any?>


    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri?>() {
        override fun createIntent(context: Context, input: Any?): Intent {
            return activity()
                .setAspectRatio(4, 3)
                .setGuidelines(CropImageView.Guidelines.ON)
                .getIntent(requireContext())
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri
        }
    }


    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(mAuthListener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)
        viewModel = (activity as MainActivity).viewmodel
        auth = getInstance()
        (activity as AppCompatActivity).supportActionBar?.title = " "
        binding.profilePic.setImageResource(R.drawable.man)
        binding.profileTvName.text = ""
        binding.profileTvKind.text = ""
        binding.profileTvLevel.text = "level-3"
        binding.profileTvExperience.text = ""
        binding.aboutUser.text = ""

        builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false) // if you want user to wait for some process to finish,

        builder.setView(R.layout.layout_uploading_dialog)
        dialog = builder.create()
        profile_tv_Level_update.setOnClickListener {
            Log.d(TAG, "onViewCreated: Clicked")
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.profileFragment2, true).build()

            findNavController().navigate(R.id.action_profileFragment2_to_fragmentEditYourLevel
                ,savedInstanceState,navOptions)
        }


        binding.profileTvEditProfileText.setOnClickListener {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.profileFragment2, true).build()
            findNavController().navigate(
                R.id.action_profileFragment2_to_updateProfileFragment2,
                savedInstanceState,
                navOptions

            )
        }
        binding.profileTvEarnings.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment2_to_earningsFragment2)
        }
        binding.profileTvSpecialityUpdate.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment2_to_fragmentEditYourSpeciality)
        }


        mAuthListener = AuthStateListener {
            /*NO-OP*/
        }
        retrieveCoachDetails()


        binding.profilePic.setOnClickListener {
            CropImage.activity().setAspectRatio(4, 3)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(requireContext(), this);
            //picImageFromGallery()

        }


        binding.btLogout.setOnClickListener {

            getInstance().signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        mediaController = MediaController(requireContext())
//     0


    }

    private fun picImageFromGallery() {
        Intent(Intent.ACTION_GET_CONTENT).also {
            it.type = "image/*"
            startActivityForResult(it, Constant.REQUEST_CODE_IMAGE_PICK)
        }
    }

    private fun uploadImageToStorage() = CoroutineScope(Dispatchers.IO).launch {
        try {
            userPicUri?.let {

                val filename = UserIntro.child("file" + userPicUri?.lastPathSegment)


                filename.putFile(it).addOnProgressListener { snapshot ->

                    val progress: Double =
                        (100.0 * snapshot.bytesTransferred / snapshot.totalByteCount)
                    //   binding.updateProgressBar.progress = progress.toInt()

                }.addOnSuccessListener { taskSnapshot ->
                    val handler = Handler()
                    //  handler.postDelayed({ binding.updateProgressBar.setProgress(0) }, 500)
                    filename.downloadUrl.addOnSuccessListener {
                        picurl = it.toString()

                        coachDetailsCollectionRef.update("userPicUri", picurl)

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_profile, container, false)
        setHasOptionsMenu(true)
        return v

    }

    private fun retrieveCoachDetails() = CoroutineScope(
        Dispatchers.IO
    ).launch {
        try {
            var profile_name: String? = null
            var profile_email: String? = null
            var profile_mobile: String? = null
            var profile_about: String? = null
            var profile_user_pic: String? = null
            var profile_user_video: String? = null
            var experience: String? = null
            var coachType: String? = null
            var coachlevel: String? = null

            val querySnapshot = coachDetailsCollectionRef.get().addOnSuccessListener { document ->
                val about = document.getString("userAbout")
                val email = document.getString("userEmail")
                val mobile = document.getString("userMobile")
                val profileName = document.getString("userName")
                val profileUserPic = document.getString("userPicUri")
                val profileVideo = document.getString("introVideoUri")
                val exp = document.getString("experience")
                val type = document.getString("userKind")
                val level=document.getString("userLevel")
                profile_name = profileName
                profile_about = about
                profile_mobile = mobile
                profile_email = email
                profile_user_pic = profileUserPic
                profile_user_video = profileVideo
                experience = "$exp yrs of experience"
                coachType = type
                coachlevel=level


            }.await()

            withContext(Dispatchers.Main) {
                binding.profileTvName.text = profile_name
                // binding.tvProfileEmail.text = profile_email
                binding.aboutUser.text = profile_about
                //  binding.tvProfileMobile.text = profile_mobile
                binding.profileTvKind.text = coachType
                binding.profileTvExperience.text = experience
                binding.profileTvLevel.text=coachlevel

                Glide.with(requireContext()).load(profile_user_pic)
                    .placeholder(R.drawable.ic_baseline_person_24).fitCenter().centerCrop()
                    .into(binding.profilePic)
                if (profile_user_video?.isEmpty()!!) {
                  //  Snackbar.make(requireView(), "there is no video", Snackbar.LENGTH_LONG).show()
                } else {
                    binding.profileVideoView.visibility = View.VISIBLE
                   // binding.profileVideoView.setVideoURI(profile_user_video?.toUri())
                  //  binding.profileVideoView.setMediaController(mediaController)
                    mediaController.setAnchorView(binding.profileVideoView)
                  //  mediaController.setMediaPlayer(binding.profileVideoView)
                    binding.profileVideoView.requestFocus()
                   // binding.profileVideoView.start()
                    binding.profileVideoView.setSource(profile_user_video)

                }


            }


        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }

        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = getActivityResult(data)
            result?.let {
                dialog.show()

                userPicUri = it.uri
                uploadImageToStorage()
            }
        }


    }


    override fun onStop() {
        super.onStop()
        if (mAuthListener == null) {
            auth.removeAuthStateListener(mAuthListener)
            getInstance().signOut()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

}
