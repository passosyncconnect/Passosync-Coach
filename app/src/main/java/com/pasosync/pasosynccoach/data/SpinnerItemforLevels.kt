package com.pasosync.pasosynccoach.data

import com.pasosync.pasosynccoach.R

data class SpinnerItemforLevels(
    val TypeName: String,
    val flagImage: Int
)

object TypesLevels {

    private val images = intArrayOf(
        R.drawable.securitysmall,
        R.drawable.securitysmall,
        R.drawable.securitysmall,



    )

    private val category = arrayOf(
        "Level-1",
        "Level-2",
        "Level-3"

    )

    var list: ArrayList<SpinnerItemforLevels>? = null
        get() {

            if (field != null)
                return field

            field = ArrayList()
            for (i in images.indices) {

                val imageId = images[i]
                val countryName = category[i]

                val spinnerItem = SpinnerItemforLevels(countryName, imageId)
                field!!.add(spinnerItem)
            }

            return field
        }

}
