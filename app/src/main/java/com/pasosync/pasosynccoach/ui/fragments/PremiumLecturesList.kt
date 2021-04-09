package com.pasosync.pasosynccoach.ui.fragments

import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.pasosync.pasosynccoach.adapters.PaidLectureAdapter
import com.pasosync.pasosynccoach.data.NewLectureDetails
import com.pasosync.pasosynccoach.ui.dialogs.DeletePostDialog
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.fragment_premium_lecture.*

class PremiumLecturesList : Fragment(R.layout.fragment_premium_lecture) {

    private val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()
    lateinit var paidLectureAdapter: PaidLectureAdapter
    private val lectureDetailsRef =
        db.collection("CoachLectureList").document(user?.uid!!).collection(
            "PaidLecture"
        )


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_premium_lecture, container, false)

        setHasOptionsMenu(true)

        return v

    }

    override fun onStart() {
        super.onStart()
        paidLectureAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        paidLectureAdapter.stopListening()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)





        setUpRecyclerView()

        paidLectureAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("lecture", it)
            }
            findNavController().navigate(
                R.id.action_lectureListFragment_to_lectureContent,
                bundle
            )
        }
        paidLectureAdapter.setOnDeleteClickListener {

            DeletePostDialog().apply {
                setPositiveListener {
                    it.documentId?.let { it1 ->
                        lectureDetailsRef.document(it1).delete().addOnSuccessListener {
//                            Toast.makeText(
//                                requireContext(),
//                                "Successfully deleted",
//                                Toast.LENGTH_SHORT
//                            ).show()
                        }
                    }
                }
            }.show(childFragmentManager,null)
        }


    }


    private fun setUpRecyclerView() {
        rv_paid_lectureList.apply {

            val query = lectureDetailsRef.orderBy("date", Query.Direction.DESCENDING)
            val options = FirestoreRecyclerOptions.Builder<NewLectureDetails>()
                .setQuery(query, NewLectureDetails::class.java)
                .build()
            paidLectureAdapter = PaidLectureAdapter(options)
            adapter = paidLectureAdapter
            layoutManager = LinearLayoutManager(requireContext())


//            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
//                0,
//                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
//            ) {
//                override fun onMove(
//                    recyclerView: RecyclerView,
//                    viewHolder: RecyclerView.ViewHolder,
//                    target: RecyclerView.ViewHolder
//                ): Boolean {
//                    return false
//                }
//
//                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//
//                    DeletePostDialog().apply {
//                        setPositiveListener {
//                            paidLectureAdapter.deleteItem(viewHolder.adapterPosition)
//                        }
//                    }.show(childFragmentManager, null)
//
//
//                }
//
//                override fun onChildDraw(
//                    c: Canvas,
//                    recyclerView: RecyclerView,
//                    viewHolder: RecyclerView.ViewHolder,
//                    dX: Float,
//                    dY: Float,
//                    actionState: Int,
//                    isCurrentlyActive: Boolean
//                ) {
//                    RecyclerViewSwipeDecorator.Builder(
//                        c,
//                        recyclerView,
//                        viewHolder,
//                        dX,
//                        dY,
//                        actionState,
//                        isCurrentlyActive
//                    )
//                        .addBackgroundColor(
//                            ContextCompat.getColor(
//                                requireContext(),
//                                R.color.red
//                            )
//                        )
//                        .addActionIcon(R.drawable.ic_delete)
//                        .create()
//                        .decorate()
//
//                    super.onChildDraw(
//                        c,
//                        recyclerView,
//                        viewHolder,
//                        dX,
//                        dY,
//                        actionState,
//                        isCurrentlyActive
//                    )
//                }
//            }).attachToRecyclerView(this)


        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }
}