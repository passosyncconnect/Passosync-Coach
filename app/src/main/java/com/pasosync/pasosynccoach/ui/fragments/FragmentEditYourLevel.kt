package com.pasosync.pasosynccoach.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.adapters.SpinnerAdapter
import com.pasosync.pasosynccoach.adapters.SpinnerLevelAdapter
import com.pasosync.pasosynccoach.data.SpinnerItem
import com.pasosync.pasosynccoach.data.SpinnerItemforLevels
import com.pasosync.pasosynccoach.data.Types
import com.pasosync.pasosynccoach.data.TypesLevels
import kotlinx.android.synthetic.main.fargment_newpost.*
import kotlinx.android.synthetic.main.fragment_edit_your_level.*

class FragmentEditYourLevel : Fragment(R.layout.fragment_edit_your_level),
    AdapterView.OnItemSelectedListener {
    var spinnerText = ""


    val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()
    private val coachDetailsCollectionRef =
        db.collection("UserDetails").document(user?.uid!!)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_edit_your_level, container, false)
        setHasOptionsMenu(true)
        return v

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpCustomSpinner()
        spinner_update_level.onItemSelectedListener = this

        coachDetailsCollectionRef.get().addOnSuccessListener {
            Glide.with(requireContext()).load(it.getString("userPicUri")).fitCenter()
                .into(circleImageLevel)
        }

        update_level.setOnClickListener {
            var type = spinnerText
            coachDetailsCollectionRef.update("userLevel", type).addOnSuccessListener {
                Toast.makeText(requireContext(), "Updated Successfully", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    private fun setUpCustomSpinner() {
        val adapter = SpinnerLevelAdapter(requireContext(), TypesLevels.list!!)

        spinner_update_level.adapter = adapter

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent?.getItemAtPosition(position)?.equals("Choose a Category")!!) {

        } else {


            val text = parent!!.getItemAtPosition(position).toString()

            val clickedItem: SpinnerItemforLevels =
                parent.getItemAtPosition(position) as SpinnerItemforLevels
            val clickedCountryName: String = clickedItem.TypeName

            spinnerText = clickedCountryName

        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {


    }
}