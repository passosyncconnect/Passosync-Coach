package com.pasosync.pasosynccoach.data

import com.pasosync.pasosynccoach.R

data class SpinnerItem(
    val TypeName: String,
    val flagImage: Int
)

object Types {

    private val images = intArrayOf(
        R.drawable.bat,
        R.drawable.ic_baseline_sports_baseball_24,
        R.drawable.field,
        R.drawable.captain,
        R.drawable.net,
        R.drawable.match,
        R.drawable.strength,
        R.drawable.mental


    )

    private val category = arrayOf(
        "Batting",
        "Bowling",
        "Fielding",
        "Captaincy",
        "Net Preparation",
        "Match Preparation",
        "Strength and Conditioning",
        "Mental Skills"
    )

    var list: ArrayList<SpinnerItem>? = null
        get() {

            if (field != null)
                return field

            field = ArrayList()
            for (i in images.indices) {

                val imageId = images[i]
                val countryName = category[i]

                val spinnerItem = SpinnerItem(countryName, imageId)
                field!!.add(spinnerItem)
            }

            return field
        }

}
