package com.pasosync.pasosynccoach.ui.fragments

import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.onNavDestinationSelected
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.data.CoachDetails
import com.pasosync.pasosynccoach.data.FreeSubscribedCoachEmail
import com.pasosync.pasosynccoach.data.UserDetails

import kotlinx.android.synthetic.main.coach_description.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class CoachDescriptionFragment : Fragment(R.layout.coach_description) {

    val args: CoachDescriptionFragmentArgs by navArgs()
    lateinit var mediaController: MediaController
    private val db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser
    var emailList = arrayListOf<String>()
    var freeEmailList = arrayListOf<String>()
    lateinit var builder: AlertDialog.Builder
    var free_count: Long = 0
    lateinit var dialog: AlertDialog
    private val coachDetailsCollectionRef =
        db.collection("UserDetails").document(user?.uid!!)
    private val coachCollectRef = db.collection("UserDetails")
    private val userCollectRef = db.collection("UserDetails")


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.coach_description, container, false)
        setHasOptionsMenu(true)

        return v
    }


    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false) // if you want user to wait for some process to finish,
        builder.setView(R.layout.layout_loading_dialog)
        dialog = builder.create()

        //  confirmInput()
        freeConfirmInput(args.coach.uid, requireContext())


//        if (freeConfirmInput(args.coach.coachEmail, requireContext())){
//            btn_subs_free.text="Followed"
//            Log.d(TAG, "onViewCreated:execute ")
//
//
//
//        }else{
//            btn_subs_free.text="Follow now"
//            Log.d(TAG, "onViewCreated: not execute")
//        }

        (activity as AppCompatActivity).supportActionBar?.title = " "

        collapseCoachName.isTitleEnabled = true
        collapseCoachName.title = args.coach.userName
        collapseCoachName.setCollapsedTitleTextAppearance(R.style.Textview)

        //collapseCoachName.setCollapsedTitleTextColor(resources.getColor(R.color.black))
        // collapseCoachName.setExpandedTitleTextColor(ColorStateList.valueOf(resources.getColor(R.color.white)))

        val coach = args.coach
        mediaController = MediaController(requireContext())
        coach_description_tv_name.text = "About"
        coach_description_tv_description.text = coach.userAbout

        val videoUrl = coach.introVideoUri?.toUri()

        if (args.coach.uid == "BuQGkp4HxZUTg9mVRenCfeay1aj1") {
            btn_subs_free.visibility = View.INVISIBLE


        } else {
            btn_subs_free.visibility = View.VISIBLE

        }


        val coachFollowerCollectionRef =
            db.collection("FreeCoachSubscriberCount").document(args.coach.uid)
        val coachSubscriberCollectionRef =
            db.collection("CoachSubscriberCount").document(args.coach.uid)

