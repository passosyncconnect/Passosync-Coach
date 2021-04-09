package com.pasosync.pasosyncconnect.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.pasosync.pasosynccoach.R

import kotlinx.android.synthetic.main.game_expand_image.*

class ExpandIamgeView:Fragment(R.layout.game_expand_image) {
    val args:ExpandIamgeViewArgs by navArgs()




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.game_expand_image, container, false)
        setHasOptionsMenu(true)

        return v
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        Glide.with(requireContext()).load(args.expand.imgUrl?.toUri()).into(expandImage)


    }
}