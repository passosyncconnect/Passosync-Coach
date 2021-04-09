package com.pasosync.pasosynccoach.ui.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.adapters.QueryAdapter
import com.pasosync.pasosynccoach.data.QueryDataRV
import com.pasosync.pasosynccoach.ui.dialogs.DialogFragmentCoachList
import com.pasosync.pasosynccoach.ui.dialogs.DialogImageView
import kotlinx.android.synthetic.main.activity_coach_plus.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.coachNavHostFragment
import kotlinx.android.synthetic.main.video_progress_details_layout.*
import kotlinx.android.synthetic.main.video_progress_details_layout_analyitcs.*
import java.io.ByteArrayOutputStream


private const val TAG = "VideoProgressDetailsAna"
class VideoProgressDetailsAnalytics : Fragment(R.layout.video_progress_details_layout_analyitcs),
    DialogFragmentCoachList.OnInputSelected {

    var fireUri: Uri? = null
    val args: VideoProgressDetailsAnalyticsArgs by navArgs()
    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog
    lateinit var mediaController: MediaController
    val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()

    val connectUserProgressImage = Firebase.storage.reference.child("UserProgressImage")


//    private var mp: MediaPlayer? = null

    var queryList = arrayListOf<QueryDataRV>()
    var timeAtPause = 0L


    override fun sendInput(input: String?, timeStamp: String?, millis: Long?, byte: ByteArray?) {
        Log.d(TAG, "sendInput: found Incoming Input:${input} ${timeStamp}")

        val gameQueryCollectionRef =
            db.collection("UserDetails").document(user?.uid!!)
                .collection("GameProgressVideoDetails")
                .document(args.video.id!!).collection("QueryDataList")
        val practiceQueryCollectionRef =
            db.collection("UserDetails").document(user?.uid!!)
                .collection("PracticeProgressVideoDetails").document(args.video.id!!)
                .collection("QueryDataList")


        val bmp = BitmapFactory.decodeByteArray(byte, 0, byte!!.size)
        val uri = getImageUri(requireContext(), bmp)

        if (args.video.type == "Game") {

            val filename =
                connectUserProgressImage.child("file" + System.currentTimeMillis())
            if (fireUri != null) {
                filename.putFile(fireUri!!).addOnProgressListener { snapshot ->
                    dialog.show()

                }.addOnSuccessListener {
                    filename.downloadUrl.addOnSuccessListener {
                        dialog.dismiss()
                        Log.d(TAG, "sendInput: ${it.toString()}")
                        val id = gameQueryCollectionRef.document().id

                        val data = gameQueryCollectionRef.document(id)
                            .set(
                                QueryDataRV(
                                    timeStamp,
                                    input,
                                    millis,
                                    null,
                                    id,
                                    it.toString(),
                                    args.video.id,
                                    args.video.type
                                )
                            )

                        Toast.makeText(requireContext(), "Successful", Toast.LENGTH_SHORT).show()


                    }
                }
            }


        } else {
            val filename =
                connectUserProgressImage.child("file" + System.currentTimeMillis())
            if (fireUri != null) {
                filename.putFile(fireUri!!).addOnProgressListener { snapshot ->
                    dialog.show()
                }.addOnSuccessListener { task ->
                    filename.downloadUrl.addOnSuccessListener {

                        dialog.dismiss()
                        val id = practiceQueryCollectionRef.document().id
                        val data = practiceQueryCollectionRef.document(id)
                            .set(
                                QueryDataRV(
                                    timeStamp,
                                    input,
                                    millis,
                                    null,
                                    id,
                                    it.toString(),
                                    args.video.id,
                                    args.video.type
                                )
                            )
                        Toast.makeText(requireContext(), "Successful practice", Toast.LENGTH_SHORT)
                            .show()
                    }


                }
            }


        }

//        queryList.add(QueryDataRV(timeStamp, input, millis, bmp,))
        Log.d(TAG, "sendInput: ${byte.size}")
        Log.d(TAG, "sendInputString: ${byte.toString()}")


    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.video_progress_details_layout_analyitcs, container, false)
        setHasOptionsMenu(true)

        return v
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ")
        Log.d(TAG, "onViewCreated Type: ${args.video.type}")
        Log.d(TAG, "onViewCreated Type: ${args.video.id}")
        builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false)
        builder.setView(R.layout.layout_loading_dialog)
        dialog = builder.create()
        mediaController = MediaController(requireContext())
        val fm = parentFragmentManager
        val dialog = DialogFragmentCoachList()

        rvQueryListAnalytics.setOnClickListener {
            Log.d(TAG, "onViewCreated: happening something")
        }

        rvQueryListAnalytics.layoutManager = LinearLayoutManager(requireContext())