//        scrollView2.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
//
//            when {
//                oldScrollY>0 -> {
//                    linearButtonHolder.visibility=View.GONE
//
//
//                }
//                scrollY<0 -> {
//                    linearButtonHolder.visibility=View.VISIBLE
//
//                }
//                else -> {
//                    linearButtonHolder.visibility=View.VISIBLE
//                }
//            }
////            if (scrollY!=oldScrollY){
////                linearButtonHolder.visibility=View.VISIBLE
////
////
////            }else{
////                linearButtonHolder.visibility=View.GONE
////
////            }
//
//
//        }


        try {
            coachFollowerCollectionRef.get().addOnSuccessListener {
                val count = it.get("free")
                tv_follower_count_description.text = "${count.toString()}    "
                // Log.d(TAG, "followers:${it.getLong("free")} ")

            }

            coachSubscriberCollectionRef.get().addOnSuccessListener {
                val count = it.get("title")
                subscriber_count_description.text = count.toString()

            }
            coachDetailsCollectionRef.get().addOnSuccessListener {
                tvExperienceDescription.text = "${args.coach.experience} yrs"
                tvCoachName.text = args.coach.userName
                tvCoachSpeciality.text = args.coach.userKind
                if (args.coach.userLevel.isNotEmpty()) {
                    tvLevelDescription.text = args.coach.userLevel
                } else {
                    tvLevelDescription.text = "No-Level"
                }

                Glide.with(requireContext()).load(args.coach.userPicUri)
                    .placeholder(R.drawable.man).into(
                        profile_pic_description
                    )
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }

        if (args.coach.introVideoUri.isEmpty()) {
            Snackbar.make(requireView(), "there is no video", Snackbar.LENGTH_LONG).show()
        } else {
            video_coach_description.visibility = View.VISIBLE
            video_coach_description.setVideoURI(videoUrl)
            video_coach_description.setMediaController(mediaController)
            mediaController.setAnchorView(video_coach_description)
            mediaController.setMediaPlayer(video_coach_description)
            this.video_coach_description.setMediaController(mediaController)
            video_coach_description.requestFocus()
            video_coach_description.start()

        }



        btn_subs_free.setOnClickListener {

            val minutes = video_coach_description.currentPosition.toLong() / 1000 / 60
            val seconds = video_coach_description.currentPosition.toLong() / 1000 % 60


            val splash: Thread = object : Thread() {
                override fun run() {
                    try {
                        sleep(1000)
                        dialog.show()
                    } catch (e: Exception) {

                    }
                }
            }
            splash.start()
            if (freeConfirmInput(args.coach.uid, requireContext())) {

                //  btn_subs_free.background=R.color.green

                Snackbar.make(
                    requireView(),
                    "Already Following",
                    Snackbar.LENGTH_SHORT
                ).show()
                dialog.dismiss()

            } else {
                //  btn_subs_free.text="Follow now"
                val bundle = Bundle().apply {
                    val id = args.coach.uid
                    val name = args.coach.userName
                    val about = args.coach.userAbout
                    val email = args.coach.userEmail
                    val mobile = args.coach.userMobile
                    val pic = args.coach.userPicUri
                    val video = args.coach.introVideoUri
                    val type = args.coach.userKind
                    val experience = args.coach.experience
                    val exploreCoachList = UserDetails(
                        uid = id
                    )
                    putSerializable("article", exploreCoachList)


                    val subscribedCoachCollectionRef =
                        db.collection("UserDetails").document(user?.uid!!)
                            .collection("CoachFollowed")
                    val data = subscribedCoachCollectionRef.add(exploreCoachList)
                    val freeSubscriberCount = db.collection("CoachFollowersCount")
                        .document(id).get().addOnSuccessListener {
                            val count = it.get("followers")
                            free_count = count as Long
//                            tv_follower_count_description.text=free_count as CharSequence
                            val count_f: HashMap<String, Any> = HashMap()
                            count_f["followers"] = free_count + 1
                            val freeAddCountRef = db.collection("CoachFollowersCount")
                                .document(id).set(count_f)

                        }
                    // retrieveUserDetails()
                    toggleFollowForUser(args.coach.uid, user?.uid!!)
                    Toast.makeText(
                        requireContext(),
                        "Now you are connected to ${args.coach.userName}",
                        Toast.LENGTH_SHORT
                    ).show()


                }


            }
        }

    }


    private fun toggleFollowForUser(coachUid: String, email: String): Boolean {
        var isFollowing = false
        CoroutineScope(Dispatchers.Main).launch {

            try {

                db.runTransaction { transaction ->
                    val emailID = user
                    val currentUser = transaction.get(coachCollectRef.document(email))
                        .toObject(UserDetails::class.java)!!
                    isFollowing = coachUid in currentUser.followsCoaches
                    val newFollows =
                        if (isFollowing) currentUser.followsCoaches - coachUid
                        else currentUser.followsCoaches + coachUid
                    transaction.update(
                        coachCollectRef.document(user?.uid!!),
                        "followsCoaches",
                        newFollows
                    )


                }.await()
                isFollowing = !isFollowing


            } catch (e: Exception) {
                // Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                Log.d("FOLLOWEDTOGGLE", "toggleFollowForUser:${e.message} ")
            }
        }
        return isFollowing


    }


    private fun freeConfirmInput(email: String, context: Context): Boolean {
        if (!checkingFree(email, context)) {
            return false
        }
        return true
    }

    private fun checkingFree(email: String, context: Context): Boolean {
        val freeEmail: String? = ""
        var refer = db.collection("UserDetails").document(user?.uid!!)
            .collection("CoachFollowed").whereEqualTo("uid", email)
            .get()
            .addOnSuccessListener {
//
                for (document in it.documents) {
                    val subscribedCoachEmail = FreeSubscribedCoachEmail(
                        document.getString("uid")
                    )
                    freeEmailList.add(document.get("uid") as String)
                    // Log.d(TAG, "avoidDuplicateEntries: ${freeEmailList.size}")
                    Log.d("list", "checkingFree: ${freeEmailList.size}")
                }


            }.addOnFailureListener {
                // Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                Log.d("FOLLOWED", "checkingFree:${it.message} ")
            }
        return freeEmailList.contains(email)
    }


    private fun retrieveUserDetails() = CoroutineScope(Dispatchers.IO).launch {

        try {
            var profile_name: String? = null
            var profile_email: String? = null
            var profile_mobile: String? = null
            var profile_age: String? = null
            var userpic: String? = null
            val querySnapshot = coachDetailsCollectionRef.get().addOnSuccessListener { document ->
                val age = document.getString("userAge")
                val id = document.getString("uid")
                val email = document.getString("userEmail")
                val mobile = document.getString("userMobile")
                val profileName = document.getString("userName")
                val profilepic = document.getString("userPicUri")
                //  val profileUserPic = document.getString("userPicUri")
                profile_name = profileName

                profile_age = age
                profile_mobile = mobile
                profile_email = user?.email
                userpic = profilepic
                val userDetails = id?.let {
                    UserDetails(
                        uid = it
                    )
                }
                val userDetailsToCoach = db.collection("CoachLectureList")
                    .document(args.coach.uid).collection("FreeSubscribedUsers")
                val userdata = userDetails?.let { userDetailsToCoach.add(it) }
            }.await()
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
            }


        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }


    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

}