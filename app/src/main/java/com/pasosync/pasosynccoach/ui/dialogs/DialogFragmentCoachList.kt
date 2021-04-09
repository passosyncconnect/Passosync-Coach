package com.pasosync.pasosynccoach.ui.dialogs

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.pasosync.pasosynccoach.R

import kotlinx.android.synthetic.main.add_query_layout.*
import kotlinx.android.synthetic.main.add_query_layout.view.*


private const val TAG = "DialogFragmentCoachList"

class DialogFragmentCoachList : DialogFragment() {

//    val user = FirebaseAuth.getInstance().currentUser
//    var coachList = arrayListOf<ExploreCoachList>()
//
//    private val db = FirebaseFirestore.getInstance()

    interface OnInputSelected {
        fun sendInput(input: String?,timeStamp:String?,millis:Long?,byte:ByteArray?)
    }

    var mOnInputSelected: OnInputSelected? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.add_query_layout, container, false)
        val bundle: Bundle? = arguments
        val time = bundle?.getString("key")
        view.heading.text = "Add a Comment at ${time}"
        Log.d(TAG, "onCreateView: ${time}")


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        this.dialog?.setTitle("Add a Comment")





        view.action_cancel.setOnClickListener {
            dialog?.dismiss()
        }
        view.action_ok.setOnClickListener {
            val string=etAddQuery.text.toString()
            if (string.isEmpty()){
                Toast.makeText(requireContext(), "Enter Comment", Toast.LENGTH_SHORT).show()
            }else{
                val string=etAddQuery.text.toString()


                val bundleTime: Bundle? = arguments
                // val videoProgressDetails=VideoProgressDetails()
                val byteArray=bundleTime?.getByteArray("byte")

                mOnInputSelected?.sendInput(string,bundleTime?.getString("key"),bundleTime?.getLong("mili"),byteArray)
                dialog?.dismiss()
            }




        }

//        coach_list_dialog.apply {
//            setHasFixedSize(true)
//            layoutManager = LinearLayoutManager(requireContext())
//        }
        // showData()

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mOnInputSelected=targetFragment as OnInputSelected


        }catch (e:Exception){
            Toast.makeText(requireContext(),e.message,Toast.LENGTH_SHORT).show()
        }
    }



}