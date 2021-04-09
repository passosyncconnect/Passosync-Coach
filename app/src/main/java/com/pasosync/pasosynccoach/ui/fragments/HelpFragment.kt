package com.pasosync.pasosynccoach.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.databinding.FargmentHelpBinding
import kotlinx.android.synthetic.main.fargment_help.*


class HelpFragment: Fragment(R.layout.fargment_help) {
    private lateinit var binding: FargmentHelpBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fargment_help, container, false)
        setHasOptionsMenu(true)
        return v

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FargmentHelpBinding.bind(view)
        (activity as AppCompatActivity).supportActionBar?.title = " "

binding.tvHelpEmailWrite.setOnClickListener {
    sendMail()
}
    }

    private fun sendMail(){
        val emailaddress = arrayOf<String>("Pasosync@pasosync.com")

        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_EMAIL,emailaddress)
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.help_title))
        intent.putExtra(Intent.EXTRA_TEXT,getString(R.string.message))
        intent.type = "message/rfc822"
        startActivity(Intent.createChooser(intent, "Choose an email client"))

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

}