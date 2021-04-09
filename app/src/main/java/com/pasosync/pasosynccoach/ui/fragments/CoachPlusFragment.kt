package com.pasosync.pasosynccoach.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.BaseOnTabSelectedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.adapters.IntroViewPagerAdapter
import com.pasosync.pasosynccoach.databinding.FragmentCoachPlusBinding
import com.pasosync.pasosynccoach.other.ScreenItem
import com.pasosync.pasosynccoach.ui.CoachPlusActivity


class CoachPlusFragment : Fragment(R.layout.fragment_coach_plus) {
lateinit var binding:FragmentCoachPlusBinding
    private var screenPager: ViewPager? = null
    var introViewPagerAdapter: IntroViewPagerAdapter? = null

    var position = 0

    var btnAnim: Animation? = null
    var tvSkip: TextView? = null
    private val db = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v: View = inflater.inflate(R.layout.fragment_coach_plus, container, false)
        setHasOptionsMenu(true)
binding= FragmentCoachPlusBinding.bind(v)
        return v
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding= FragmentCoachPlusBinding.bind(view)
        //Glide.with(requireContext()).load(Constant.COMING_SOON).into(ivComingSoon)
        // ini views
        // ini views

        btnAnim = AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.button_animation
        )
        db.collection("UserDetails")
            .document(user?.uid!!).get().addOnSuccessListener {
                val freeCount = it.getBoolean("coachPlus")

                if (freeCount == true){
                    binding.btnGoToCoachPlus.visibility=View.VISIBLE
                    invisibleScreen()
                }else{
                    binding.btnGoToCoachPlus.visibility=View.INVISIBLE
                }
               // coach_list_tv_name.text = freeCount.toString()
            }

        // fill list screen

        binding.btnGoToCoachPlus.setOnClickListener {
            Intent(requireContext(),CoachPlusActivity::class.java).also {
                startActivity(it)

            }
        }


        // fill list screen
        val mList: MutableList<ScreenItem> = ArrayList()
        mList.add(
            ScreenItem(
                "",
                "",
                R.drawable.videoanalytics
            )
        )
        mList.add(
            ScreenItem(
                "",
                "",
                R.drawable.videoanalytics
            )
        )
        mList.add(
            ScreenItem(
                "",
                "",
                R.drawable.playermng
            )
        )


        mList.add(
            ScreenItem(
                "1-1 online consultation",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua, consectetur  consectetur adipiscing elit",
                R.drawable.oneonone
            )
        )
        // setup viewpager

        // setup viewpager
       // screenPager = findViewById(R.id.screen_viewpager)
        introViewPagerAdapter = IntroViewPagerAdapter(requireContext(), mList)
        binding.screenViewpager.adapter = introViewPagerAdapter

        // setup tablayout with viewpager


        // setup tablayout with viewpager
        binding.tabIndicator.setupWithViewPager(binding.screenViewpager)

// next button click Listner

        // next button click Listner
        binding.btnNext.setOnClickListener {
            position = binding.screenViewpager.currentItem
            if (position < mList.size) {
                position++
                binding.screenViewpager.currentItem = position
            }
            if (position == mList.size - 1) { // when we rech to the last screen


                loaddLastScreen()
            }
        }

        // tablayout add change listener


        // tablayout add change listener
       binding.tabIndicator.addOnTabSelectedListener(object :
           BaseOnTabSelectedListener<TabLayout.Tab?> {


           override fun onTabSelected(tab: TabLayout.Tab?) {

               if (tab!!.position == mList.size - 1) {

                   loaddLastScreen()

               }
           }

           override fun onTabUnselected(tab: TabLayout.Tab?) {

           }

           override fun onTabReselected(tab: TabLayout.Tab?) {

           }
       })

        // Get Started button click listener

        // Get Started button click listener
        binding.btnGetStarted.setOnClickListener { //open main activity
         findNavController().navigate(R.id.action_coachPlusFragment_to_purchaseCoachPlusFragment2)
            // also we need to save a boolean value to storage so next time when the user run the app
            // we could know that he is already checked the intro screen activity
            // i'm going to use shared preferences to that process

           // savePrefsData()


        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    // show the GETSTARTED Button and hide the indicator and the next button
    private fun loaddLastScreen() {
        binding.btnNext.visibility = View.INVISIBLE
        binding.btnGetStarted.visibility = View.VISIBLE
       // binding.tvSkip!!.visibility = View.INVISIBLE
        binding.tabIndicator.visibility = View.INVISIBLE

        // setup animation
        binding.btnGetStarted.animation = btnAnim
    }

    private fun invisibleScreen() {
        binding.btnNext.visibility = View.INVISIBLE
        binding.btnGetStarted.visibility = View.INVISIBLE
        // binding.tvSkip!!.visibility = View.INVISIBLE
        binding.tabIndicator.visibility = View.INVISIBLE

        // setup animation
        binding.screenViewpager.visibility=View.INVISIBLE
    }

}