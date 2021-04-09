package com.pasosync.pasosyncconnect.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Path
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.onNavDestinationSelected
import com.bumptech.glide.Glide
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pasosync.pasosynccoach.R

import ja.burhanrashid52.photoeditor.OnSaveBitmap
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditor.OnSaveListener
import ja.burhanrashid52.photoeditor.SaveSettings
import kotlinx.android.synthetic.main.draw_on_layout.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException


private const val TAG = "DrawOnImage"

const val READ_WRITE_STORAGE = 52

class DrawOnImage : Fragment(R.layout.draw_on_layout) {
    val args: DrawOnImageArgs by navArgs()

    //    var path: Path = Path()
//      var paint_brush: Paint = Paint()
    var colorhii: Int = 2
    var progressImageUri: Uri? = null
    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog
    var mPhotoEditor: PhotoEditor? = null
    private val db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser

    val connectUserProgressImage = Firebase.storage.reference.child("UserProgressImage")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.draw_on_layout, container, false)
        setHasOptionsMenu(true)


        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle: Bundle? = arguments
        var bytearray = bundle?.getByteArray("draw")
        Log.d(TAG, "onViewCreated: ${args.draw.timestamp}")

        Log.d(TAG, "onViewCreatedparent: ${args.draw.parentId}")
        Log.d(TAG, "onViewCreatedparent: ${args.draw.id}")

//        ivDrawImage.setImageBitmap(args.draw.img)
        red.setCardBackgroundColor(Color.BLACK)
        builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false) // if you want user to wait for some process to finish
        builder.setView(R.layout.layout_loading_dialog)
        dialog = builder.create()

      //  dragRect.source.setImageBitmap(args.draw.img)
     //   dragRect.source.setImageURI(args.draw.imgUrl?.toUri())
        Glide.with(requireContext()).load(args.draw.imgUrl).into(dragRect.source)
        mPhotoEditor = PhotoEditor.Builder(requireContext(), dragRect)
            .build()

//        if (null != dragRect) {
//            dragRect.setOnUpCallback(object : DragRectView.OnUpCallback {
//                override fun onRectFinished(rect: Rect?) {
////                    Toast.makeText(
////                        requireContext(),
////                        "Rect is (" + rect!!.left + ", " + rect.top + ", " + rect.right + ", " + rect.bottom + ")",
////                        Toast.LENGTH_LONG
////                    ).show();
//
//                    Snackbar.make(requireView(),"Marked",Snackbar.LENGTH_SHORT).show()
//
//                }
//
//
//            })
//        }

