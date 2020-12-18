package com.pasosync.pasosynccoach.ui.fragments

import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.adapters.FreeLectureAdapter
import com.pasosync.pasosynccoach.adapters.PaidLectureAdapter
import com.pasosync.pasosynccoach.data.NewLectureDetails
import com.pasosync.pasosynccoach.databinding.FargmentLecturelistBinding
import com.pasosync.pasosynccoach.ui.MainActivity
import com.pasosync.pasosynccoach.ui.viewmodels.MainViewModels
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator



const val CANCEL_TRACKING_DIALOG = "CancelDialog"

class LectureListFragment : Fragment(R.layout.fargment_lecturelist) {
    private val TAG = "LectureListFragment"
    // lateinit var lectureAdapter: LectureAdapter
private lateinit var binding: FargmentLecturelistBinding
    lateinit var viewModel: MainViewModels
    lateinit var builder: AlertDialog.Builder
    var userList = arrayListOf<NewLectureDetails>()
    lateinit var dialog: AlertDialog
    lateinit var freeLectureAdapter: FreeLectureAdapter
    lateinit var paidLectureAdapter: PaidLectureAdapter
    private val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()
    private val lectureDetailsRef =
        db.collection("CoachLectureList").document(user?.email.toString()).collection(
            "PaidLecture"
        )

    private val freeDetailsRef =
        db.collection("CoachLectureList").document(user?.email.toString()).collection(
            "FreeLecture"
        )


    private fun showDialog() {
        DeleteLectureDialog().apply {
            setYesListener {

            }
        }
    }

    private fun setUpRecyclerView() {
        binding.rvPaidLectureList.apply {

            val query = lectureDetailsRef.orderBy("date",Query.Direction.DESCENDING)
            val options = FirestoreRecyclerOptions.Builder<NewLectureDetails>()
                .setQuery(query, NewLectureDetails::class.java)
                .build()
            paidLectureAdapter = PaidLectureAdapter(options)
            adapter = paidLectureAdapter
            layoutManager = LinearLayoutManager(requireContext())


            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    paidLectureAdapter.deleteItem(viewHolder.adapterPosition)
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    RecyclerViewSwipeDecorator.Builder(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                        .addBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.red
                            )
                        )
                        .addActionIcon(R.drawable.ic_delete)
                        .create()
                        .decorate()

                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            }).attachToRecyclerView(this)


        }
    }
    private fun freeSetUpRecyclerView() {
        binding.rvLectureList.apply {
            val query = freeDetailsRef.orderBy("date",Query.Direction.DESCENDING)
            val options = FirestoreRecyclerOptions.Builder<NewLectureDetails>()
                .setQuery(query, NewLectureDetails::class.java)
                .build()
            freeLectureAdapter = FreeLectureAdapter(options)
            adapter = freeLectureAdapter
            layoutManager = LinearLayoutManager(requireContext())

            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    freeLectureAdapter.deleteItem(viewHolder.adapterPosition)
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    RecyclerViewSwipeDecorator.Builder(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                        .addBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.red
                            )
                        )
                        .addActionIcon(R.drawable.ic_delete)
                        .create()
                        .decorate()

                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            }).attachToRecyclerView(this)


        }
    }

//    private fun setUpRecyclerView() = rv_lectureList.apply {
//        Log.d(TAG, "setUpRecyclerView: ${rv_lectureList.size}")
//        try {
//            val query = lectureDetailsRef.orderBy("date", Query.Direction.DESCENDING)
//            val options = FirestoreRecyclerOptions.Builder<NewLectureDetails>()
//                .setQuery(query, NewLectureDetails::class.java)
//                .build()
//            lectureAdapter = LectureAdapter(options)
//            adapter = lectureAdapter
//            setHasFixedSize(true)
//            layoutManager = LinearLayoutManager(requireContext())
//
//        } catch (e: Exception) {
//            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
//        }
//
//        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
//            0,
//            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
//        ) {
//            override fun onMove(
//                recyclerView: RecyclerView,
//                viewHolder: RecyclerView.ViewHolder,
//                target: RecyclerView.ViewHolder
//            ): Boolean {
//                return false
//            }
//
//            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                lectureAdapter.deleteItem(viewHolder.adapterPosition)
//            }
//
//            override fun onChildDraw(
//                c: Canvas,
//                recyclerView: RecyclerView,
//                viewHolder: RecyclerView.ViewHolder,
//                dX: Float,
//                dY: Float,
//                actionState: Int,
//                isCurrentlyActive: Boolean
//            ) {
//                RecyclerViewSwipeDecorator.Builder(
//                    c,
//                    recyclerView,
//                    viewHolder,
//                    dX,
//                    dY,
//                    actionState,
//                    isCurrentlyActive
//                )
//                    .addBackgroundColor(
//                        ContextCompat.getColor(
//                            requireContext(),
//                            R.color.red
//                        )
//                    )
//                    .addActionIcon(R.drawable.ic_delete)
//                    .create()
//                    .decorate()
//
//                super.onChildDraw(
//                    c,
//                    recyclerView,
//                    viewHolder,
//                    dX,
//                    dY,
//                    actionState,
//                    isCurrentlyActive
//                )
//            }
//        }).attachToRecyclerView(this)
//    }

