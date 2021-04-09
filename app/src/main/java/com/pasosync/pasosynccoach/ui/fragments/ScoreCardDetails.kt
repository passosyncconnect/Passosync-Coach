package com.pasosync.pasosynccoach.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.onNavDestinationSelected
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.pasosync.pasosynccoach.R
import kotlinx.android.synthetic.main.score_card_details_fragment.*


private const val TAG = "ScoreCardDetails"
class ScoreCardDetails:Fragment(R.layout.score_card_details_fragment) {
    lateinit var mediaController: MediaController
    val args:ScoreCardDetailsArgs by navArgs()


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.score_card_details_fragment, container, false)
        setHasOptionsMenu(true)
        // binding = FragmentUserVideoAndScoreChooserBinding.bind(v)
        return v
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ${args.game.ballPlayed}")
        tvMyTotalScore.text = "${args.game.myTeamScore}(${args.game.totalOver} Overs)"
        tvOpponentTotalScore.text = "${args.game.rivalScore}(${args.game.totalOver} Overs)"
        myRunsData.text = "${args.game.myRuns}(${args.game.ballPlayed} Balls)"
        myWicketTakenData.text = "${args.game.myWicketTaken} wickets"
        itemMyTeamName.text=args.game.myTeamName
        iteOpponentName.text=args.game.rivalTeamName
        myFoursAndSixesData.text = "${args.game.myFours} Fours/${args.game.mySixes} Sixes"
        matchDescription.text= args.game.aboutMatchDescription
        Glide.with(requireContext()).load(args.game.gameImageUrl)
                .placeholder(R.drawable.progressbg).into(
                        ivImage
                )


        mediaController = MediaController(requireContext())
        val videoUrl=args.game.gameVideoUrl?.toUri()

        if (videoUrl == null) {
            Snackbar.make(requireView(), "there is no video", Snackbar.LENGTH_LONG).show()
        } else {
            videoScoreCardDetails.visibility=View.VISIBLE
            videoScoreCardDetails.setVideoURI(videoUrl)
            videoScoreCardDetails.setMediaController(mediaController)
            mediaController.setAnchorView(videoScoreCardDetails)
            mediaController.setMediaPlayer(videoScoreCardDetails)
            this.videoScoreCardDetails.setMediaController(mediaController)
            videoScoreCardDetails.requestFocus()
            videoScoreCardDetails.start()

        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

}