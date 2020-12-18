package com.pasosync.pasosynccoach.ui

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Patterns

import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener

import com.google.firebase.auth.FirebaseAuth
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.databinding.ActivityAlreadyRegisterBinding

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AlreadyRegisterActivity : AppCompatActivity() {
    private val TAG = "AlreadyRegisterActivity"
    lateinit var auth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog
    lateinit var topAnimation: Animation
    private lateinit var binding: ActivityAlreadyRegisterBinding
    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(mAuthListener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding= ActivityAlreadyRegisterBinding.inflate(layoutInflater)

        setContentView(binding.root)
        builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setView(R.layout.layout_loading_dialog)
        dialog = builder.create()
        topAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.top_animation)
        binding.loginLogo.animation = topAnimation
        auth = FirebaseAuth.getInstance()
        binding.goToRegister.setOnClickListener {
            Intent(this, LoginActivity::class.java).also {
                startActivity(it)
            }
        }


        supportActionBar?.hide()
        val email = binding.alEtEmail.text.toString().trim()
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        binding.alEtEmail.addTextChangedListener {
            if (email.matches(emailPattern.toRegex()) && email.isNotEmpty()) {
                binding.alreadyEmailred.error = "Valid Email Address"
            } else {
                binding.alreadyEmailred.error = null
            }
        }
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
                dialog.dismiss()
                startActivity(Intent(this@AlreadyRegisterActivity, MainActivity::class.java))
                finish()
            }
        }

        binding.alSignInButton.setOnClickListener {
            if (confirmInput()) {
                dialog.show()
                loginUser()
            }
        }

        binding.forgotPass.setOnClickListener {
            showRecoverPasswordDialog()
        }

    }

    private fun showRecoverPasswordDialog() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Recover Password")
        var linearLayout = LinearLayout(this)
        builder.setIcon(R.mipmap.ic_add_person_round)
        builder.setMessage("You will get an email with a link to reset password")
       var layoutParams =ViewGroup.LayoutParams.MATCH_PARENT

        val email_et = EditText(this)
        email_et.hint = "Enter your registered email                  "
        email_et.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS


        linearLayout.addView(email_et)

        builder.setView(linearLayout)


        builder.setPositiveButton("Send Link", DialogInterface.OnClickListener { dialog, which ->

            val email = email_et.text.toString().toLowerCase().trim()
            beginRecovery(email)


        }).setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->

            dialog.dismiss()

        })
        builder.create().show()

    }

    private fun beginRecovery(email: String) {

        dialog.show()


        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            dialog.dismiss()
            if (it.isSuccessful) {
                Toast.makeText(this, "Email Sent", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Something went wrong...", Toast.LENGTH_SHORT).show()
            }


        }.addOnFailureListener {
            dialog.dismiss()
            Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        topAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.top_animation)
        binding.loginLogo.animation = topAnimation
    }

    private fun validateEmail(): Boolean {
        val email: String = binding.alEtEmail.text.toString().trim()
        return if (email.isEmpty()) {
            binding.alreadyEmailred.error = "Field can't be empty"
            dialog.dismiss()
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.alreadyEmailred.error = "Please Enter a valid Email"
            return false

        } else {
            binding.alreadyEmailred.error = null
            true
        }
    }

    private fun validatePassword(): Boolean {
        val password: String = binding.alEtPass.text.toString().trim()
        return if (password.isEmpty()) {
            binding.alPass.error = "Field can't be empty"
            dialog.dismiss()
            false
        } else {
            binding.alPass.error = null
            true
        }
    }

    private fun confirmInput(): Boolean {
        if (!validateEmail() or !validatePassword()) {
            return false
        }
        return true
        dialog.show()
    }

    private fun loginUser() {
        val email = binding.alEtEmail.text.toString().trim()
        val password = binding.alEtPass.text.toString().trim()


        if (email.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.signInWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main) {
                        dialog.dismiss()

                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AlreadyRegisterActivity, e.message, Toast.LENGTH_LONG)
                            .show()
                        dialog.dismiss()
                    }
                }
            }
        }
    }


}