//    private fun setUpFreeRecyclerView() = rv_lectureList.apply {
//        Log.d(TAG, "setUpFreeRecyclerView: ${rv_lectureList.size}")
//        try {
//            val query = freeDetailsRef.orderBy("date", Query.Direction.DESCENDING)
//            val options = FirestoreRecyclerOptions.Builder<NewLectureDetails>()
//                .setQuery(query, NewLectureDetails::class.java)
//                .build()
//            lectureAdapter = LectureAdapter(options)
//            adapter = lectureAdapter
//            setHasFixedSize(true)
//            layoutManager = LinearLayoutManager(requireContext())
//
//        } catch (e: Exception) {
//            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
//        }
//
//        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
//            0,
//            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
//        ) {
//            override fun onMove(
//                recyclerView: RecyclerView,
//                viewHolder: RecyclerView.ViewHolder,
//                target: RecyclerView.ViewHolder
//            ): Boolean {
//                return false
//            }
//
//            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                lectureAdapter.deleteItem(viewHolder.adapterPosition)
//            }
//
//            override fun onChildDraw(
//                c: Canvas,
//                recyclerView: RecyclerView,
//                viewHolder: RecyclerView.ViewHolder,
//                dX: Float,
//                dY: Float,
//                actionState: Int,
//                isCurrentlyActive: Boolean
//            ) {
//                RecyclerViewSwipeDecorator.Builder(
//                    c,
//                    recyclerView,
//                    viewHolder,
//                    dX,
//                    dY,
//                    actionState,
//                    isCurrentlyActive
//                )
//                    .addBackgroundColor(
//                        ContextCompat.getColor(
//                            requireContext(),
//                            R.color.red
//                        )
//                    )
//                    .addActionIcon(R.drawable.ic_delete)
//                    .create()
//                    .decorate()
//
//                super.onChildDraw(
//                    c,
//                    recyclerView,
//                    viewHolder,
//                    dX,
//                    dY,
//                    actionState,
//                    isCurrentlyActive
//                )
//            }
//        }).attachToRecyclerView(this)
//    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fargment_lecturelist, container, false)

        setHasOptionsMenu(true)

        return v

    }


    override fun onStart() {
        super.onStart()

        freeLectureAdapter.startListening()
        paidLectureAdapter.startListening()

    }


    override fun onStop() {
        super.onStop()
        freeLectureAdapter.stopListening()
        paidLectureAdapter.stopListening()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FargmentLecturelistBinding.bind(view)

        viewModel = (activity as MainActivity).viewmodel
        (activity as AppCompatActivity).supportActionBar?.title = " "
        Log.d(TAG, "onViewCreated:${user?.email.toString()}")
        builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false) // if you want user to wait for some process to finish,

        builder.setView(R.layout.layout_loading_dialog)
        dialog = builder.create()
        // freeShowData()

        //
        setUpRecyclerView()

        freeSetUpRecyclerView()
        freeLectureAdapter.setOnItemClickListener {
                        val bundle = Bundle().apply {
                            putSerializable("lecture", it)
                        }
                        findNavController().navigate(
                            R.id.action_lectureListFragment_to_lectureContent,
                            bundle
                        )
                    }
        paidLectureAdapter.setOnItemClickListener {
                        val bundle = Bundle().apply {
                            putSerializable("lecture", it)
                        }
                        findNavController().navigate(
                            R.id.action_lectureListFragment_to_lectureContent,
                            bundle
                        )
                    }




//        rv_lectureList.apply {
//            setHasFixedSize(true)
//            layoutManager = LinearLayoutManager(requireContext())
//
//        }

//
//        free_lecture.setOnClickListener {
//            free_lecture.isSelected = !free_lecture.isSelected
//            paid_Lecture.isSelected = false
//            setUpRecyclerView()
//
//            // freeShowData()
//        }
//        paid_Lecture.setOnClickListener {
//            paid_Lecture.isSelected = !paid_Lecture.isSelected
//            free_lecture.isSelected = false
//            paidSetUpRecyclerView()
//            // showData()
//        }


    }


