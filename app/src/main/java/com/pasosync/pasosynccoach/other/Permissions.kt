package com.pasosync.pasosynccoach.other

import android.Manifest
import android.content.Context
import pub.devrel.easypermissions.EasyPermissions

object Permissions {

    fun hasWritingPermissions(context: Context)=
        EasyPermissions.hasPermissions(context
        ,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA
        )
}