package com.pasosync.pasosynccoach.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.onNavDestinationSelected
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.data.PurchasedCoachPlusDetails
import com.pasosync.pasosynccoach.databinding.PaymentPagePlusBinding
import com.pasosync.pasosynccoach.ui.CoachPlusActivity
import com.pasosync.pasosynccoach.ui.MainActivity
import kotlinx.android.synthetic.main.payment_page_plus.*
import kotlinx.android.synthetic.main.purchase_layout_screen.*
import java.util.*

class PaymentFragment : Fragment(R.layout.payment_page_plus) {
    val args: PaymentFragmentArgs by navArgs()
    lateinit var binding: PaymentPagePlusBinding
    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog
    private val db = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser
    private val coachCollectRef = db.collection("UserDetails")
    private val coachPlusCollectRef = db.collection("CoachPlus")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v: View = inflater.inflate(R.layout.payment_page_plus, container, false)
        setHasOptionsMenu(true)
        binding = PaymentPagePlusBinding.bind(v)
        return v
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = PaymentPagePlusBinding.bind(view)
        builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false)
        builder.setView(R.layout.layout_payment_dialog)
        dialog = builder.create()

        val nameRef = db.collection("UserDetails")
            .document(user?.uid!!).get().addOnSuccessListener {
                val freeCount = it.getString("userPicUri")
                val name = it.getString("userName")
                val type = it.getString("userKind")

                Glide.with(requireContext()).load(freeCount).into(binding.purchaseProfilePic)
                binding.purchaseCoachTvName.text = name
                binding.purchaseCoachTvSpeciality.text = type
                binding.purchaseCoachTvPurchaseAmount.text = args.price
            }
        btnBuy.setOnClickListener {
            dialog.show()
            val splash: Thread = object : Thread() {
                override fun run() {
                    try {
                        sleep(3000)
                        coachCollectRef.document(user.uid).update("coachPlus", true)
                        val purchasedCoachPlusDetails = PurchasedCoachPlusDetails(
                            uid = user.uid, "", args.price, "", System.currentTimeMillis()
                        )
                        coachPlusCollectRef.add(purchasedCoachPlusDetails)
                        Intent(requireContext(),CoachPlusActivity::class.java).also {
                            startActivity(it)
                        }

                        dialog.dismiss()
                        Toast.makeText(
                            requireContext(),
                            "Subscription Successful",
                            Toast.LENGTH_SHORT
                        ).show()


                    } catch (e: Exception) {

                    }
                }
            }
            splash.start()


        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

}