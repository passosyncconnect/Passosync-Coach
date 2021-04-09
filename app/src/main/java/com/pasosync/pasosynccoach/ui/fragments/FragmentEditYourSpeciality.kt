package com.pasosync.pasosynccoach.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.databinding.FargmentDashboardBinding
import kotlinx.android.synthetic.main.fragment_specailty.*


private const val TAG = "FragmentEditYourSpecial"
class FragmentEditYourSpeciality:Fragment(R.layout.fragment_specailty) {
    lateinit var auth: FirebaseAuth
    val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()
    private val coachDetailsCollectionRef =
            db.collection("UserDetails").document(user?.uid!!)
    var typeRadioText = ""

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_specailty, container, false)
        setHasOptionsMenu(true)
       // binding = FargmentDashboardBinding.bind(v)
        //  setUpRecyclerView()
        //   setupRecyclerViewPost()

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        radioGroupProfileSpeciality.setOnCheckedChangeListener { group, checkedId ->
            var rb = view.findViewById<RadioButton>(checkedId)
            if (rb != null) {
                Log.d(TAG, "onViewCreated: ${rb.text.toString()}")
                typeRadioText = rb.text.toString()
                Log.d(TAG, "onViewCreated:$typeRadioText")
            } else {

            }

        }

        coachDetailsCollectionRef.get().addOnSuccessListener {
            Glide.with(requireContext()).load(it.getString("userPicUri")).placeholder(R.drawable.man).fitCenter().into(circleIvSpeciality)

        }

        tvUploadKind.setOnClickListener {

            var type = typeRadioText.toString()
            coachDetailsCollectionRef.update("userKind", type).addOnSuccessListener {
                Toast.makeText(requireContext(), "Updated Successfully", Toast.LENGTH_SHORT)
                        .show()
            }


        }

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

}