package com.hym.zhankucompose.util;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.os.Build.VERSION.SDK_INT;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Method;
import java.util.List;

public class ProcessUtils {
    private static final String TAG = "ProcessUtils";

    private ProcessUtils() {
        // Does nothing
    }

    /**
     * @return true when running in the default app process.
     */
    public static boolean isDefaultProcess(@NonNull Context context) {
        String processName = getProcessName(context);
        ApplicationInfo info = context.getApplicationInfo();
        return TextUtils.equals(processName, info.processName);
    }

    /**
     * @return The name of the active process.
     */
    @Nullable
    @SuppressLint({"PrivateApi", "DiscouragedPrivateApi"})
    public static String getProcessName(@NonNull Context context) {
        if (SDK_INT >= 28) {
            return Application.getProcessName();
        }

        // Try using ActivityThread to determine the current process name.
        try {
            Class<?> activityThread = Class.forName(
                    "android.app.ActivityThread",
                    false,
                    ProcessUtils.class.getClassLoader());
            final Object packageName;
            Method currentProcessName = activityThread.getDeclaredMethod("currentProcessName");
            currentProcessName.setAccessible(true);
            packageName = currentProcessName.invoke(null);
            if (packageName instanceof String) {
                return (String) packageName;
            }
        } catch (Throwable exception) {
            Log.d(TAG, "Unable to check ActivityThread for processName", exception);
        }

        // Fallback to the most expensive way
        int pid = Process.myPid();
        ActivityManager am =
                (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);

        if (am != null) {
            List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
            if (processes != null && !processes.isEmpty()) {
                for (ActivityManager.RunningAppProcessInfo process : processes) {
                    if (process.pid == pid) {
                        return process.processName;
                    }
                }
            }
        }

        return null;
    }
}
