package com.pasosync.pasosynccoach.other

import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast

class CustomSelectorSpinner : AdapterView.OnItemSelectedListener {
    private  val TAG = "CustomSelectorSpinner"
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        Toast.makeText(
            parent?.context,
            parent?.getItemAtPosition(position).toString(),
            Toast.LENGTH_SHORT
        ).show()
        Log.d(TAG, "onItemSelected:${parent?.getItemAtPosition(position).toString()} ")
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}