package com.pasosync.pasosynccoach.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
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
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.databinding.FragmentProfileBinding
import com.pasosync.pasosynccoach.ui.LoginActivity
import com.pasosync.pasosynccoach.ui.MainActivity
import com.pasosync.pasosynccoach.ui.viewmodels.MainViewModels
import dagger.hilt.android.AndroidEntryPoint


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


/**
 * A simple [Fragment] subclass.
 */

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private lateinit var binding: FragmentProfileBinding
    lateinit var viewModel: MainViewModels
    private val TAG = "ProfileFragment"
    lateinit var auth: FirebaseAuth
    lateinit var mAuthListener: AuthStateListener
    lateinit var mediaController: MediaController
    val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()
    private val coachDetailsCollectionRef =
        db.collection("CoachDetails").document(user?.email.toString())


    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(mAuthListener)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FragmentProfileBinding.bind(view)
        viewModel = (activity as MainActivity).viewmodel
        auth = getInstance()
        (activity as AppCompatActivity).supportActionBar?.title = " "
        binding.circularProfile.setImageResource(R.drawable.ic_baseline_person_pin_24)
        binding.tvProfileName.text = ""
        binding.tvProfileEmail.text = ""
        binding.tvProfileMobile.text = ""
        binding.tvAboutProfile.text = ""
        binding.coachExperience.text=""
        binding.tvCoachSpeciality.text=""

        binding.btProfileUpload.setOnClickListener {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.profileFragment2, true).build()
            findNavController().navigate(
                R.id.action_profileFragment2_to_updateProfileFragment2,
                savedInstanceState,
                navOptions

            )
        }
        binding.btEarnings.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment2_to_earningsFragment2)
        }
//        userEmail="shubhamver61002"
//        val user=FirebaseAuth.getInstance().currentUser
//        user.let {
//            userEmail= user?.email.toString()
//        }
        mAuthListener = AuthStateListener {
            /*NO-OP*/
        }
        retrieveCoachDetails()
        //  getListenAuthOfUser()

        binding.btLogout.setOnClickListener {

            getInstance().signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        mediaController = MediaController(requireContext())
//     0


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
            var experience:String?=null
            var coachType:String?=null

            val querySnapshot = coachDetailsCollectionRef.get().addOnSuccessListener { document ->
                val about = document.getString("coachAbout")
                val email = document.getString("coachEmail")
                val mobile = document.getString("coachMobile")
                val profileName = document.getString("coachName")
                val profileUserPic = document.getString("coachProfilePicUri")
                val profileVideo = document.getString("coachIntroVideoUri")
                val exp=document.getString("coachExperience")
                val type=document.getString("coachType")
                profile_name = profileName
                profile_about = about
                profile_mobile = mobile
                profile_email = email
                profile_user_pic = profileUserPic
                profile_user_video = profileVideo
                experience= "$exp yrs"
                coachType=type
                Log.d(TAG, "retrieveCoachDetails:$profile_email ")

            }.await()

            withContext(Dispatchers.Main) {
                binding.tvProfileName.text = profile_name
                binding.tvProfileEmail.text = profile_email
                binding.tvAboutProfile.text = profile_about
                binding.tvProfileMobile.text = profile_mobile
                binding.tvCoachSpeciality.text=coachType
                binding.coachExperience.text=experience

                Glide.with(requireContext()).load(profile_user_pic)
                    .placeholder(R.drawable.ic_baseline_person_24).fitCenter().centerCrop()
                    .into(binding.circularProfile)
                if (profile_user_video == null) {
                    Snackbar.make(requireView(), "there is no video", Snackbar.LENGTH_LONG).show()
                } else {
                    binding.profileVideoView.visibility = View.VISIBLE
                    binding.profileVideoView.setVideoURI(profile_user_video?.toUri())
                    binding.profileVideoView.setMediaController(mediaController)
                    mediaController.setAnchorView(binding.profileVideoView)
                    mediaController.setMediaPlayer(binding.profileVideoView)
                    binding.profileVideoView.requestFocus()
                    binding.profileVideoView.start()


                }


            }


        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
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
