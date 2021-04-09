package com.pasosync.pasosynccoach.ui.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.onNavDestinationSelected
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.adapters.SpinnerAdapter
import com.pasosync.pasosynccoach.data.*
import com.pasosync.pasosynccoach.databinding.FargmentNewpostBinding
import com.pasosync.pasosynccoach.db.Lectures
import com.pasosync.pasosynccoach.other.Constant
import com.pasosync.pasosynccoach.other.Permissions
import com.pasosync.pasosynccoach.ui.MainActivity
import com.pasosync.pasosynccoach.ui.viewmodels.MainViewModels
import kotlinx.android.synthetic.main.fargment_newpost.*

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
const val TOPIC = "/topics/myTopic"

class NewPostFragment : Fragment(R.layout.fargment_newpost), AdapterView.OnItemSelectedListener {


    lateinit var viewModel: MainViewModels
    val args: NewPostFragmentArgs by navArgs()
    var lectureList = arrayListOf<NewLectureDetails>()
    var spinnerList = arrayListOf<SpinnerItem?>()


    var spinnerText = ""
    var typeSpinnerText = ""
    var lectureImageUri: Uri? = null
    var lectureVideoUri: Uri? = null
    var lecturePdfUri: Uri? = null
    var videourlLecture: String? = null
    var imageurlLecture: String? = null
    var pdfUrlLecture: String? = null
    private val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()

    private val draftCollectionRef = db.collection("CoachLectureList")
        .document(user?.uid!!).collection("DraftLecture")

    private val coachDetailsCollectionRef =
        db.collection("UserDetails").document(user?.uid!!)


    private val coachLectureCollectionRef =
        db.collection("CoachLectureList").document(user?.uid!!).collection(
            "PaidLecture"
        )
    private val FreecoachLectureCollectionRef =
        db.collection("CoachLectureList").document(user?.uid!!).collection(
            "FreeLecture"
        )
    private val AllcoachLectureCollectionRef =
        db.collection("CoachLectureList").document(user?.uid!!).collection(
            "Lecture"
        )

