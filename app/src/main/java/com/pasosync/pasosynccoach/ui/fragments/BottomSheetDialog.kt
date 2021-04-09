package com.pasosync.pasosynccoach.ui.fragments


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.Nullable
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasosync.pasosynccoach.R


class BottomSheetDialog:BottomSheetDialogFragment() {

    private var mListener: BottomSheetListener? = null

    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        val v: View =inflater.inflate(R.layout.bottom_sheet_layout,container,false)
        val button1: Button = v.findViewById(R.id.button1)
        val button2: Button = v.findViewById(R.id.button2)

        button1.setOnClickListener {
            mListener!!.onButtonClicked("Button 1 clicked")
            dismiss()
        }
        button2.setOnClickListener {
            mListener!!.onPostButtonClicked()
            dismiss()
        }

        return v
    }

    interface BottomSheetListener {
        fun onButtonClicked(text: String?)

        fun onPostButtonClicked()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = try {
            context as BottomSheetListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                context.toString()
                    .toString() + " must implement BottomSheetListener"
            )
        }
    }
}