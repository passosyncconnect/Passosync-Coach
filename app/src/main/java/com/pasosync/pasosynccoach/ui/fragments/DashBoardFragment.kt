package com.pasosync.pasosynccoach.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.adapters.*
import com.pasosync.pasosynccoach.data.NewLectureDetails
import com.pasosync.pasosynccoach.data.UserDetails
import com.pasosync.pasosynccoach.databinding.FargmentDashboardBinding
import com.pasosync.pasosynccoach.other.Constant.REQUEST_CODE_WRITING
import com.pasosync.pasosynccoach.other.Permissions
import com.pasosync.pasosynccoach.other.Resource
import com.pasosync.pasosynccoach.ui.MainActivity
import com.pasosync.pasosynccoach.ui.viewmodels.MainViewModels
import com.pasosync.pasosynccoach.other.Event
import kotlinx.android.synthetic.main.fargment_dashboard.*


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions



class DashBoardFragment : Fragment(R.layout.fargment_dashboard),
    EasyPermissions.PermissionCallbacks {

    private val KEY_TITLE = "title"
    private val KEY_FREE = "free"
    private val KEY_FOLLOWER = "followers"
    lateinit var viewModel: MainViewModels

    private var curLikedIndex: Int? = null

    // lateinit var lectureAdapter: NewPostAdapter
    private lateinit var binding: FargmentDashboardBinding
    lateinit var builder: AlertDialog.Builder
    var postAdapter = PostAdapter()
    var userList = arrayListOf<NewLectureDetails>()
    lateinit var dialog: AlertDialog
    private val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()
    private val dash = user?.uid!!
    private val lectureDetailsRef =
        db.collection("Posts")
    private val coachCollectRef = db.collection("UserDetails")


    val followerCountRef = db.collection("CoachFollowersCount").document(user?.uid!!)
 //   lateinit var newsAdapter: NewsAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fargment_dashboard, container, false)
        setHasOptionsMenu(true)
        binding = FargmentDashboardBinding.bind(v)
        //  setUpRecyclerView()
        //   setupRecyclerViewPost()

        return v
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FargmentDashboardBinding.bind(view)
        viewModel = (activity as MainActivity).viewmodel

       // setUpRecyclerViewNews()
        (activity as AppCompatActivity).supportActionBar?.title = " "

        requestPermissions()
        showConnectorsCount()

        setupRecyclerViewPost()
        subscribeToObservers()

        postAdapter.setOnLikeClickListener { post, i ->
            curLikedIndex = i
            post.isLiked = !post.isLiked
            viewModel.toggleLikeForPost(post)

        }


        postAdapter.setOnLikedByClickListener { post ->
            findNavController().navigate(
                R.id.globalLiked,
                Bundle().apply {
                    putSerializable("id", post)
                })

            // viewModel.getUsers(post.likedBy)
        }

        postAdapter.setOnCommentsClickListener { post ->
            findNavController().navigate(
                R.id.globalActionToCommentDialog,
                Bundle().apply { putString("postId", post.id) }
            )
        }


        binding.dashboardAcademyCardView.setOnClickListener {
          val counted=""
            coachCollectRef.document(user?.uid!!).addSnapshotListener { value, error ->
                try {
                    if (value!=null){
                        val pic= value?.getString("academy")
                        // Glide.with(this).load(pic).placeholder(R.drawable.man).into(profileIvimage)
                       // counted=pic!!
                        // Log.d(TAG, "onCreate:$counted")

                        val bundle=Bundle().apply {
                            putString("academy",pic)
                        }
                        findNavController().navigate(R.id.action_dashBoardFragment_to_academyMembersListFragment,bundle)
                    }
                    else{
                        //  Log.d(TAG, "onCreate:$counted")
                    }
                } catch (e: Exception) {
                    //  Log.e(TAG, "onCreate: ${error.toString()}", )
                }

            }

        }





        binding.dashboardSubscriberCardView.setOnClickListener {
            findNavController().navigate(R.id.action_dashBoardFragment_to_allCoachListFragment)
        }
        binding.linearLayoutFollowerCounter.setOnClickListener {
            findNavController().navigate(R.id.action_dashBoardFragment_to_subscribersFragment2)
        }
        binding.linearLayoutSubscriberCounter.setOnClickListener {
            findNavController().navigate(R.id.action_dashBoardFragment_to_subscribersFragment2)
        }
        viewModel.cricketNews.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                      //  newsAdapter.differ.submitList(newsResponse.articles)
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->

                    }
                }
                is Resource.Loading -> {
                   // showProgressBar()
                }
            }


        })
        //  getFollowerSubscriberCount()

        getSubscriberCount()
        getFreeSubscriberCount()
    }

    private fun getSubscriberCount() = CoroutineScope(Dispatchers.IO).launch {
        try {
            //new method for resolving null at creation stage


            val subscriberCountRef=db.collection("CoachSubscriberCount")
            var count="0"
            subscriberCountRef.document(dash).addSnapshotListener { value, error ->
                value?.let {
                    var title = value.get(KEY_TITLE).toString()

                    count = title
                    binding.tvTotalNumberOfSubscriber.text = title
                }

            }
            withContext(Dispatchers.Main) {




            }






//            val countRef = db.collection("CoachSubscriberCount").document(user?.uid!!)
//            if (countRef == null) {
//                binding.tvTotalNumberOfSubscriber.text = "0"
//            }
//            else {
//                var count = "0"
//                countRef.get().addOnSuccessListener {
//                    var title = it.get(KEY_TITLE).toString()
//                    Log.d(TAG, "getSubscriberCount: ${title}")
//                    count = title
//
//
//                }.await()
//                withContext(Dispatchers.Main) {
//                    if (count.equals(0)) {
//                        binding.tvTotalNumberOfSubscriber.text = "00"
//
//                    } else {
//                        binding.tvTotalNumberOfSubscriber.text = count
//                    }
//
//
//                }
//
//            }




        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getFreeSubscriberCount() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val freeCountRef = db.collection("FreeCoachSubscriberCount")
            var count = "0"
            freeCountRef.document(dash).addSnapshotListener { value, error ->
                value?.let {
                    var title = value.get(KEY_FREE).toString()

                    count = title
                    binding.tvTotalNumberOfStudent.text = title
                }


            }

            withContext(Dispatchers.Main) {

            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }



    private fun getFollowerSubscriberCount() = CoroutineScope(Dispatchers.IO).launch {
        try {



                var count = "0"
                followerCountRef.get().addOnSuccessListener {
                    var title = it.get(KEY_FOLLOWER).toString()

                    count = title


                }.await()
                withContext(Dispatchers.Main) {
                    if (count.equals(0)) {
                        binding.tvTotalNumberOfCoachSubscriber.text = "00"

                    } else {
                        binding.tvTotalNumberOfCoachSubscriber.text = count
                    }


                }



        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }


    private fun requestPermissions() {
        if (Permissions.hasWritingPermissions(requireContext())) {
            return
        } else {
            EasyPermissions.requestPermissions(
                this,
                "These permissions are must to use this App",
                REQUEST_CODE_WRITING,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA
            )
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun hideProgressBar() {
      //  binding.paginationProgressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
      //  binding.paginationProgressBar.visibility = View.VISIBLE
    }

//    private fun setUpRecyclerViewNews() = binding.newsSliderDashboard.apply {
//        newsAdapter = NewsAdapter()
//        adapter = newsAdapter
//        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//        newsAdapter.setOnItemClickListener {
//            Log.e("Nee=w=", "getting click")
//            val bundle = Bundle().apply {
//                putSerializable("article", it)
//            }
//            findNavController().navigate(
//                R.id.action_dashBoardFragment_to_articleFragment, bundle
//            )
//
//        }
//    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        // setUpRecyclerView()
        // lectureAdapter.startListening()

    }

    override fun onStop() {
        super.onStop()
        // lectureAdapter.stopListening()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun subscribeToObservers() {
        viewModel.likePostStatus.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {
                curLikedIndex?.let { index ->
                    postAdapter.posts[index].isLiking = false
                    postAdapter.notifyItemChanged(index)
                }
                // snackbar(it)
            },
            onLoading = {
                curLikedIndex?.let { index ->
                    postAdapter.posts[index].isLiking = true
                    postAdapter.notifyItemChanged(index)
                }
            }
        ) { isLiked ->
            curLikedIndex?.let { index ->
                val uid = FirebaseAuth.getInstance().currentUser?.uid!!
                postAdapter.posts[index].apply {
                    this.isLiked = isLiked
                    isLiking = false
                    if (isLiked) {
                        likedBy += uid
                    } else {
                        likedBy -= uid
                    }
                }
                postAdapter.notifyItemChanged(index)
            }
        })

        viewModel.counterForPost.observe(
            viewLifecycleOwner, Event.EventObserver(
                onError = {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                },
                onLoading = {

                })
            {

            }


        )




        viewModel.posts.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {
                progressBarPost.isVisible = false
                // postProgressBar.isVisible = false
                // snackbar(it)
                //Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            },
            onLoading = {
                progressBarPost.isVisible = true
                //postProgressBar.isVisible = true
            }
        ) { posts ->
            // postProgressBar.isVisible = false
            progressBarPost.isVisible = false
            postAdapter.posts = posts
        })


    }


    private fun setupRecyclerViewPost() = rv_dash.apply {
        adapter = postAdapter
        layoutManager = LinearLayoutManager(requireContext())
        itemAnimator = null
    }

    private fun showConnectorsCount() {

        coachCollectRef.document(user?.uid!!).get().addOnSuccessListener {
            val coach = it.toObject(UserDetails::class.java)!!


//
//            isConnected=model.coachEmail in coach.follows
//            if (isConnected){
//                Log.d(com.pasosync.pasosynccoach.adapters.TAG, "true:$isConnected")
//                tvConnected.visibility=View.VISIBLE
//            }else{
//                tvConnected.visibility=View.GONE
//                Log.d(com.pasosync.pasosynccoach.adapters.TAG, "false:$isConnected ")
//            }


                val count = coach.followsCoaches.size - 1

                tvTotal_number_of_coach_subscriber.text = count.toString()



        }

    }

}