//        var queryAdapter = QueryAdapter(queryList)
//        rvQueryList.adapter = queryAdapter
//        queryAdapter.notifyItemInserted(queryList.size)
        Log.d(TAG, "onViewCreated: ${queryList.size}")

        showData()


        swipeAnalyitcs.setOnRefreshListener {
            showData()

            swipeAnalyitcs.isRefreshing = false
        }



        val videoUrl = args.video.VideoUrlProgress?.toUri()
        (activity as AppCompatActivity).supportActionBar?.title = " "
        progressVideoDetailsAnalyitcs.visibility = View.VISIBLE
        progressVideoDetailsAnalyitcs.setVideoURI(videoUrl)
        progressVideoDetailsAnalyitcs.setMediaController(mediaController)
        mediaController.setAnchorView(progressVideoDetailsAnalyitcs)
        mediaController.setMediaPlayer(progressVideoDetailsAnalyitcs)
        this.progressVideoDetailsAnalyitcs.setMediaController(mediaController)
        progressVideoDetailsAnalyitcs.requestFocus()
        progressVideoDetailsAnalyitcs.start()


        addQueryFabAnalytics.setOnClickListener {
            if (queryList.size == 5) {

                Toast.makeText(
                    requireContext(),
                    "You cannot add More than five query",
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                progressVideoDetailsAnalyitcs.pause()
                dialog.setTargetFragment(coachPlusNavHostFragment, 123)
                dialog.show(fm, "new_dialog")
                val millis = progressVideoDetailsAnalyitcs.currentPosition.toLong()
                val millisseccond = progressVideoDetailsAnalyitcs.currentPosition.toLong() * 1000
                val minutes = progressVideoDetailsAnalyitcs.currentPosition.toLong() / 1000 / 60
                val seconds = progressVideoDetailsAnalyitcs.currentPosition.toLong() / 1000 % 60
                val newBitmap = getVideoFrame(requireContext(), videoUrl!!, millisseccond)
                val bitmapUri = newBitmap?.let {
                    getImageUri(requireContext(), it)
                }
                fireUri = bitmapUri
                val outputStream = ByteArrayOutputStream()
                newBitmap!!.compress(Bitmap.CompressFormat.PNG, 100, outputStream)

                val newByteArray = outputStream.toByteArray()


                if (seconds < 9) {
                    val timestamp = "0${minutes}:0${seconds}"
                    val bundle = Bundle()
                    bundle.putString("key", timestamp)
                    bundle.putLong("mili", millis)
                    bundle.putByteArray("byte", newByteArray)

                    dialog.arguments = bundle
                } else {
                    val timestamp = "0${minutes}:${seconds}"
                    val bundle = Bundle()
                    bundle.putString("key", timestamp)
                    bundle.putLong("mili", millis)
                    bundle.putByteArray("byte", newByteArray)
                    dialog.arguments = bundle

                }


            }


        }
    }


    fun getVideoFrame(context: Context?, uri: Uri, time: Long): Bitmap? {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(uri.toString(), HashMap())

            return retriever.getFrameAtTime(time, MediaMetadataRetriever.OPTION_CLOSEST)

            Log.d(TAG, "getVideoFrame: ${retriever.frameAtTime.toString()}")
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
            Log.d(TAG, "getVideoFrame: ${ex.message}")
        } catch (ex: RuntimeException) {
            ex.printStackTrace()
            Log.d(TAG, "getVideoFrame: run ${ex.message}")
        } finally {
            try {
                retriever.release()
            } catch (ex: RuntimeException) {
            }
        }
        return null
    }


    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            System.currentTimeMillis().toString(),
            null
        )
        return Uri.parse(path)
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun showData() {
        dialog.show()
        val image = args.video
        queryList.clear()

        val queryRef = db.collection("UserDetails").document(user?.uid!!)
            .collection("GameProgressVideoDetails").document(image.id!!)
            .collection("QueryDataList").get().addOnSuccessListener {
                for (document in it.documents) {

                    val queryDetails = QueryDataRV(
                        document.getString("timestamp"),
                        document.getString("query"),
                        document.getLong("milliTimeStamp"),
                        null,
                        document.getString("id"),
                        document.getString("imgUrl"),
                        document.getString("parentId"),
                        document.getString("type")

                    )
                    queryList.add(queryDetails)

                }
                var queryAdapter = QueryAdapter(queryList)
                rvQueryListAnalytics.adapter = queryAdapter
                dialog.dismiss()

                queryAdapter.setOnItemClickListener {
//                    val bundle = Bundle().apply {
//                        putSerializable("draw", it)
//                    }
//                    findNavController().navigate(
//                        R.id.action_videoProgressDetails_to_drawOnImage,
//                        bundle
//                    )
                    progressVideoDetailsAnalyitcs.seekTo(it.milliTimeStamp!!.toInt())
                    progressVideoDetailsAnalyitcs.pause()


                }
                queryAdapter.setOnLongItemClickListener {
                    val bundle = Bundle().apply {
                        putSerializable("draw", it)
                    }
                    findNavController().navigate(
                        R.id.action_videoProgressDetailsAnalytics_to_drawOnImage,
                        bundle
                    )

                }
                queryAdapter.setOnEditTheItemClickListener {
                    val imageDialog = DialogImageView()
                    val fm1 = parentFragmentManager
                    imageDialog.setTargetFragment(coachPlusNavHostFragment, 1)
                    imageDialog.show(fm1, "Image Dialog")
                    val bundle = Bundle()
                    bundle.putString("image", it.imgUrl)
                    bundle.putString("edit", it.query)
                    bundle.putString("parent", it.parentId)
                    bundle.putString("id", it.id)
                    imageDialog.arguments = bundle


                }

                queryAdapter.setOnViewImageTheItemClickListener {
                    val bundle = Bundle().apply {
                        putSerializable("expand", it)

                    }

                    findNavController().navigate(
                        R.id.action_videoProgressDetailsAnalytics_to_expandIamgeView,
                        bundle
                    )

                }

//                queryAdapter.setOnLongItemClickListener {
//
//                    val popupMenu = PopupMenu(requireContext(), rvQueryList)
//
//                    popupMenu.menuInflater.inflate(R.menu.query_menu, popupMenu.menu)
//                    popupMenu.setForceShowIcon(true)
//
//                    popupMenu.setOnMenuItemClickListener { item ->
////                         Toast.makeText(requireContext(),it.title,Toast.LENGTH_SHORT).show()
//                        when (item.itemId) {
//                            R.id.edit -> {
//                                Toast.makeText(requireContext(), item.title, Toast.LENGTH_SHORT)
//                                    .show()
//                            }
//                            R.id.delete -> {
//                                Toast.makeText(requireContext(), item.title, Toast.LENGTH_SHORT)
//                                    .show()
//                                val queryRef =
//                                    db.collection("UserDetails").document(user?.email.toString())
//                                        .collection("GameProgressVideoDetails").document(image.id!!)
//                                        .collection("QueryDataList").document(it.id!!).delete()
//                                        .addOnSuccessListener {
//                                            Toast.makeText(requireContext(), "Successfully deleted please Refresh", Toast.LENGTH_SHORT).show()
//                                        }
//
//                            }
//                            R.id.analyze -> {
//                                Toast.makeText(requireContext(), item.title, Toast.LENGTH_SHORT)
//                                    .show()
//                            }
//
//                        }
//
//                        return@setOnMenuItemClickListener true
//                    }
//                    popupMenu.show()
//
//                }


            }


//        val countQueryRef = db.collection("UserDetails").document(user?.email.toString())
//            .collection("GameProgressVideoDetails").document(image.id!!)
//            .collection("QueryDataList")
//
//        Log.d(TAG, "showData:${countQueryRef} ")


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }
}


