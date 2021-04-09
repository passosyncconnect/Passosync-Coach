package com.pasosync.pasosynccoach.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.adapters.SpinnerAdapter
import com.pasosync.pasosynccoach.adapters.SpinnerCoachAdapter
import com.pasosync.pasosynccoach.data.*
import com.pasosync.pasosynccoach.databinding.ActivityFirstProfileBinding
import kotlinx.android.synthetic.main.activity_first_profile.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fargment_newpost.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirstProfileActivity : AppCompatActivity(),AdapterView.OnItemSelectedListener {
    var EXTRA_TEXT = "com.example.application.example.EXTRA_TEXT"
    var EXTRA_NUMBER = "com.example.application.example.EXTRA_NUMBER"
    private  val TAG = "FirstProfileActivity"
    lateinit var auth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog
    private lateinit var binding: ActivityFirstProfileBinding
    private val db = FirebaseFirestore.getInstance()
    var spinnerText = ""



    private fun setUpCustomSpinner() {
        val adapter = SpinnerCoachAdapter(this,TypesCoach.list!!)

        binding.spinnerFirstProfile.adapter=adapter
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding= ActivityFirstProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.firstUpdateEmailInput.isEnabled=false
        builder = AlertDialog.Builder(this)
        builder.setCancelable(false) // if you want user to wait for some process to finish
        builder.setView(R.layout.layout_loading_dialog)
        dialog = builder.create()
        auth = FirebaseAuth.getInstance()
        supportActionBar?.hide()

        setUpCustomSpinner()
        binding.spinnerFirstProfile.onItemSelectedListener = this

        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
                dialog.dismiss()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        val intent = intent
        val text = intent.getStringExtra(EXTRA_TEXT)
        binding.firstUpdateEmail.text=text.toEditable()
        binding.firstUpload.setOnClickListener {
            if (confirmInput()){
                dialog.show()
                registerUser()
                uploaddata()
            }
        }


    }


   private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    private fun registerUser() {
        val intent = intent
        val text = intent.getStringExtra(EXTRA_TEXT)
        val pass = intent.getStringExtra(EXTRA_NUMBER)
        if (text.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.createUserWithEmailAndPassword(text, pass).await()


                    withContext(Dispatchers.Main) {
                        dialog.dismiss()

                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@FirstProfileActivity, e.message, Toast.LENGTH_LONG).show()
                        dialog.dismiss()
                    }
                }
            }
        }
    }

    private fun uploaddata() = CoroutineScope(Dispatchers.IO).launch {
        try {
            var name = binding.firstUpdateName.text.toString()
            var email = binding.firstUpdateEmail.text.toString()
            var mobile = binding.firstUpdateMobile.text.toString()
            var experience=binding.firstUpdateExperience.text.toString()
            var coachType=spinnerText
//            var age = first_update_age.text.toString()
//            val userDetails = UserDetails(name, email, mobile, age)
            val coachDetails=CoachDetails(name,email,mobile,"",
                "","",experience,coachType)
            saveUserDetails(coachDetails)


        } catch (e: Exception) {
            Toast.makeText(this@FirstProfileActivity, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserDetails(userDetails: CoachDetails) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val intent = intent
                val text = intent.getStringExtra(EXTRA_TEXT)
                val coachDetailsCollectionRef =
                    db.collection("CoachDetails").document(text)
                coachDetailsCollectionRef.set(userDetails).addOnCompleteListener {
                    dialog.dismiss()
                }.await()
                val freeCountRef = db.collection("FreeCoachSubscriberCount").document(text).set(FreeSubscriberCount("00")).await()
                val countRef = db.collection("CoachSubscriberCount").document(text).set(PaidSubscriberCount("00")).await()





                withContext(Dispatchers.Main) {
//                    Toast.makeText(this@FirstProfileActivity, "data uploaded", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@FirstProfileActivity, e.message, Toast.LENGTH_SHORT).show()
                }

            }
        }



    private fun validateEmail(): Boolean {
        val email: String = binding.firstUpdateEmailInput.editText?.text.toString().trim()
        return if (email.isEmpty()) {
            binding.firstUpdateEmailInput.error = "Field can't be empty"
            dialog.dismiss()
            false
        } else {
            binding.firstUpdateEmailInput.error = null
            true
        }
    }


    private fun validateExperience(): Boolean {
        val experience: String = binding.firstUpdateExperienceInput.editText?.text.toString().trim()
        return if (experience.isEmpty()) {
            binding.firstUpdateExperienceInput.error = "Field can't be empty"
            dialog.dismiss()
            false
        } else {
            binding.firstUpdateExperienceInput.error = null
            true
        }
    }


    private fun validateName(): Boolean {
        val name: String = binding.firstUpdateNameInput.editText?.text.toString().trim()
        return if (name.isEmpty()) {
            binding.firstUpdateNameInput.error = "Field can't be empty"
            dialog.dismiss()
            false
        } else {
            binding.firstUpdateNameInput.error = null
            true
        }
    }

    private fun validateMobile(): Boolean {
        val mobile: String = binding.firstUpdateMobileInput.editText?.text.toString().trim()
        return if (mobile.isEmpty() || mobile.length > 10 || mobile.length < 10) {
            binding.firstUpdateMobileInput.error = "invalid input"
            dialog.dismiss()
            false
        } else {
            binding.firstUpdateMobileInput.error = null
            true
        }
    }




    private fun confirmInput(): Boolean {
        if (!validateEmail() or !validateName() or !validateMobile() or !validateExperience()) {
            return false
        }
        return true
        dialog.show()
    }
    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(mAuthListener)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val clickedItem: SpinnerItemForCoachDetails = parent?.getItemAtPosition(position) as SpinnerItemForCoachDetails
        val clickedCountryName: String =clickedItem.TypeName

        spinnerText = clickedCountryName
        Log.d(TAG, "onItemSelected: ${spinnerText}")

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {


    }
}