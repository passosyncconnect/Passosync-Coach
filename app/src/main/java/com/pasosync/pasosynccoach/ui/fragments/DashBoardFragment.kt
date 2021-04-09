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
import androidx.core.view.size
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.adapters.DashBoardAdapter
import com.pasosync.pasosynccoach.adapters.LectureAdapter
import com.pasosync.pasosynccoach.adapters.NewsAdapter
import com.pasosync.pasosynccoach.data.NewLectureDetails
import com.pasosync.pasosynccoach.databinding.FargmentDashboardBinding
import com.pasosync.pasosynccoach.db.Lectures
import com.pasosync.pasosynccoach.other.Constant.REQUEST_CODE_WRITING
import com.pasosync.pasosynccoach.other.Permissions
import com.pasosync.pasosynccoach.other.Resource
import com.pasosync.pasosynccoach.ui.MainActivity
import com.pasosync.pasosynccoach.ui.viewmodels.MainViewModels



import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


class DashBoardFragment : Fragment(R.layout.fargment_dashboard),
    EasyPermissions.PermissionCallbacks {
    private val TAG = "DashBoardFragment"
    private val KEY_TITLE = "title"
    private val KEY_FREE = "free"
    lateinit var viewModel: MainViewModels
    lateinit var lectureAdapter: DashBoardAdapter
private lateinit var binding: FargmentDashboardBinding
    lateinit var builder: AlertDialog.Builder
    var userList = arrayListOf<NewLectureDetails>()
    lateinit var dialog: AlertDialog
    private val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()
    private val lectureDetailsRef =
        db.collection("CoachLectureList").document(user?.email.toString()).collection(
            "PaidLecture"
        )
    val countRef = db.collection("CoachSubscriberCount").document(user?.email.toString())
    val freeCountRef = db.collection("FreeCoachSubscriberCount").document(user?.email.toString())
    lateinit var newsAdapter: NewsAdapter
    private fun setUpRecyclerView() {
        binding.rvDash.apply {
            Log.d(TAG, "setUpRecyclerView: ${binding.rvDash.size}")
            val query = lectureDetailsRef.orderBy("date", Query.Direction.DESCENDING).limit(3)
            val options = FirestoreRecyclerOptions.Builder<NewLectureDetails>()
                .setQuery(query, NewLectureDetails::class.java)
                .build()
            lectureAdapter = DashBoardAdapter(options)
            adapter = lectureAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fargment_dashboard, container, false)
        setHasOptionsMenu(true)

        return v
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FargmentDashboardBinding.bind(view)
        getSubscriberCount()
        requestPermissions()
        setUpRecyclerView()
        getFreeSubscriberCount()
        lectureAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("lecture", it)
            }
            findNavController().navigate(R.id.action_dashBoardFragment_to_lectureContent, bundle)
        }


        viewModel = (activity as MainActivity).viewmodel
        setUpRecyclerViewNews()
        (activity as AppCompatActivity).supportActionBar?.title = " "




        binding.dashboardStudentCardView.setOnClickListener {
            findNavController().navigate(R.id.action_dashBoardFragment_to_freeSubscribersFragment)
        }
        binding.dashboardSubscriberCardView.setOnClickListener {
            findNavController().navigate(R.id.action_dashBoardFragment_to_paidSubscribersFragment)
        }
        viewModel.cricketNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles)
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.e(TAG, "An error occured: $message")
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }

        })
    }

    private fun getSubscriberCount() = CoroutineScope(Dispatchers.IO).launch {
        try {
            if (countRef==null){
                binding.tvTotalNumberOfSubscriber.text="0"
                Log.d(TAG, "ge: ${binding.tvTotalNumberOfSubscriber}")
            }else{
                var count = "0"
                countRef.get().addOnSuccessListener {
                    var title = it.get(KEY_TITLE).toString()
                    count = title

                    Log.d(TAG, "getSubscriberCount: $title")
                    Log.d(TAG, "getSubscriberCount: $count")
                }.await()
                withContext(Dispatchers.Main) {
                    if (count.equals(0)){
                        binding.tvTotalNumberOfSubscriber.text="00"

                    }else{
                        binding.tvTotalNumberOfSubscriber.text = count
                    }






                }

            }

        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getFreeSubscriberCount() = CoroutineScope(Dispatchers.IO).launch {
        try {
            var count = "0"
            freeCountRef.get().addOnSuccessListener {
                var title = it.get(KEY_FREE).toString()
                count = title
                Log.d(TAG, "getSubscriberCount: $title")
                Log.d(TAG, "getSubscriberCount: $count")
            }.await()
            withContext(Dispatchers.Main) {
                binding.tvTotalNumberOfStudent.text = count
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
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
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
        binding.paginationProgressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
    }

    private fun setUpRecyclerViewNews() = binding.newsSliderDashboard.apply {
        newsAdapter = NewsAdapter()
        adapter = newsAdapter
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        newsAdapter.setOnItemClickListener {
            Log.e("Nee=w=", "getting click")
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_dashBoardFragment_to_articleFragment, bundle
            )

        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        lectureAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
         lectureAdapter.stopListening()
    }

}
