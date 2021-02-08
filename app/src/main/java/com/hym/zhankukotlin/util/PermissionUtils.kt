package com.hym.zhankukotlin.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.text.TextUtils
import androidx.annotation.RequiresApi
import java.util.*

@RequiresApi(api = Build.VERSION_CODES.M)
object PermissionUtils {
    val RUNTIME_PERMISSIONS = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    fun checkSelfPermission(context: Context, permission: String): Boolean {
        if (context == null || TextUtils.isEmpty(permission)) {
            return false
        }
        val result = context.checkSelfPermission(permission!!)
        return result == PackageManager.PERMISSION_GRANTED
    }

    fun checkSelfPermissions(context: Context, permissions: List<String>): List<String> {
        if (context == null || permissions == null || permissions.isEmpty()) {
            return emptyList<String>()
        }
        val notGrantedPermissions: MutableList<String> = LinkedList()
        for (permission in permissions) {
            if (!checkSelfPermission(context, permission)) {
                notGrantedPermissions.add(permission)
            }
        }
        return notGrantedPermissions
    }

    fun checkSelfPermissions(context: Context, vararg permissions: String): List<String> {
        return if (context == null || permissions == null || permissions.isEmpty()) {
            emptyList<String>()
        } else checkSelfPermissions(context, listOf(*permissions))
    }

    fun requestPermissions(activity: Activity, vararg permissions: String) {
        if (activity == null || permissions == null || permissions.isEmpty()) {
            return
        }
        activity.requestPermissions(permissions, 0)
    }

    fun requestPermissions(activity: Activity, permissions: List<String>) {
        if (activity == null || permissions == null || permissions.isEmpty()) {
            return
        }
        requestPermissions(activity, *permissions.toTypedArray())
    }

    fun checkAndRequestPermissions(activity: Activity, permissions: List<String>) {
        if (activity == null || permissions == null || permissions.isEmpty()) {
            return
        }
        val notGrantedPermissions = checkSelfPermissions(activity, permissions)
        if (notGrantedPermissions.isNotEmpty()) {
            requestPermissions(activity, notGrantedPermissions)
        }
    }

    fun checkAndRequestPermissions(activity: Activity, vararg permissions: String) {
        if (activity == null || permissions == null || permissions.isEmpty()) {
            return
        }
        checkAndRequestPermissions(activity, listOf(*permissions))
    }
}