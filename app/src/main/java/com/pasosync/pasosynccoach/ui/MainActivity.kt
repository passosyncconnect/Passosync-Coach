package com.pasosync.pasosynccoach.ui


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu

import android.view.View

import androidx.appcompat.widget.Toolbar

import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bairwa.newsapp.database.ArticleDatabase
import com.google.firebase.messaging.FirebaseMessaging

import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.Repositories.MainRepository
import com.pasosync.pasosynccoach.databinding.ActivityMainBinding
import com.pasosync.pasosynccoach.databinding.ToolbarBinding
import com.pasosync.pasosynccoach.db.LectureDatabase

import com.pasosync.pasosynccoach.ui.viewmodels.MainViewModelProvidefactory
import com.pasosync.pasosynccoach.ui.viewmodels.MainViewModels

import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    lateinit var viewmodel:MainViewModels
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)



        setContentView(binding.root)
        setSupportActionBar(toolbar as Toolbar?)
        ( toolbar as Toolbar)?.setTitleTextColor(resources.getColor(R.color.orange))
        (toolbar as Toolbar)?.setLogo(R.drawable.toolbarlogin)
        
        FirebaseMessaging.getInstance().subscribeToTopic("adam")
val repository=MainRepository(LectureDatabase(this),ArticleDatabase(this))
        val viewModelProvidefactory=MainViewModelProvidefactory(repository)
        viewmodel=ViewModelProvider(this,viewModelProvidefactory).get(MainViewModels::class.java)

        binding.bottomNavigationView.setupWithNavController(coachNavHostFragment.findNavController())
        binding.bottomNavigationView.setOnNavigationItemReselectedListener { /* NO-OP*/ }

        coachNavHostFragment.findNavController()
            .addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.dashBoardFragment, R.id.subscribersFragment2, R.id.lectureListFragment,R.id.newPostFragment,R.id.draftFragment ->
                        binding.bottomNavigationView.visibility = View.VISIBLE
                    else -> binding.bottomNavigationView.visibility = View.GONE

                }
            }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }
}
