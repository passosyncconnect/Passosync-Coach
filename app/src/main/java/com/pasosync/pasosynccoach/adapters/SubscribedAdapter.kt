package com.pasosync.pasosynccoach.adapters

import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.data.UserDetails
import kotlinx.android.synthetic.main.notification_layout.view.*
import kotlinx.android.synthetic.main.user_list_item.view.*

class SubscribedAdapter(var mList: List<UserDetails>) :
    RecyclerView.Adapter<SubscribedAdapter.ViewHolderSubscribedAdapter>() {
    private val db = FirebaseFirestore.getInstance()

    inner class ViewHolderSubscribedAdapter(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderSubscribedAdapter {
        return ViewHolderSubscribedAdapter(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.user_list_item, parent, false)

        )
    }

    override fun onBindViewHolder(holder: ViewHolderSubscribedAdapter, position: Int) {
        holder.itemView.apply {
//            val nameText = "Name: ${mList[position].userName}"
//            val ss = SpannableString(nameText)
//            val boldSpan = StyleSpan(Typeface.BOLD)
//            ss.setSpan(boldSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//
//            user_list_tv_name.text = mList[position].userName
//
//            val emailText = "Email: ${mList[position].userEmail}"
//            val ssEmail = SpannableString(emailText)
//            val boldSpanEmail = StyleSpan(Typeface.BOLD)
//            ssEmail.setSpan(boldSpanEmail, 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//
//            //user_list_tv_email.text=ssEmail
//
//            val mobileText = "Mobile: ${mList[position].userMobile}"
//            val ssMobile = SpannableString(mobileText)
//            val boldSpanMobile = StyleSpan(Typeface.BOLD)
//            ssMobile.setSpan(boldSpanMobile, 0, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//            // user_list_tv_mobile.text=ssMobile
//
//            val ageText = "Age: ${mList[position].userAge}"
//            val ssAge = SpannableString(ageText)
//            val boldSpanAge = StyleSpan(Typeface.BOLD)
//            ssAge.setSpan(boldSpanAge, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//            user_list_tv_age.text = mList[position].userAge
//
//            Glide.with(context).load(mList[position].userPicUri).placeholder(R.drawable.man)
//                .into(iv_images)



            try {

                val subscriberCountRef = db.collection("UserDetails")
                    .document(mList[position].uid!!).get().addOnSuccessListener {
                        val count = it.get("userName")
                        user_list_tv_name.text = count.toString()


                    }

                val freeSubscriberCountRef = db.collection("UserDetails")
                    .document(mList[position].uid!!).get().addOnSuccessListener {
                        val freeCount = it.getString("userAge")
                            if (freeCount!!.isEmpty()){
                                user_list_tv_age.visibility=View.GONE

                            }else{
                                user_list_tv_age.text = "${freeCount.toString()} yrs"
                                user_list_tv_age.visibility=View.VISIBLE
                            }


                    }
                val nameRef = db.collection("UserDetails")
                    .document(mList[position].uid!!).get().addOnSuccessListener {
                        val freeCount = it.getString("userAbout")
                        user_list_tv_about.text = freeCount.toString()

                    }

                val aboutRef = db.collection("UserDetails")
                    .document(mList[position].uid!!).get().addOnSuccessListener {
                        val freeCount = it.get("userKind")
                        user_list_tv_speciality.text = freeCount.toString()

                    }
                val expRef = db.collection("UserDetails")
                    .document(mList[position].uid!!).get().addOnSuccessListener {
                        val freeCount = it.get("userLevel")

                        user_list_tv_level.text = freeCount.toString()

                    }
                val typeRef = db.collection("UserDetails")
                    .document(mList[position].uid!!).get().addOnSuccessListener {
                        val freeCount = it.get("userPicUri")

                        Glide.with(context).load(freeCount).placeholder(R.drawable.man)
                            .into(iv_images)
                    }


            } catch (e: Exception) {
               // Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                Log.d("Adapter", "onBindViewHolder: ${e.message}")
            }

        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }

}