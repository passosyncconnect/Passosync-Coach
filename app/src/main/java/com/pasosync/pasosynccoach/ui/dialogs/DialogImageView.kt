package com.pasosync.pasosynccoach.ui.dialogs

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pasosync.pasosynccoach.R

import kotlinx.android.synthetic.main.add_query_layout.view.*
import kotlinx.android.synthetic.main.dialog_image_view.*
import kotlinx.android.synthetic.main.dialog_image_view.view.*


private const val TAG = "DialogImageView"
class DialogImageView:DialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_image_view, container, false)
//        val bundle: Bundle? = arguments
//        val time = bundle?.getString("key")
//        view.heading.text = "Add a Query at ${time}"
//        Log.d(TAG, "onCreateView: ${time}")
        val bundle: Bundle? = arguments








        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle: Bundle? = arguments
        val time = bundle?.getString("image")
        Log.d(TAG, "edit:${bundle?.getString("edit")} ")
        Log.d(TAG, "onCreateView: ${time}")

        etUpdateAddQuery.text=bundle?.getString("edit")!!.toEditable()
         update_action_cancel.setOnClickListener {
             dialog?.dismiss()
         }

        updateAction_ok.setOnClickListener {
            FirebaseFirestore.getInstance().collection("UserDetails")
                .document(FirebaseAuth.getInstance().currentUser?.uid!!)
                .collection("GameProgressVideoDetails")
                .document(bundle?.getString("parent")!!).collection("QueryDataList")
                .document(bundle?.getString("id")!!).update("query",etUpdateAddQuery.text.toString())


            dialog?.dismiss()
             Toast.makeText(requireContext(),"Swipe down to see the update",Toast.LENGTH_SHORT).show()

        }






//        time?.let {
////            Glide.with(view).load(it).fitCenter().into(dialogIvProgressYt)
//            view.dialogIvProgressYt.setImageURI(it.toUri())
//
//        }








    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)





}