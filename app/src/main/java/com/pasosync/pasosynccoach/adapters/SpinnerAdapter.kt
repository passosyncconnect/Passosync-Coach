package com.pasosync.pasosynccoach.adapters


import android.content.Context
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.data.SpinnerItem
import kotlinx.android.synthetic.main.custom_spinner_layout.view.*
import kotlinx.android.synthetic.main.spinner_layout.view.*


class SpinnerAdapter(context: Context,spinnerList: List<SpinnerItem>) :
    ArrayAdapter<SpinnerItem>(context, 0, spinnerList) {

    override fun getView(position: Int,  convertView: View?, parent: ViewGroup): View {
        val view=convertView?:LayoutInflater.from(parent.context).inflate(
            R.layout.custom_spinner_layout,parent,false
        )
        val item=getItem(position)
        view.ivSpinnerLayout.setImageResource(item!!.flagImage)
        view.tvSpinnerLayout.text=item.TypeName


     return view
    }

    override fun getDropDownView(
        position: Int,
         convertView: View?,
        parent: ViewGroup
    ): View {
        return initView(position, convertView, parent)

    }

private fun initView(position: Int, convertView: View?, parent: ViewGroup):View {
    val spinnerItem=getItem(position)
    val view=convertView?:LayoutInflater.from(parent.context).inflate(R.layout.spinner_layout,parent,false)
    view.image_view_flag.setImageResource(spinnerItem!!.flagImage)
    view.spinner_text.text=spinnerItem.TypeName

    return view
}

}