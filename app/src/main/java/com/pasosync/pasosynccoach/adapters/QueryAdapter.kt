package com.pasosync.pasosynccoach.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.data.QueryDataRV

import kotlinx.android.synthetic.main.query_layout.*
import kotlinx.android.synthetic.main.query_layout.view.*
import kotlinx.android.synthetic.main.video_progress_details_layout.*

class QueryAdapter(var mList: List<QueryDataRV>) :
    RecyclerView.Adapter<QueryAdapter.QueryViewHolder>() {
    val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()

    inner class QueryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private var onEditItemClickListener: ((QueryDataRV) -> Unit)? = null
    private var onItemClickListener: ((QueryDataRV) -> Unit)? = null
    private var onLongItemClickListener: ((QueryDataRV) -> Unit)? = null
    private var onViewImageItemClickListener: ((QueryDataRV) -> Unit)? = null

    fun setOnItemClickListener(listener: (QueryDataRV) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnLongItemClickListener(listener: (QueryDataRV) -> Unit) {
        onLongItemClickListener = listener
    }

    fun setOnEditTheItemClickListener(listener: (QueryDataRV) -> Unit) {
        onEditItemClickListener = listener
    }
    fun setOnViewImageTheItemClickListener(listener: (QueryDataRV) -> Unit) {
        onViewImageItemClickListener = listener
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueryViewHolder {
        return QueryViewHolder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.query_layout, parent, false)
        )


    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: QueryViewHolder, position: Int) {
        holder.itemView.apply {
            tvTimestamp.text = "${mList[position].timestamp} "
            tvQuery.text = mList[position].query



//            deleteQuery.setOnClickListener {
//                onItemClickListener?.let {
//                    it(mList[position])
//                    mList[position].id
//                }
//
//            }



            setOnClickListener {
                onItemClickListener?.let {
                    it(mList[position])
                }
            }

            Glide.with(context).load(mList[position].imgUrl).into(deleteQuery)

            setOnLongClickListener {
//                linearQuery.visibility=View.VISIBLE
//              onLongItemClickListener?.let {
//                  it(mList[position])
//
//              }

                val popupMenu = PopupMenu(context, holder.itemView)

                popupMenu.menuInflater.inflate(R.menu.query_menu, popupMenu.menu)
                popupMenu.setForceShowIcon(true)

                popupMenu.setOnMenuItemClickListener { item ->
//                         Toast.makeText(requireContext(),it.title,Toast.LENGTH_SHORT).show()
                    when (item.itemId) {
                        R.id.edit -> {
                            Toast.makeText(context, item.title, Toast.LENGTH_SHORT)
                                .show()
                            onEditItemClickListener?.let {
                                it(mList[position])
                            }


                        }
                        R.id.delete -> {


                            val queryRef =
                                db.collection("UserDetails").document(user?.uid!!)
                                    .collection("GameProgressVideoDetails")
                                    .document(mList[position].parentId!!)
                                    .collection("QueryDataList").document(mList[position].id!!)
                                    .delete()

                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            "Successfully deleted please Refresh",
                                            Toast.LENGTH_SHORT
                                        ).show()


                                    }

                        }
                        R.id.analyze -> {

                            onLongItemClickListener?.let {
                                it(mList[position])
                            }
                        }
                        R.id.seeImage->{

                            onViewImageItemClickListener?.let {
                                it(mList[position])
                            }

                        }

                    }

                    return@setOnMenuItemClickListener true
                }
                popupMenu.show()

                return@setOnLongClickListener true
            }

        }


    }

    override fun getItemCount(): Int {
        return mList.size

    }



}