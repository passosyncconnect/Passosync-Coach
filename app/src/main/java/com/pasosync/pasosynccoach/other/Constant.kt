package com.pasosync.pasosynccoach.other

import java.util.regex.Pattern

object Constant {
    const val FCM_BASE_URL = "https://fcm.googleapis.com"
    const val SERVER_KEY = "AAAAfUu2ST8:APA91bEUTwdZDKTkkF2Kh3nCK05htmJN-lhj_EiJai8ThlTKhQoTZCgbfWCkySA_ZUesN54pqQJSfV_Ri6boiDXyQeH_wHO-x-L_0z3tPB4Ix5sfhyphDNRPxCC8_bTAM84lEA08qP0w"
    const val CONTENT_TYPE = "application/json"
    const val LECTURE_DATABASE_NAME="lecture_db"
    const val BASE_URL="https://newsapi.org/"
    const val REQUEST_CODE_SIGN_IN=0
    const val REQUEST_CODE_IMAGE_PICK=1
    const val PICK_VIDEO=2
    const val PICK_PDF=3
    const val REQUEST_CODE_WRITING=4
    val PASSWORD_PATTERN: Pattern = Pattern.compile(
        "^" +  //"(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +  //any letter
                "(?=.*[@#$%^&+=])" +  //at least 1 special character
                "(?=\\S+$)" +  //no white spaces
                ".{6,}" +  //at least 4 characters
                "$"
    )
}