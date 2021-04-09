package com.pasosync.pasosynccoach.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.adapters.LecturesViewPagerAdapter
import com.pasosync.pasosynccoach.adapters.PaidLectureAdapter
import com.pasosync.pasosynccoach.data.NewLectureDetails
import com.pasosync.pasosynccoach.databinding.FargmentLecturelistBinding
import com.pasosync.pasosynccoach.ui.MainActivity
import com.pasosync.pasosynccoach.ui.viewmodels.MainViewModels


class LectureListFragment : Fragment(R.layout.fargment_lecturelist) {


    // lateinit var lectureAdapter: LectureAdapter
    private lateinit var binding: FargmentLecturelistBinding
    lateinit var viewModel: MainViewModels
    lateinit var builder: AlertDialog.Builder
    var userList = arrayListOf<NewLectureDetails>()
    lateinit var dialog: AlertDialog

    lateinit var paidLectureAdapter: PaidLectureAdapter
    private val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()
    private val lectureDetailsRef =
        db.collection("CoachLectureList").document(user?.uid!!).collection(
            "PaidLecture"
        )

    private val freeDetailsRef =
        db.collection("CoachLectureList").document(user?.uid!!).collection(
            "FreeLecture"
        )




//    private fun setUpRecyclerView() {
//        binding.rvPaidLectureList.apply {
//
//            val query = lectureDetailsRef.orderBy("date", Query.Direction.DESCENDING)
//            val options = FirestoreRecyclerOptions.Builder<NewLectureDetails>()
//                .setQuery(query, NewLectureDetails::class.java)
//                .build()
//            paidLectureAdapter = PaidLectureAdapter(options)
//            adapter = paidLectureAdapter
//            layoutManager = LinearLayoutManager(requireContext())
//
//
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
//                    paidLectureAdapter.deleteItem(viewHolder.adapterPosition)
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
//
//
//        }
//    }
//
//    private fun freeSetUpRecyclerView() {
//        binding.rvLectureList.apply {
//            val query = freeDetailsRef.orderBy("date", Query.Direction.DESCENDING)
//            val options = FirestoreRecyclerOptions.Builder<NewLectureDetails>()
//                .setQuery(query, NewLectureDetails::class.java)
//                .build()
//            freeLectureAdapter = FreeLectureAdapter(options)
//            adapter = freeLectureAdapter
//            layoutManager = LinearLayoutManager(requireContext())
//
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
//                    freeLectureAdapter.deleteItem(viewHolder.adapterPosition)
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
//
//
//        }
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



    }


    override fun onStop() {
        super.onStop()

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FargmentLecturelistBinding.bind(view)

        viewModel = (activity as MainActivity).viewmodel
        (activity as AppCompatActivity).supportActionBar?.title = " "

        builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false) // if you want user to wait for some process to finish,

        builder.setView(R.layout.layout_loading_dialog)
        dialog = builder.create()


        setUpTabs()
//        setUpRecyclerView()
//        freeSetUpRecyclerView()
    }


    private fun setUpTabs() {
        val tabAdapter= LecturesViewPagerAdapter(childFragmentManager)
        tabAdapter.addFragment(PostFragment(),"Post")
        tabAdapter.addFragment(PremiumLecturesList(),"Drills")

        binding.viewPager2Lecture.adapter=tabAdapter
        binding.tabsLecture.setupWithViewPager(binding.viewPager2Lecture)
        binding.tabsLecture.getTabAt(0)!!.setIcon(R.drawable.send)
        binding.tabsLecture.getTabAt(1)!!.setIcon(R.drawable.lecturetab)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

}