//    private fun showData() {
//        try {
//            dialog.show()
//            userList.clear()
//            val freelectureRef = db.collection("CoachLectureList")
//                .document(user?.email.toString()).collection("PaidLecture")
//                .get().addOnSuccessListener {
//                    for (document in it.documents) {
//                        dialog.dismiss()
//                        val userDetails = NewLectureDetails(
//                            "1",
//                            document.getString("lectureName"),
//                            document.getString("lectureContent"),
//                            document.getString("lectureImageUrl"),
//                            document.getString("lectureVideoUrl"),
//                            document.getString("lecturePdfUrl"), document.getString("date"),
//                            document.getString("seacrh"), document.getString("type")
//                        )
//                        userList.add(userDetails)
//                        Log.d(TAG, "showData: ${userList.size}")
//
//                    }
//                    lectureAdapter = LectureAdapter(userList)
//                    rv_lectureList.adapter = lectureAdapter
//                    dialog.dismiss()
//                    lectureAdapter.setOnItemClickListener {
//                        val bundle = Bundle().apply {
//                            putSerializable("lecture", it)
//                        }
//                        findNavController().navigate(
//                            R.id.action_lectureListFragment_to_lectureContent,
//                            bundle
//                        )
//                    }
//
//
//                }.addOnFailureListener {
//                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
//                    dialog.dismiss()
//                }
//        } catch (e: Exception) {
//            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
//        }
//    }


//    private fun freeShowData() {
//        try {
//            dialog.show()
//            userList.clear()
//            val freelectureRef = db.collection("CoachLectureList")
//                .document(user?.email.toString()).collection("FreeLecture")
//                .get().addOnSuccessListener {
//                    for (document in it.documents) {
//                        dialog.dismiss()
//                        val userDetails = NewLectureDetails(
//                            "1",
//                            document.getString("lectureName"),
//                            document.getString("lectureContent"),
//                            document.getString("lectureImageUrl"),
//                            document.getString("lectureVideoUrl"),
//                            document.getString("lecturePdfUrl"), document.getString("date"),
//                            document.getString("seacrh"), document.getString("type")
//                        )
//                        userList.add(userDetails)
//                        Log.d(TAG, "showData: ${userList.size}")
//
//                    }
//                    lectureAdapter = LectureAdapter(userList)
//                    rv_lectureList.adapter = lectureAdapter
//                    dialog.dismiss()
//                    lectureAdapter.setOnItemClickListener {
//                        val bundle = Bundle().apply {
//                            putSerializable("lecture", it)
//                        }
//                        findNavController().navigate(
//                            R.id.action_lectureListFragment_to_lectureContent,
//                            bundle
//                        )
//                    }
//                    ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
//                        0,
//                        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
//                    ) {
//                        override fun onMove(
//                            recyclerView: RecyclerView,
//                            viewHolder: RecyclerView.ViewHolder,
//                            target: RecyclerView.ViewHolder
//                        ): Boolean {
//                            return false
//                        }
//
//                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                            //  lectureAdapter.deleteItem(viewHolder.adapterPosition)
//                            val pos=viewHolder.adapterPosition
//
//                            userList.removeAt(pos)
//                            freeDetailsRef
//                            lectureAdapter.notifyDataSetChanged()
//
//
//                        }
//
//                        override fun onChildDraw(
//                            c: Canvas,
//                            recyclerView: RecyclerView,
//                            viewHolder: RecyclerView.ViewHolder,
//                            dX: Float,
//                            dY: Float,
//                            actionState: Int,
//                            isCurrentlyActive: Boolean
//                        ) {
//                            RecyclerViewSwipeDecorator.Builder(
//                                c,
//                                recyclerView,
//                                viewHolder,
//                                dX,
//                                dY,
//                                actionState,
//                                isCurrentlyActive
//                            )
//                                .addBackgroundColor(
//                                    ContextCompat.getColor(
//                                        requireContext(),
//                                        R.color.red
//                                    )
//                                )
//                                .addActionIcon(R.drawable.ic_delete)
//                                .create()
//                                .decorate()
//
//                            super.onChildDraw(
//                                c,
//                                recyclerView,
//                                viewHolder,
//                                dX,
//                                dY,
//                                actionState,
//                                isCurrentlyActive
//                            )
//                        }
//                    }).attachToRecyclerView(rv_lectureList)
//
//
//
//
//                }.addOnFailureListener {
//                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
//                    dialog.dismiss()
//
//                }
//        } catch (e: Exception) {
//            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
//        }
//    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

}