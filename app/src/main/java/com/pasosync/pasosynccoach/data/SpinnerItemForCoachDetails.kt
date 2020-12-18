package com.pasosync.pasosynccoach.data

import com.pasosync.pasosynccoach.R

data class SpinnerItemForCoachDetails(
    val TypeName: String,
    val flagImage: Int
)

object TypesCoach {

    private val images = intArrayOf(
        R.drawable.bat,
        R.drawable.ic_baseline_sports_baseball_24,
        R.drawable.field



    )

    private val category = arrayOf(
        "Batting",
        "Bowling",
        "Fielding"
    )

    var list: ArrayList<SpinnerItemForCoachDetails>? = null
        get() {

            if (field != null)
                return field

            field = ArrayList()
            for (i in images.indices) {

                val imageId = images[i]
                val countryName = category[i]

                val spinnerItem = SpinnerItemForCoachDetails(countryName, imageId)
                field!!.add(spinnerItem)
            }
            return field
        }

}
