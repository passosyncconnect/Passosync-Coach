package com.pasosync.pasosynccoach.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.onNavDestinationSelected
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.databinding.FragmnetPdfViewerBinding


class PdfFragment : Fragment(R.layout.fragmnet_pdf_viewer) {
    private lateinit var binding: FragmnetPdfViewerBinding

    private val TAG = "PdfFragment"
   // val args: LectureContentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragmnet_pdf_viewer, container, false)
        setHasOptionsMenu(true)
        return v
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = " "

        binding= FragmnetPdfViewerBinding.bind(view)

//        val lectures = args.lecture
//        val pdfUrl = args.lecture.lecturePdfUrl?.toUri()
//        Log.d(TAG, "onViewCreated: ${pdfUrl.toString()}")
//        pdfView.fromUri(pdfUrl).load()
        binding.pdfView.fromAsset("resume.pdf").load()

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }
}