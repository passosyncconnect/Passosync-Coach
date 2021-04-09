package com.pasosync.pasosynccoach.ui.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.pasosync.pasosynccoach.R
import kotlinx.android.synthetic.main.activity_main.*


/**
 * A simple [Fragment] subclass.
 */

class EarningsFragment : Fragment(R.layout.fargment_earnings) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fargment_earnings,container, false)
        setHasOptionsMenu(true)
        return v

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = " "
    }






    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.helpFragment -> {
//                coachNavHostFragment.findNavController().navigate(R.id.action_earningsFragment_to_helpFragment)
//            }
//            R.id.notificationFragment -> Toast.makeText(requireContext(), "notification", Toast.LENGTH_SHORT).show()
//
//        }
//        return true
        val navController =findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)

    }
    }




