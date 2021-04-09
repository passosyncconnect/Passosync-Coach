package com.pasosync.pasosynccoach.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.databinding.ActivityLoginBinding

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog
    lateinit var topAnimation:Animation
    private lateinit var binding: ActivityLoginBinding
    var EXTRA_TEXT = "com.example.application.example.EXTRA_TEXT"
    var EXTRA_NUMBER = "com.example.application.example.EXTRA_NUMBER"
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
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        supportActionBar?.hide()
        topAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.top_animation)
        binding.registerLogo.animation=topAnimation

        binding.goToLogin.fontFeatureSettings


        binding.goToLogin.setOnClickListener {
            Intent(this, AlreadyRegisterActivity::class.java).also {
                startActivity(it)
            }
        }
        val email = binding.etEmail.text.toString().toLowerCase().trim()
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        binding.etEmail.addTextChangedListener {
            if (email.matches(emailPattern.toRegex()) && email.isNotEmpty()) {
                binding.emailred.error = "Valid Email Address"
            } else {
                binding.emailred.error = null
            }
        }
        binding.signInButton.setOnClickListener {
            if (confirmInput()){
                val email = binding.etEmail.text.toString().toLowerCase().trim()
                val password = binding.etPass.text.toString().trim()
                val intent = Intent(this, FirstProfileActivity::class.java)
                intent.putExtra(EXTRA_TEXT, email)
                intent.putExtra(EXTRA_NUMBER, password)
                startActivity(intent)



//                dialog.show()
                //registerUser()
            }
            else{
                 Toast.makeText(this,"Make sure your email address is verified Or your input is correct ",Toast.LENGTH_LONG).show()
            }
        }
        builder = AlertDialog.Builder(this)
        builder.setCancelable(false) // if you want user to wait for some process to finish,

        builder.setView(R.layout.layout_loading_dialog)
        dialog = builder.create()


        mAuthListener = AuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
                dialog.dismiss()
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            }
        }


    }

    override fun onBackPressed() {
        super.onBackPressed()
        topAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.top_animation)
        binding.registerLogo.animation=topAnimation
    }

    private fun validateEmail(): Boolean {
        val email: String = binding.etEmail.text.toString().trim()
        return if (email.isEmpty()) {
            binding.emailred.error = "Field can't be empty"
            dialog.dismiss()
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailred.error="Please Enter a valid Email"
            return false

        } else {
            binding.emailred.error = null
            true
        }
    }
    private fun validatePassword(): Boolean {
        val password: String = binding.etPass.text.toString().trim()
        return if (password.isEmpty()) {
            binding.pass.error = "Field can't be empty"
            dialog.dismiss()
            false
        }  else {
            binding.pass.error = null
            true
        }
    }




    private fun confirmInput():Boolean {
        if (!validateEmail() or !validatePassword()) {
            return false
        }
        return true
        dialog.show()
    }

    private fun registerUser() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPass.text.toString().trim()


        if (email.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.createUserWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main) {
                        dialog.dismiss()

                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_LONG).show()
                        dialog.dismiss()
                    }
                }
            }
        }
    }

//    private fun googleAuthForFirebase(account: GoogleSignInAccount) {
//        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                auth.signInWithCredential(credentials).await()
//                withContext(Dispatchers.Main) {
//                    dialog.dismiss()
//                    Toast.makeText(this@LoginActivity, "Successfully logged in", Toast.LENGTH_LONG)
//                        .show()
//                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
//                    startActivity(intent)
//                    finish()
//                }
//            } catch (e: Exception) {
//                withContext(Dispatchers.Main) {
//                    dialog.dismiss()
//                    Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_LONG).show()
//                }
//            }
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == REQUEST_CODE_SIGN_IN && data!=null) {
//            val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
//            account?.let {
//                googleAuthForFirebase(it)
//            }
//        }
//        else{
//            dialog.dismiss()
//        }
//    }


}


