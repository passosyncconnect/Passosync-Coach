package com.pasosync.pasosynccoach.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.adapters.DraftAdapter
import com.pasosync.pasosynccoach.adapters.DraftLectureAdapter
import com.pasosync.pasosynccoach.data.DraftLectureDetails
import com.pasosync.pasosynccoach.data.NewLectureDetails
import com.pasosync.pasosynccoach.databinding.FargmentDraftBinding
import com.pasosync.pasosynccoach.ui.MainActivity
import com.pasosync.pasosynccoach.ui.viewmodels.MainViewModels
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 */

private const val TAG = "DarftFragment"

class DarftFragment : Fragment(R.layout.fargment_draft) {
    lateinit var viewModel: MainViewModels
    var draft = arrayListOf<DraftLectureDetails>()
    private lateinit var binding: FargmentDraftBinding
    lateinit var draftLectureAdapter: DraftLectureAdapter
    private val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()
    private val draftCollectionRef = db.collection("CoachLectureList")
        .document(user?.email.toString()).collection("DraftLecture")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fargment_draft, container, false)
        setHasOptionsMenu(true)
        return v

    }

    private fun setupDraftRecyclerview() {
        binding.rvDraft.apply {
            val query = draftCollectionRef
            val option = FirestoreRecyclerOptions.Builder<DraftLectureDetails>()
                .setQuery(query, DraftLectureDetails::class.java)
                .build()
            draftLectureAdapter = DraftLectureAdapter(option)
            adapter = draftLectureAdapter
            layoutManager = LinearLayoutManager(requireContext())


        }
    }

    override fun onStart() {
        super.onStart()
        draftLectureAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        draftLectureAdapter.stopListening()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FargmentDraftBinding.bind(view)
        viewModel = (activity as MainActivity).viewmodel

        (activity as AppCompatActivity).supportActionBar?.title = " "
        Log.d(TAG, "onViewCreated: ")
        setupDraftRecyclerview()


        //  retrieveLectureDetails()
//        val title = tv_draft_title.text.toString()
//        confirmInput()
//
//        bt_draft_edit.setOnClickListener {
//            val bundle = Bundle().apply {
//                val title = tv_draft_title.text.toString()
//                val desc = tv_draft_description.text.toString()
//                val draftLectureDetails = DraftLectureDetails(title, desc)
//                putSerializable("draft", draftLectureDetails)
//            }
//
//            findNavController().navigate(R.id.action_draftFragment_to_newPostFragment, bundle)
//        }

//
//        bt_draft_delete.setOnClickListener {
//            draftCollectionRef.delete().addOnSuccessListener {
//                findNavController().navigate(R.id.action_draftFragment_to_newPostFragment)
//            }
//        }


    }

//    private fun validateName(): Boolean {
//        val title: String = tv_draft_title.text.toString()
//        return if (title.isEmpty()) {
//            false
//        } else {
//            true
//        }
//    }
//
//    private fun validateDesc(): Boolean {
//        val desc: String = tv_draft_description.text.toString()
//        return if (desc.isEmpty()) {
//            false
//        } else {
//            true
//        }
//    }
//
//    private fun confirmInput(): Boolean {
//        if (!validateName() or !validateDesc()) {
//            return false
//        }
//        return true
//
//    }


//    private fun retrieveLectureDetails() = CoroutineScope(Dispatchers.IO).launch {
//        try {
//            var title_lecture: String? = null
//            var des_lecture: String? = null
//            val querySnapshot = draftCollectionRef.get().addOnSuccessListener { documents ->
//                val title = documents.getString("lectureTitleCoach")
//                val desc = documents.getString("lectureContentCoach")
//                val id=documents.id
//                Log.d(TAG, "retrieveLectureDetails: ${title}")
//                Log.d(TAG, "retrieveLectureDetails: ${id}")
//                title_lecture = title
//                des_lecture = desc
//
//
//            }.await()
//            withContext(Dispatchers.Main) {
//                tv_draft_title.text = title_lecture
//                tv_draft_description.text = des_lecture
//            }
//        } catch (e: Exception) {
//            withContext(Dispatchers.Main) {
//                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
//            }
//        }
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }
}
