package com.pasosync.pasosynccoach.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.adapters.PostAdapter
import com.pasosync.pasosynccoach.ui.MainActivity
import com.pasosync.pasosynccoach.ui.dialogs.DeletePostDialog
import com.pasosync.pasosynccoach.ui.viewmodels.MainViewModels
import com.pasosync.pasosynccoach.other.Event
import kotlinx.android.synthetic.main.fragment_post.*

class PostFragment : Fragment(R.layout.fragment_post) {
    //  lateinit var freeLectureAdapter: FreeLectureAdapter
    private val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()

    //  lateinit var newPostAdapter: NewPostAdapter
    lateinit var viewModel: MainViewModels
    private var postAdapter = PostAdapter()
    private var curLikedIndex: Int? = null
    private val freeDetailsRef =
        db.collection("CoachLectureList")
            .document(user?.uid!!).collection("Post")
    private val lectureDetailsRef =
        db.collection("Posts")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_post, container, false)

        setHasOptionsMenu(true)

        return v

    }

    override fun onStart() {
        super.onStart()
        //  freeLectureAdapter.startListening()
        // newPostAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        //  freeLectureAdapter.stopListening()
        //  newPostAdapter.stopListening()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewmodel
        // viewModel.loadProfile(user?.email.toString())
        swipePractice.setOnRefreshListener {
            //  postAdapter.posts=
            setupRecyclerViewPost()
            //   subscribeToObservers()
//            var queryAdapter=QueryAdapter(queryList)
//            queryAdapter.notifyDataSetChanged()
            swipePractice.isRefreshing = false
        }
        setupRecyclerViewPost()
        subscribeToObservers()
        viewModel.getPostsProfile(user?.uid!!)
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
//            viewModel.getUsers(post.likedBy)
        }

        postAdapter.setOnCommentsClickListener { post ->
            findNavController().navigate(
                R.id.globalActionToCommentDialog,
                Bundle().apply { putString("postId", post.id) }
            )
        }

        postAdapter.setOnDeletePostClickListener { post ->
            DeletePostDialog().apply {
                setPositiveListener {
                    viewModel.deletePost(post)
                }
            }.show(childFragmentManager, null)
        }

        //  setupRecyclerView()
        //   freeSetUpRecyclerView()
//        freeLectureAdapter.setOnItemClickListener {
//            val bundle = Bundle().apply {
//                putSerializable("lecture", it)
//            }
//            findNavController().navigate(
//                R.id.action_lectureListFragment_to_lectureContent,
//                bundle
//            )
//        }

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    private fun subscribeToObservers() {

        viewModel.postsProfile.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {
                // postProgressBar.isVisible = false
                // snackbar(it)
                progressBarPostProfile.isVisible = false
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            },
            onLoading = {
                progressBarPostProfile.isVisible = true
                //postProgressBar.isVisible = true
            }
        ) { posts ->
            // postProgressBar.isVisible = false
            progressBarPostProfile.isVisible = false
            postAdapter.posts = posts
        })



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

//        viewModel.likedByUsers.observe(viewLifecycleOwner, Event.EventObserver(
//            onError = {
//                //snackbar(it)
//                progressBarPostProfile.isVisible = false
//                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
//                Log.d("TAG", "subscribeToObservers: $it")
//            }
//        ) { users ->
//            val userAdapter = UserAdapter()
//            userAdapter.users = users
//            LikedByDialog(userAdapter).show(childFragmentManager, null)
//        })


        viewModel.deletePostStatus.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {// snackbar(it) }
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                Log.d("Deleting", "subscribeToObservers: $it")

            }
        ) { deletedPost ->
            viewModel.deletePost(deletedPost)
            postAdapter.posts -= deletedPost
        })
    }


    private fun setupRecyclerViewPost() = rv_lectureList.apply {
        adapter = postAdapter
        layoutManager = LinearLayoutManager(requireContext())
        itemAnimator = null
    }


}