//        val bmp = BitmapFactory.decodeByteArray(bytearray, 0, bytearray!!.size)
//        ivDrawImage.setImageBitmap(bmp)

        undo.setOnClickListener {
            mPhotoEditor?.let {
                it.undo()
            }
        }

        redo.setOnClickListener {
            mPhotoEditor?.let {
                it.redo()
            }
        }
        red.setOnClickListener {
            ColorPickerDialog
                .Builder(requireContext())                        // Pass Activity Instance
                .setTitle("Pick color")            // Default "Choose Color"
                .setColorShape(ColorShape.SQAURE)   // Default ColorShape.CIRCLE
                .setDefaultColor("#c1c1c1")     // Pass Default Color
                .setColorListener { color, colorHex ->
                    // Handle Color Selection
                    colorhii = color
                    mPhotoEditor!!.brushColor = colorhii
                    mPhotoEditor!!.brushSize = 5f


                    red.setCardBackgroundColor(color)


                }
                .setPositiveButton("Selected")
                .setNegativeButton("Cancel")
                .show()
            pen.isSelected = true


        }
        yellow.setOnClickListener {
//            paint_brush.color = Color.YELLOW
//            currentColor(paint_brush.color)
//            saveImage()
            val saveSettings = SaveSettings.Builder()
//                .setClearViewsEnabled(true)
//                .setTransparencyEnabled(true)
                .build()
            mPhotoEditor!!.saveAsBitmap(saveSettings, object : OnSaveBitmap {
                override fun onBitmapReady(saveBitmap: Bitmap?) {
//                    getImageUri(requireContext(), saveBitmap!!)
                    try {
                        saveBitmap?.let {
                            val filename =
                                connectUserProgressImage.child("file" + System.currentTimeMillis())
                            getImageUri(requireContext(), it)?.let { it1 ->
                                filename.putFile(it1).addOnProgressListener {
                                    dialog.show()
                                }.addOnSuccessListener {
                                    dialog.dismiss()
                                    filename.downloadUrl.addOnSuccessListener {
                                        Log.d(TAG, "onBitmapReady: ${it.toString()}")
                                        if (args.draw.type == "Game") {
                                            val userGameVideoProgressCollectionRef =
                                                db.collection("UserDetails")
                                                    .document(user?.uid!!).collection(
                                                        "GameProgressVideoDetails"
                                                    ).document(args.draw.parentId!!)
                                                    .collection("QueryDataList")
                                                    .document(args.draw.id!!)
                                            userGameVideoProgressCollectionRef.update(
                                                "imgUrl",
                                                it.toString()
                                            )


                                        } else {
                                            val userPracticeVideoProgressCollectionRef =
                                                db.collection("UserDetails")
                                                    .document(user?.uid!!).collection(
                                                        "PracticeProgressVideoDetails"
                                                    ).document(args.draw.parentId!!)
                                                    .collection("QueryDataList")
                                                    .document(args.draw.id!!)
                                            userPracticeVideoProgressCollectionRef.update(
                                                "imageUrl",
                                                it.toString()
                                            )
                                            

                                        }


                                    }
                                }


                            }


                        }


                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()

                    }


                }

                override fun onFailure(e: java.lang.Exception?) {

                }

            })

        }

        pen.setOnClickListener {
            mPhotoEditor!!.brushColor = colorhii
            mPhotoEditor!!.brushSize = 5f


        }



        eraser.setOnClickListener {
            mPhotoEditor!!.brushEraser()

        }
    }




    @SuppressLint("MissingPermission")
    private fun saveImage() {

        if (requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

        }
        dialog.show()
        val file = File(
            Environment.getExternalStorageDirectory()
                .toString() + File.separator + ""
                    + System.currentTimeMillis() + ".png"
        )
        try {
            file.createNewFile()
            val saveSettings = SaveSettings.Builder()
                .setClearViewsEnabled(true)
                .setTransparencyEnabled(true)
                .build()


            mPhotoEditor?.saveAsFile(file.absolutePath, saveSettings, object : OnSaveListener {
                override fun onSuccess(imagePath: String) {
                    dialog.dismiss()
//                        hideLoading()
////                        showSnackbar("Image Saved Successfully")
//                        mSaveImageUri = Uri.fromFile(File(imagePath))
//                        mPhotoEditorView.getSource().setImageURI(mSaveImageUri)
                    Toast.makeText(
                        requireContext(),
                        "Image Saved Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onFailure(exception: Exception) {
//
                    dialog.dismiss()
//                      dialog.dismis
//                      s()
//                        hideLoading()
//                        showSnackbar("Failed to save Image")
                    Toast.makeText(requireContext(), "Failed to save Image", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } catch (e: IOException) {
            e.printStackTrace()
            dialog.dismiss()
//                hideLoading()
//                showSnackbar(e.message)
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            Log.d(TAG, "saveImage: ${e.message}")

        }
    }


    private fun requestPermission(permission: String): Boolean {
        val isGranted =
            ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED
        if (!isGranted) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(permission),
                READ_WRITE_STORAGE
            )
        }
        return isGranted
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    private fun isPermissionGranted(isGranted: Boolean, permission: String?) {
        if (isGranted) {
            saveImage()
        }
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


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            READ_WRITE_STORAGE -> isPermissionGranted(
                grantResults[0] == PackageManager.PERMISSION_GRANTED, permissions[0]
            )
        }
    }


}