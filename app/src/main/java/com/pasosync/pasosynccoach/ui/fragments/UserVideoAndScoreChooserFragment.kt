package com.pasosync.pasosynccoach.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.onNavDestinationSelected
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.databinding.FragmentAcademyMembersListBinding
import com.pasosync.pasosynccoach.databinding.FragmentUserVideoAndScoreChooserBinding
import kotlinx.android.synthetic.main.fragment_user_video_and_score_chooser.*


private const val TAG = "UserVideoAndScoreChoose"
class UserVideoAndScoreChooserFragment:Fragment(R.layout.fragment_user_video_and_score_chooser) {
private lateinit var binding:FragmentUserVideoAndScoreChooserBinding
val args:UserVideoAndScoreChooserFragmentArgs by navArgs()


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_user_video_and_score_chooser, container, false)
        setHasOptionsMenu(true)
        binding = FragmentUserVideoAndScoreChooserBinding.bind(v)
        return v
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FragmentUserVideoAndScoreChooserBinding.bind(view)
        Log.d(TAG, "onViewCreated: ${args.users.academy}")

        videoUserCardView.setOnClickListener {
            val bundle=Bundle().apply {
                putString("video",args.users.uid)
            }

            findNavController().navigate(R.id.action_userVideoAndScoreChooserFragment_to_usersVideoFragment,bundle)

        }

        scoreUserCardView.setOnClickListener {
            val bundle=Bundle().apply {
                putString("score",args.users.uid)
            }
            findNavController().navigate(R.id.action_userVideoAndScoreChooserFragment_to_usersScoreFragment,bundle)


        }

        practiceScoreUserCardView.setOnClickListener {
            val bundle=Bundle().apply {
                putString("practicescore",args.users.uid)
            }

            findNavController().navigate(R.id.action_userVideoAndScoreChooserFragment_to_userPracticeScoreFragment,bundle)
        }

        practiceVideoUserCardView.setOnClickListener {
            val bundle=Bundle().apply {
                putString("practice",args.users.uid)
            }

            findNavController().navigate(R.id.action_userVideoAndScoreChooserFragment_to_userPracticeVideoFragment,bundle)

        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }
}