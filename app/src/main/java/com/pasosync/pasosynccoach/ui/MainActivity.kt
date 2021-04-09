package com.pasosync.pasosynccoach.ui


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem

import android.view.View
import android.widget.Toast

import androidx.appcompat.widget.Toolbar

import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.pasosync.pasosynccoach.db.ArticleDatabase
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.repositories.DefaultMainRepository
import com.pasosync.pasosynccoach.repositories.MainRepos
import com.pasosync.pasosynccoach.repositories.MainRepository
import com.pasosync.pasosynccoach.databinding.ActivityMainBinding
import com.pasosync.pasosynccoach.db.LectureDatabase
import com.pasosync.pasosynccoach.ui.fragments.BottomSheetDialog

import com.pasosync.pasosynccoach.ui.viewmodels.MainViewModelProvidefactory
import com.pasosync.pasosynccoach.ui.viewmodels.MainViewModels

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_control.*
import kotlinx.android.synthetic.main.toolbar.*


class MainActivity : AppCompatActivity(),BottomSheetDialog.BottomSheetListener {
    lateinit var viewmodel: MainViewModels
    private lateinit var binding: ActivityMainBinding
    val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()
    private val coachDetailsCollectionRef =
        db.collection("UserDetails").document(user?.uid!!)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.bottomNavigationView.background = null
        binding.bottomNavigationView.menu.getItem(2).isEnabled = false



        setContentView(binding.root)
        setSupportActionBar(toolbar as Toolbar?)
        (toolbar as Toolbar)?.setTitleTextColor(resources.getColor(R.color.orange))
        (toolbar as Toolbar)?.setLogo(R.drawable.logotoolbar)

        FirebaseMessaging.getInstance().subscribeToTopic("adam")
        val repository = MainRepository(LectureDatabase(this), ArticleDatabase(this))
        val mainRepos = DefaultMainRepository() as MainRepos
        val viewModelProvidefactory = MainViewModelProvidefactory(application,repository,mainRepos)
        viewmodel = ViewModelProvider(this, viewModelProvidefactory).get(MainViewModels::class.java)

        binding.bottomNavigationView.setupWithNavController(coachNavHostFragment.findNavController())


        val profileUserPic = coachDetailsCollectionRef.addSnapshotListener { value, error ->

            try {
                if (value!=null){
                    val pic= value?.getString("userPicUri")
                    Glide.with(this).load(pic).placeholder(R.drawable.man).into(profileIvimage)
                }
                else{
                    Log.e("PROFILE", "onCreate: ${error.toString()}", )
                }
            } catch (e: Exception) {
                Log.e("PROFILE", "onCreate: ${error.toString()}", )
            }

        }

//        profileIvimage.setOnClickListener {
//
//        }


        binding.fab.setOnClickListener {

//            coachNavHostFragment.findNavController().navigate(R.id.newPostFragment)
            if (user!!.isEmailVerified){
                val bottomSheet=BottomSheetDialog()
                bottomSheet.show(supportFragmentManager,"bottomSheet")
            }
            else{

                 Toast.makeText(this,"Please logout and login again \n after email verification ",Toast.LENGTH_LONG).show()
            }


        }


        coachNavHostFragment.findNavController()
            .addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.dashBoardFragment, R.id.subscribersFragment2,R.id.allCoachListFragment, R.id.lectureListFragment, R.id.draftFragment -> {
                        binding.bottomNavigationView.visibility = View.VISIBLE
                        binding.fab.visibility = View.VISIBLE
                        binding.bottomAppBar.visibility=View.VISIBLE
                    }
                    R.id.newPostUploadFragment,R.id.newPostFragment,R.id.profileFragment2,R.id.updateProfileFragment2,->{
                        binding.fab.visibility=View.GONE

                    }
                    R.id.coachPlusFragment->{
                        binding.fab.visibility=View.GONE

                    }



                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)


        profileIvimage.setOnClickListener {
            coachDetailsCollectionRef.get().addOnSuccessListener {
                val name = it.getString("userName")
                Toast.makeText(this, name, Toast.LENGTH_SHORT).show()
             //  control_frag.findNavController().navigate(R.id.globalActionToProfile)
            }

        }
        return true
    }

    override fun onButtonClicked(text: String?) {
       coachNavHostFragment.findNavController().navigate(R.id.newPostUploadFragment)
    }

    override fun onPostButtonClicked() {
        coachNavHostFragment.findNavController().navigate(R.id.newPostFragment)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.logout_coach -> {
                FirebaseAuth.getInstance().signOut()
                Intent(this, LoginActivity::class.java).also {
                    startActivity(it)
                }
                finish()
            }
        }
        return super.onOptionsItemSelected(item)

    }
}