    val coachNewPostStorageReference = Firebase.storage.reference.child("CoachNewPostData")
    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fargment_newpost, container, false)
        setHasOptionsMenu(true)
        return v

    }


    private fun updateDraft() = CoroutineScope(Dispatchers.IO).launch {
        try {
            var title = et_lectureTitle.text.toString()
            var description = et_lecture_details.text.toString()
            val id = args.draft.id

            val draftLectureDetails = DraftLectureDetails(title, description, id)
            draftCollectionRef.document(id!!).set(draftLectureDetails).await()
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "updated successfully", Toast.LENGTH_SHORT)
                    .show()
                findNavController().navigate(R.id.action_newPostFragment_to_draftFragment)
            }

        } catch (e: Exception) {

        }
    }

    private fun draftLecture() = CoroutineScope(Dispatchers.IO).launch {
        try {
            var title = et_lectureTitle.text.toString()
            var description = et_lecture_details.text.toString()
            val idnew = UUID.randomUUID().toString()
            val draftLectureDetails = DraftLectureDetails(title, description, idnew)
            draftCollectionRef.document(idnew).set(draftLectureDetails).await()
            withContext(Dispatchers.Main) {
                android.widget.Toast.makeText(
                    requireContext(),
                    "Saved in Draft Successfully",
                    Toast.LENGTH_SHORT
                )
                    .show()
                findNavController().navigate(R.id.action_newPostFragment_to_draftFragment)
            }

            
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun setUpCustomSpinner() {
        val adapter = SpinnerAdapter(requireContext(), Types.list!!)

        spinner.adapter = adapter
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel = (activity as MainActivity).viewmodel
        builder = AlertDialog.Builder(requireContext())

        try {

            et_lectureTitle.text = args.draft.lectureTitleCoach?.toEditable()
            et_lecture_details.text = args.draft.lectureContentCoach?.toEditable()

        } catch (e: Exception) {
//             Toast.makeText(requireContext(),"nodata",Toast.LENGTH_SHORT).show()
        }

//        initSpinnerList()

        setUpCustomSpinner()
        spinner.onItemSelectedListener = this


        tv_new_post_draftList.setOnClickListener {
            findNavController().navigate(R.id.action_newPostFragment_to_draftFragment)

        }

//        radioGroup.setOnCheckedChangeListener { group, checkedId ->
//            var rb = view.findViewById<RadioButton>(checkedId)
//            if (rb != null) {
//                Log.d(TAG, "onViewCreated: ${rb.text.toString()}")
//                typeSpinnerText = rb.text.toString()
//                Log.d(TAG, "onViewCreated:$typeSpinnerText")
//            } else {
//                typeSpinnerText = "Free"
//            }
//        }


        builder.setCancelable(false) // if you want user to wait for some process to finish,
        (activity as AppCompatActivity).supportActionBar?.title = " "

        builder.setView(R.layout.layout_uploading_dialog)
        dialog = builder.create()


        tv_new_post_draft.setOnClickListener {
            if (confirmInput()) {
                draftLecture()
            }

        }
        cardUpdatePost.setOnClickListener {
            if (confirmInput()) {
                updateDraft()
            }
        }


        et_lecture_details.isVerticalScrollBarEnabled = true
        et_lecture_details.movementMethod = ScrollingMovementMethod()


        new_post_image.setOnClickListener {
            if (Permissions.hasWritingPermissions(requireContext())) {
                chooseImageForLecture()

            } else {
                Toast.makeText(
                    requireContext(),
                    "You need to accept the permissions",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

//        btn_new_post_choose_image.setOnClickListener {
//            if (lectureImageUri == null) {
//                Toast.makeText(requireContext(), "Please Select an Image", Toast.LENGTH_SHORT)
//                    .show()
//            } else {
//                uploadImageToStorage()
//            }
//
//        }
//        btn_new_post_choose_pdf.setOnClickListener {
//            if (lecturePdfUri == null) {
//                Toast.makeText(requireContext(), "Please Select a pdf", Toast.LENGTH_SHORT).show()
//            } else {
//                uploadPdfToStorage()
//            }
//        }

//        btn_new_post_choose_video.setOnClickListener {
//            if (lectureVideoUri == null) {
//                Toast.makeText(requireContext(), "Please Select a video", Toast.LENGTH_SHORT).show()
//            } else {
//                uploadVideoToStorage()
//            }
//        }

        new_post_video.setOnClickListener {
            if (Permissions.hasWritingPermissions(requireContext())) {
                chooseVideoForNewPost()

            } else {
                Toast.makeText(
                    requireContext(),
                    "You need to accept the permissions",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
//        new_post_pdf.setOnClickListener {
//            if (Permissions.hasWritingPermissions(requireContext())) {
//                choosePdfForLecture()
//            } else {
//                Toast.makeText(
//                    requireContext(),
//                    "You need to accept the permissions",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }


        btn_upload_new_post.setOnClickListener {
            if (confirmInput()) {
                uploadLectureToStorage()

            } else {
                Toast.makeText(requireContext(), "fill all details", Toast.LENGTH_SHORT).show()
            }


        }


    }


    private fun validateLectureTitle(): Boolean {
        val title: String = Lecture_title_input.editText?.text.toString().trim()
        return if (title.isEmpty()) {
            Lecture_title_input.error = "Field can't be empty"
            false
        } else {
            Lecture_title_input.error = null
            true
        }
    }

    private fun validateLectureContent(): Boolean {
        val details: String = Lecture_details_input.editText?.text.toString().trim()
        return if (details.isEmpty()) {
            Lecture_details_input.error = "Field can't be empty"
            false
        } else {
            Lecture_details_input.error = null
            true
        }

    }

    private fun confirmInput(): Boolean {
        if (!validateLectureTitle() or !validateLectureContent()) {

            return false
        }
        return true
    }

    private fun chooseVideoForNewPost() {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, Constant.PICK_VIDEO)
    }

    private fun chooseImageForLecture() {
        Intent(Intent.ACTION_GET_CONTENT).also {
            it.type = "image/*"
            startActivityForResult(it, Constant.REQUEST_CODE_IMAGE_PICK)
        }
    }

    private fun choosePdfForLecture() {
        Intent(Intent.ACTION_GET_CONTENT).also {
            it.type = "pdf/*"
            startActivityForResult(it, Constant.PICK_PDF)
        }
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    private fun uploadImageToStorage() = CoroutineScope(Dispatchers.IO).launch {
        try {


            lectureImageUri?.let {
                val imageFileName =
                    coachNewPostStorageReference.child("Image${System.currentTimeMillis()}" + lectureImageUri?.lastPathSegment)
                imageFileName.putFile(it).addOnProgressListener {
                    dialog.show()

                }.addOnSuccessListener { taskSnapshot ->
                    imageFileName.downloadUrl.addOnSuccessListener {
                        imageurlLecture = it.toString()


                    }
                }.await()
            }
            withContext(Dispatchers.Main) {
                dialog.dismiss()
                Toast.makeText(
                    requireContext(),
                    "Image Uploaded Successfully",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }

    }

    private fun uploadPdfToStorage() = CoroutineScope(Dispatchers.IO).launch {
        try {
            lecturePdfUri?.let {
                val pdfFileName =
                    coachNewPostStorageReference.child("Pdf${System.currentTimeMillis()}" + lecturePdfUri?.lastPathSegment)
                pdfFileName.putFile(it).addOnProgressListener {
                    dialog.show()

                }.addOnSuccessListener { taskSnapshot ->
                    pdfFileName.downloadUrl.addOnSuccessListener {
                        pdfUrlLecture = it.toString()

                    }
                }.await()

            }
            withContext(Dispatchers.Main) {
                dialog.dismiss()
                Toast.makeText(
                    requireContext(),
                    "Pdf Uploaded Successfully",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }


        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }


    private fun uploadVideoToStorage() = CoroutineScope(Dispatchers.IO).launch {
        try {
            lectureVideoUri?.let {
                val videoFileName =
                    coachNewPostStorageReference.child("Video${System.currentTimeMillis()}" + lectureVideoUri?.lastPathSegment)
                videoFileName.putFile(it).addOnProgressListener {
                    dialog.show()

                }.addOnSuccessListener { taskSnapshot ->
                    videoFileName.downloadUrl.addOnSuccessListener {
                        videourlLecture = it.toString()

                    }
                }.await()

            }
            withContext(Dispatchers.Main) {
                dialog.dismiss()
                Toast.makeText(
                    requireContext(),
                    "Video Uploaded Successfully",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadLectureToStorage() = CoroutineScope(Dispatchers.Main).launch {

        try {


            val lectureName = et_lectureTitle.text.toString()
            val lectureBody = et_lecture_details.text.toString()
            val searchText = spinnerText.toString()
            val type = typeSpinnerText.toString()
            val lecturePdf = pdfUrlLecture?.toString()
            val lectureImage = imageurlLecture?.toString()
            val lectureVideo = videourlLecture?.toString()
            val calendar = Calendar.getInstance()
            val currentDate: String =
                DateFormat.getDateInstance().format(calendar.time)
            val id = coachLectureCollectionRef.document().id
            val newLectureDetails = NewLectureDetails(
                id,
                lectureName,
                lectureBody,
                lectureImage,
                lectureVideo,
                lecturePdf,
                currentDate, searchText, type, System.currentTimeMillis()
            )
            val data = coachLectureCollectionRef.document(id).set(newLectureDetails)
            Snackbar.make(requireView(), "Uploaded successfully", Snackbar.LENGTH_SHORT)
                .show()
            findNavController().navigate(R.id.action_newPostFragment_to_dashBoardFragment)


            val lecture = Lectures(lectureName, currentDate, lectureBody)
            val notifyCollectionRef =
                db.collection("Notification").document(user?.uid!!).collection(
                    "NotificationLecture"
                )
            var profile_name: String? = null
            val query = coachDetailsCollectionRef.get().addOnSuccessListener { document ->
                val profileName = document.getString("userName")
                profile_name = profileName
            }.await()


//            if (type == "Premium") {
//                val id=coachLectureCollectionRef.document().id
//                val data = coachLectureCollectionRef.add(newLectureDetails)
//                Snackbar.make(requireView(), "Uploaded successfully", Snackbar.LENGTH_SHORT)
//                    .show()
//                findNavController().navigate(R.id.action_newPostFragment_to_dashBoardFragment)
//
//            } else if (type == "Free") {
//                val freedata = FreecoachLectureCollectionRef.add(newLectureDetails)
//                Snackbar.make(requireView(), "Uploaded successfully", Snackbar.LENGTH_SHORT)
//                    .show()
//                findNavController().navigate(R.id.action_newPostFragment_to_dashBoardFragment)
//
//
//            } else {
//                Toast.makeText(
//                    requireContext(),
//                    "Please choose between \n Post and Premium",
//                    Toast.LENGTH_SHORT
//                ).show()
//
//            }

            


        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent?.getItemAtPosition(position)?.equals("Choose a Category")!!) {

        } else {


            val text = parent!!.getItemAtPosition(position).toString()

            val clickedItem: SpinnerItem = parent.getItemAtPosition(position) as SpinnerItem
            val clickedCountryName: String = clickedItem.TypeName

            spinnerText = clickedCountryName

        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_IMAGE_PICK) {
            data?.data?.let {
                lectureImageUri = it
                new_post_image.setImageResource(R.drawable.imageafterupload)
                Toast.makeText(
                    requireContext(),
                    "Your Image is Uploading\n Please wait",
                    Toast.LENGTH_SHORT
                ).show()
                uploadImageToStorage()
            }
        }
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.PICK_VIDEO) {
            data?.data?.let {
                lectureVideoUri = it
                new_post_video.setImageResource(R.drawable.videofteruload)
                Toast.makeText(
                    requireContext(),
                    "Your video is Uploading\n Please wait",
                    Toast.LENGTH_SHORT
                ).show()
                uploadVideoToStorage()
            }
        }
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.PICK_PDF) {
            data?.data?.let {
                lecturePdfUri = it
                // new_post_pdf.setImageResource(R.drawable.checkedsmall)
                Toast.makeText(requireContext(), "Your pdf is Ready to upload", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)

    }
}
