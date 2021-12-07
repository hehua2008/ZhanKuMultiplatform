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
    @JvmField
    val RUNTIME_PERMISSIONS = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    @JvmStatic
    fun checkSelfPermission(context: Context, permission: String): Boolean {
        if (TextUtils.isEmpty(permission)) {
            return false
        }
        val result = context.checkSelfPermission(permission)
        return result == PackageManager.PERMISSION_GRANTED
    }

    @JvmStatic
    fun checkSelfPermissions(context: Context, permissions: List<String>): List<String> {
        if (permissions.isEmpty()) {
            return emptyList()
        }
        val notGrantedPermissions: MutableList<String> = LinkedList()
        for (permission in permissions) {
            if (!checkSelfPermission(context, permission)) {
                notGrantedPermissions.add(permission)
            }
        }
        return notGrantedPermissions
    }

    @JvmStatic
    fun checkSelfPermissions(context: Context, vararg permissions: String): List<String> {
        return if (permissions.isEmpty()) {
            emptyList()
        } else checkSelfPermissions(context, listOf(*permissions))
    }

    @JvmStatic
    fun requestPermissions(activity: Activity, vararg permissions: String) {
        if (permissions.isEmpty()) {
            return
        }
        activity.requestPermissions(permissions, 0)
    }

    @JvmStatic
    fun requestPermissions(activity: Activity, permissions: List<String>) {
        if (permissions.isEmpty()) {
            return
        }
        requestPermissions(activity, *permissions.toTypedArray())
    }

    @JvmStatic
    fun checkAndRequestPermissions(activity: Activity, permissions: List<String>) {
        if (permissions.isEmpty()) {
            return
        }
        val notGrantedPermissions = checkSelfPermissions(activity, permissions)
        if (notGrantedPermissions.isNotEmpty()) {
            requestPermissions(activity, notGrantedPermissions)
        }
    }

    @JvmStatic
    fun checkAndRequestPermissions(activity: Activity, vararg permissions: String) {
        if (permissions.isEmpty()) {
            return
        }
        checkAndRequestPermissions(activity, listOf(*permissions))
    }
}