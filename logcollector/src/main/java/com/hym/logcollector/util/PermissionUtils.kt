package com.hym.logcollector.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * @author hehua2008
 * @date 2021/6/29
 */
@RequiresApi(api = Build.VERSION_CODES.M)
object PermissionUtils {
    fun checkSelfPermission(context: Context, permission: String): Boolean {
        require(permission.isNotBlank())
        val result = context.checkSelfPermission(permission)
        return result == PackageManager.PERMISSION_GRANTED
    }

    fun checkSelfPermissions(context: Context, vararg permissions: String): Array<String> {
        if (permissions.isEmpty()) return emptyArray()
        val notGrantedPermissions: MutableList<String> = mutableListOf()
        permissions.filterTo(notGrantedPermissions) {
            !checkSelfPermission(context, it)
        }
        return notGrantedPermissions.toTypedArray()
    }

    fun checkSelfPermissions(context: Context, permissions: List<String>): Array<String> {
        return checkSelfPermissions(context, *permissions.toTypedArray())
    }

    fun getPermissionName(context: Context, permission: String): String? {
        return try {
            val pm = context.packageManager
            pm.getPermissionInfo(permission, 0).loadLabel(pm).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            // do nothing
            null
        }
    }
}