package com.pasosync.pasosynccoach.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pasosync.pasosynccoach.R

class CoachPlusActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coach_plus)
    }


    override fun onBackPressed() {
        super.onBackPressed()

        Intent(this,MainActivity::class.java).also {
            startActivity(it)
        }

    }
}