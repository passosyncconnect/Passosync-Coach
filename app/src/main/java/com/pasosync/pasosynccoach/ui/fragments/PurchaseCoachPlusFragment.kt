package com.pasosync.pasosynccoach.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.adapters.IntroViewPagerAdapter
import com.pasosync.pasosynccoach.adapters.PurchaseViewPagerAdapter


import com.pasosync.pasosynccoach.other.ScreenItem
import kotlinx.android.synthetic.main.purchase_layout_screen.*


class PurchaseCoachPlusFragment : Fragment(R.layout.purchase_layout_screen) {
    private val db = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser
    var radioGroup: RadioGroup? = null
    lateinit var radioButton: RadioButton
    var introViewPagerAdapter: PurchaseViewPagerAdapter? = null
    var position = 0
    var radioText = "Free Trial"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v: View = inflater.inflate(R.layout.purchase_layout_screen, container, false)
        setHasOptionsMenu(true)

        return v
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val nameRef = db.collection("UserDetails")
            .document(user?.uid!!).get().addOnSuccessListener {
                val freeCount = it.getString("userPicUri")

                Glide.with(requireContext()).load(freeCount).into(ivCoachPlusPurchaseProfilePic)


            }

        rgPackage.setOnCheckedChangeListener { group, checkedId ->
            var rb = view.findViewById<RadioButton>(checkedId)
            if (rb != null) {
                radioText = rb.text.toString()
            } else {

            }


        }

        // fill list screen
        val mList: MutableList<ScreenItem> = ArrayList()
        mList.add(
            ScreenItem(
                "Video Analytics",
                "Swipe",
                R.drawable.videoanalytics
            )
        )
        mList.add(
            ScreenItem(
                "Video Consultation",
                "Swipe",
                R.drawable.videoanalytics
            )
        )

        mList.add(
            ScreenItem(
                "Player Management",
                "Swipe",
                R.drawable.playermng
            )
        )

        mList.add(
            ScreenItem(
                "1-1 online consultation",
                "",
                R.drawable.oneonone
            )
        )
        radioGroup = view.findViewById(R.id.rgPackage)

        val intSelectButton: Int = radioGroup!!.checkedRadioButtonId
        radioButton = view.findViewById(intSelectButton)
        btMakePayment.setOnClickListener {
            val bundle = Bundle().apply {
                putString("price", radioText)
            }

            findNavController().navigate(
                R.id.action_purchaseCoachPlusFragment2_to_paymentFragment,
                bundle
            )

        }



        introViewPagerAdapter = PurchaseViewPagerAdapter(requireContext(), mList)

        purchaseViewpager.adapter = introViewPagerAdapter
        tab_purchase_indicator.setupWithViewPager(purchaseViewpager)

        tab_purchase_indicator!!.addOnTabSelectedListener(object :
            TabLayout.BaseOnTabSelectedListener<TabLayout.Tab?> {



            override fun onTabSelected(tab: TabLayout.Tab?) {

                if (tab!!.position == mList.size - 1) {


                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })


    }

    fun onModeSelected(view: View?) {
        val radioGroup: RadioGroup = view?.findViewById(R.id.rgPackage)!!
        //  Log.d(com.pasosync.pasosynccoach.ui.TAG, "onAligned: $radioText")
        when (rgPackage.checkedRadioButtonId) {
//            R.id.alignLeft -> Toast.makeText(this, "alignLeft", Toast.LENGTH_SHORT).show()
//            R.id.alignCenter -> Toast.makeText(this, "alignCenter", Toast.LENGTH_SHORT).show()
//            R.id.alignRighr -> Toast.makeText(this, "alignRighr", Toast.LENGTH_SHORT).show()
//            R.id.alignJustify -> Toast.makeText(this, "alignJustify", Toast.LENGTH_SHORT).show()


            R.id.rbFreeTrial -> {
                radioText = "Free Trial"
                Snackbar.make(radioGroup, "Free Trial", Snackbar.LENGTH_SHORT).show()

            }
            R.id.rbMonthly -> {
                radioText = "Rs.175 Monthly"
                Snackbar.make(radioGroup, "Rs.175 Monthly", Snackbar.LENGTH_SHORT).show()
            }
            R.id.rbYearly -> {
                radioText = "Rs.1800 Yearly"
                Snackbar.make(radioGroup, "Rs.1800 Yearly", Snackbar.LENGTH_SHORT).show()
            }

            else -> {

            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

}