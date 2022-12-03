package com.rndeep.fns_fantoo.ui.login

import android.Manifest
import android.content.Context
import pub.devrel.easypermissions.EasyPermissions

class PermissionCheck {

    companion object{
        val PERMISSIONS_ARRAY = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO)

        fun checkPermission(context:Context):Boolean{
            return EasyPermissions.hasPermissions(context, *PERMISSIONS_ARRAY)
        }
    }
}