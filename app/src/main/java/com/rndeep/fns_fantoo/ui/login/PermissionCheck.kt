package com.rndeep.fns_fantoo.ui.login

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import pub.devrel.easypermissions.EasyPermissions

class PermissionCheck {

    companion object {
        private val PERMISSIONS_ARRAY_UNDER_32 = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
        )

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        private val PERMISSIONS_ARRAY_OVER_33 = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
        )

        val PERMISSIONS_ARRAY = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PERMISSIONS_ARRAY_OVER_33
        } else {
            PERMISSIONS_ARRAY_UNDER_32
        }

        fun checkPermission(context: Context): Boolean {
            return EasyPermissions.hasPermissions(context, *PERMISSIONS_ARRAY)
        }
    }
}