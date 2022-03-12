package android.content;

import android.content.IntentSender.SendIntentException;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.util.AttributeSet;
import android.view.Display;
import android.view.ViewDebug.ExportedProperty;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * @formatter:off
 */
public abstract class Context {
    public static final String ACCESSIBILITY_SERVICE = "accessibility";
    public static final String ACCOUNT_SERVICE = "account";
    public static final String ACTIVITY_SERVICE = "activity";
    public static final String ALARM_SERVICE = "alarm";
    public static final String APPWIDGET_SERVICE = "appwidget";
    public static final String APP_OPS_SERVICE = "appops";
    public static final String APP_SEARCH_SERVICE = "app_search";
    public static final String AUDIO_SERVICE = "audio";
    public static final String BATTERY_SERVICE = "batterymanager";
    public static final int BIND_ABOVE_CLIENT = 8;
    public static final int BIND_ADJUST_WITH_ACTIVITY = 128;
    public static final int BIND_ALLOW_OOM_MANAGEMENT = 16;
    public static final int BIND_AUTO_CREATE = 1;
    public static final int BIND_DEBUG_UNBIND = 2;
    public static final int BIND_EXTERNAL_SERVICE = -2147483648;
    public static final int BIND_IMPORTANT = 64;
    public static final int BIND_INCLUDE_CAPABILITIES = 4096;
    public static final int BIND_NOT_FOREGROUND = 4;
    public static final int BIND_NOT_PERCEPTIBLE = 256;
    public static final int BIND_WAIVE_PRIORITY = 32;
    public static final String BIOMETRIC_SERVICE = "biometric";
    public static final String BLOB_STORE_SERVICE = "blob_store";
    public static final String BLUETOOTH_SERVICE = "bluetooth";
    public static final String BUGREPORT_SERVICE = "bugreport";
    public static final String CAMERA_SERVICE = "camera";
    public static final String CAPTIONING_SERVICE = "captioning";
    public static final String CARRIER_CONFIG_SERVICE = "carrier_config";
    public static final String CLIPBOARD_SERVICE = "clipboard";
    public static final String COMPANION_DEVICE_SERVICE = "companiondevice";
    public static final String CONNECTIVITY_DIAGNOSTICS_SERVICE = "connectivity_diagnostics";
    public static final String CONNECTIVITY_SERVICE = "connectivity";
    public static final String CONSUMER_IR_SERVICE = "consumer_ir";
    public static final int CONTEXT_IGNORE_SECURITY = 2;
    public static final int CONTEXT_INCLUDE_CODE = 1;
    public static final int CONTEXT_RESTRICTED = 4;
    public static final String CROSS_PROFILE_APPS_SERVICE = "crossprofileapps";
    public static final String DEVICE_POLICY_SERVICE = "device_policy";
    public static final String DISPLAY_HASH_SERVICE = "display_hash";
    public static final String DISPLAY_SERVICE = "display";
    public static final String DOMAIN_VERIFICATION_SERVICE = "domain_verification";
    public static final String DOWNLOAD_SERVICE = "download";
    public static final String DROPBOX_SERVICE = "dropbox";
    public static final String EUICC_SERVICE = "euicc";
    public static final String FILE_INTEGRITY_SERVICE = "file_integrity";
    public static final String FINGERPRINT_SERVICE = "fingerprint";
    public static final String GAME_SERVICE = "game";
    public static final String HARDWARE_PROPERTIES_SERVICE = "hardware_properties";
    public static final String INPUT_METHOD_SERVICE = "input_method";
    public static final String INPUT_SERVICE = "input";
    public static final String IPSEC_SERVICE = "ipsec";
    public static final String JOB_SCHEDULER_SERVICE = "jobscheduler";
    public static final String KEYGUARD_SERVICE = "keyguard";
    public static final String LAUNCHER_APPS_SERVICE = "launcherapps";
    public static final String LAYOUT_INFLATER_SERVICE = "layout_inflater";
    public static final String LOCATION_SERVICE = "location";
    public static final String MEDIA_COMMUNICATION_SERVICE = "media_communication";
    public static final String MEDIA_METRICS_SERVICE = "media_metrics";
    public static final String MEDIA_PROJECTION_SERVICE = "media_projection";
    public static final String MEDIA_ROUTER_SERVICE = "media_router";
    public static final String MEDIA_SESSION_SERVICE = "media_session";
    public static final String MIDI_SERVICE = "midi";
    public static final int MODE_APPEND = 32768;
    public static final int MODE_ENABLE_WRITE_AHEAD_LOGGING = 8;
    @Deprecated
    public static final int MODE_MULTI_PROCESS = 4;
    public static final int MODE_NO_LOCALIZED_COLLATORS = 16;
    public static final int MODE_PRIVATE = 0;
    @Deprecated
    public static final int MODE_WORLD_READABLE = 1;
    @Deprecated
    public static final int MODE_WORLD_WRITEABLE = 2;
    public static final String NETWORK_STATS_SERVICE = "netstats";
    public static final String NFC_SERVICE = "nfc";
    public static final String NOTIFICATION_SERVICE = "notification";
    public static final String NSD_SERVICE = "servicediscovery";
    public static final String PEOPLE_SERVICE = "people";
    public static final String PERFORMANCE_HINT_SERVICE = "performance_hint";
    public static final String POWER_SERVICE = "power";
    public static final String PRINT_SERVICE = "print";
    public static final int RECEIVER_VISIBLE_TO_INSTANT_APPS = 1;
    public static final String RESTRICTIONS_SERVICE = "restrictions";
    public static final String ROLE_SERVICE = "role";
    public static final String SEARCH_SERVICE = "search";
    public static final String SENSOR_SERVICE = "sensor";
    public static final String SHORTCUT_SERVICE = "shortcut";
    public static final String STORAGE_SERVICE = "storage";
    public static final String STORAGE_STATS_SERVICE = "storagestats";
    public static final String SYSTEM_HEALTH_SERVICE = "systemhealth";
    public static final String TELECOM_SERVICE = "telecom";
    public static final String TELEPHONY_IMS_SERVICE = "telephony_ims";
    public static final String TELEPHONY_SERVICE = "phone";
    public static final String TELEPHONY_SUBSCRIPTION_SERVICE = "telephony_subscription_service";
    public static final String TEXT_CLASSIFICATION_SERVICE = "textclassification";
    public static final String TEXT_SERVICES_MANAGER_SERVICE = "textservices";
    public static final String TV_INPUT_SERVICE = "tv_input";
    public static final String UI_MODE_SERVICE = "uimode";
    public static final String USAGE_STATS_SERVICE = "usagestats";
    public static final String USB_SERVICE = "usb";
    public static final String USER_SERVICE = "user";
    public static final String VIBRATOR_MANAGER_SERVICE = "vibrator_manager";
    @Deprecated
    public static final String VIBRATOR_SERVICE = "vibrator";
    public static final String VPN_MANAGEMENT_SERVICE = "vpn_management";
    public static final String WALLPAPER_SERVICE = "wallpaper";
    public static final String WIFI_AWARE_SERVICE = "wifiaware";
    public static final String WIFI_P2P_SERVICE = "wifip2p";
    public static final String WIFI_RTT_RANGING_SERVICE = "wifirtt";
    public static final String WIFI_SERVICE = "wifi";
    public static final String WINDOW_SERVICE = "window";

    public Context() {
        throw new RuntimeException("Stub!");
    }

    public abstract AssetManager getAssets();

    public abstract Resources getResources();

    public abstract PackageManager getPackageManager();

    public abstract ContentResolver getContentResolver();

    public abstract Looper getMainLooper();

    public Executor getMainExecutor() {
        throw new RuntimeException("Stub!");
    }

    public abstract Context getApplicationContext();

    public void registerComponentCallbacks(ComponentCallbacks callback) {
        throw new RuntimeException("Stub!");
    }

    public void unregisterComponentCallbacks(ComponentCallbacks callback) {
        throw new RuntimeException("Stub!");
    }

    @NonNull
    public final CharSequence getText(int resId) {
        throw new RuntimeException("Stub!");
    }

    @NonNull
    public final String getString(int resId) {
        throw new RuntimeException("Stub!");
    }

    @NonNull
    public final String getString(int resId, Object... formatArgs) {
        throw new RuntimeException("Stub!");
    }

    public final int getColor(int id) {
        throw new RuntimeException("Stub!");
    }

    @Nullable
    public final Drawable getDrawable(int id) {
        throw new RuntimeException("Stub!");
    }

    @NonNull
    public final ColorStateList getColorStateList(int id) {
        throw new RuntimeException("Stub!");
    }

    public abstract void setTheme(int var1);

    @ExportedProperty(
        deepExport = true
    )
    public abstract Theme getTheme();

    @NonNull
    public final TypedArray obtainStyledAttributes(@NonNull int[] attrs) {
        throw new RuntimeException("Stub!");
    }

    @NonNull
    public final TypedArray obtainStyledAttributes(int resid, @NonNull int[] attrs) throws NotFoundException {
        throw new RuntimeException("Stub!");
    }

    @NonNull
    public final TypedArray obtainStyledAttributes(@Nullable AttributeSet set, @NonNull int[] attrs) {
        throw new RuntimeException("Stub!");
    }

    @NonNull
    public final TypedArray obtainStyledAttributes(@Nullable AttributeSet set, @NonNull int[] attrs, int defStyleAttr, int defStyleRes) {
        throw new RuntimeException("Stub!");
    }

    public abstract ClassLoader getClassLoader();

    public abstract String getPackageName();

    @NonNull
    public String getOpPackageName() {
        throw new RuntimeException("Stub!");
    }

    @Nullable
    public String getAttributionTag() {
        throw new RuntimeException("Stub!");
    }

    @NonNull
    public AttributionSource getAttributionSource() {
        throw new RuntimeException("Stub!");
    }

    @Nullable
    public ContextParams getParams() {
        throw new RuntimeException("Stub!");
    }

    public abstract ApplicationInfo getApplicationInfo();

    public abstract String getPackageResourcePath();

    public abstract String getPackageCodePath();

    public abstract SharedPreferences getSharedPreferences(String var1, int var2);

    public abstract boolean moveSharedPreferencesFrom(Context var1, String var2);

    public abstract boolean deleteSharedPreferences(String var1);

    public abstract FileInputStream openFileInput(String var1) throws FileNotFoundException;

    public abstract FileOutputStream openFileOutput(String var1, int var2) throws FileNotFoundException;

    public abstract boolean deleteFile(String var1);

    public abstract File getFileStreamPath(String var1);

    public abstract File getDataDir();

    public abstract File getFilesDir();

    public abstract File getNoBackupFilesDir();

    @Nullable
    public abstract File getExternalFilesDir(@Nullable String var1);

    public abstract File[] getExternalFilesDirs(String var1);

    public abstract File getObbDir();

    public abstract File[] getObbDirs();

    public abstract File getCacheDir();

    public abstract File getCodeCacheDir();

    @Nullable
    public abstract File getExternalCacheDir();

    public abstract File[] getExternalCacheDirs();

    @Deprecated
    public abstract File[] getExternalMediaDirs();

    public abstract String[] fileList();

    public abstract File getDir(String var1, int var2);

    public abstract SQLiteDatabase openOrCreateDatabase(String var1, int var2, CursorFactory var3);

    public abstract SQLiteDatabase openOrCreateDatabase(String var1, int var2, CursorFactory var3, @Nullable DatabaseErrorHandler var4);

    public abstract boolean moveDatabaseFrom(Context var1, String var2);

    public abstract boolean deleteDatabase(String var1);

    public abstract File getDatabasePath(String var1);

    public abstract String[] databaseList();

    @Deprecated
    public abstract Drawable getWallpaper();

    @Deprecated
    public abstract Drawable peekWallpaper();

    @Deprecated
    public abstract int getWallpaperDesiredMinimumWidth();

    @Deprecated
    public abstract int getWallpaperDesiredMinimumHeight();

    @Deprecated
    public abstract void setWallpaper(Bitmap var1) throws IOException;

    @Deprecated
    public abstract void setWallpaper(InputStream var1) throws IOException;

    @Deprecated
    public abstract void clearWallpaper() throws IOException;

    public abstract void startActivity(Intent var1);

    public abstract void startActivity(Intent var1, @Nullable Bundle var2);

    public abstract void startActivities(Intent[] var1);

    public abstract void startActivities(Intent[] var1, Bundle var2);

    public abstract void startIntentSender(IntentSender var1, @Nullable Intent var2, int var3, int var4, int var5) throws SendIntentException;

    public abstract void startIntentSender(IntentSender var1, @Nullable Intent var2, int var3, int var4, int var5, @Nullable Bundle var6) throws SendIntentException;

    public abstract void sendBroadcast(Intent var1);

    public abstract void sendBroadcast(Intent var1, @Nullable String var2);

    public void sendBroadcastWithMultiplePermissions(@NonNull Intent intent, @NonNull String[] receiverPermissions) {
        throw new RuntimeException("Stub!");
    }

    public abstract void sendOrderedBroadcast(Intent var1, @Nullable String var2);

    public abstract void sendOrderedBroadcast(@NonNull Intent var1, @Nullable String var2, @Nullable BroadcastReceiver var3, @Nullable Handler var4, int var5, @Nullable String var6, @Nullable Bundle var7);

    public abstract void sendBroadcastAsUser(Intent var1, UserHandle var2);

    public abstract void sendBroadcastAsUser(Intent var1, UserHandle var2, @Nullable String var3);

    public abstract void sendOrderedBroadcastAsUser(Intent var1, UserHandle var2, @Nullable String var3, BroadcastReceiver var4, @Nullable Handler var5, int var6, @Nullable String var7, @Nullable Bundle var8);

    public void sendOrderedBroadcast(@NonNull Intent intent, @Nullable String receiverPermission, @Nullable String receiverAppOp, @Nullable BroadcastReceiver resultReceiver, @Nullable Handler scheduler, int initialCode, @Nullable String initialData, @Nullable Bundle initialExtras) {
        throw new RuntimeException("Stub!");
    }

    @Deprecated
    public abstract void sendStickyBroadcast(Intent var1);

    @Deprecated
    public void sendStickyBroadcast(@NonNull Intent intent, @Nullable Bundle options) {
        throw new RuntimeException("Stub!");
    }

    @Deprecated
    public abstract void sendStickyOrderedBroadcast(Intent var1, BroadcastReceiver var2, @Nullable Handler var3, int var4, @Nullable String var5, @Nullable Bundle var6);

    @Deprecated
    public abstract void removeStickyBroadcast(Intent var1);

    @Deprecated
    public abstract void sendStickyBroadcastAsUser(Intent var1, UserHandle var2);

    @Deprecated
    public abstract void sendStickyOrderedBroadcastAsUser(Intent var1, UserHandle var2, BroadcastReceiver var3, @Nullable Handler var4, int var5, @Nullable String var6, @Nullable Bundle var7);

    @Deprecated
    public abstract void removeStickyBroadcastAsUser(Intent var1, UserHandle var2);

    @Nullable
    public abstract Intent registerReceiver(@Nullable BroadcastReceiver var1, IntentFilter var2);

    @Nullable
    public abstract Intent registerReceiver(@Nullable BroadcastReceiver var1, IntentFilter var2, int var3);

    @Nullable
    public abstract Intent registerReceiver(BroadcastReceiver var1, IntentFilter var2, @Nullable String var3, @Nullable Handler var4);

    @Nullable
    public abstract Intent registerReceiver(BroadcastReceiver var1, IntentFilter var2, @Nullable String var3, @Nullable Handler var4, int var5);

    public abstract void unregisterReceiver(BroadcastReceiver var1);

    @Nullable
    public abstract ComponentName startService(Intent var1);

    @Nullable
    public abstract ComponentName startForegroundService(Intent var1);

    public abstract boolean stopService(Intent var1);

    public abstract boolean bindService(Intent var1, @NonNull ServiceConnection var2, int var3);

    public boolean bindService(@NonNull Intent service, int flags, @NonNull Executor executor, @NonNull ServiceConnection conn) {
        throw new RuntimeException("Stub!");
    }

    public boolean bindIsolatedService(@NonNull Intent service, int flags, @NonNull String instanceName, @NonNull Executor executor, @NonNull ServiceConnection conn) {
        throw new RuntimeException("Stub!");
    }

    public boolean bindServiceAsUser(@NonNull Intent service, @NonNull ServiceConnection conn, int flags, @NonNull UserHandle user) {
        throw new RuntimeException("Stub!");
    }

    public void updateServiceGroup(@NonNull ServiceConnection conn, int group, int importance) {
        throw new RuntimeException("Stub!");
    }

    public abstract void unbindService(@NonNull ServiceConnection var1);

    public abstract boolean startInstrumentation(@NonNull ComponentName var1, @Nullable String var2, @Nullable Bundle var3);

    public abstract Object getSystemService(@NonNull String var1);

    public final <T> T getSystemService(@NonNull Class<T> serviceClass) {
        throw new RuntimeException("Stub!");
    }

    @Nullable
    public abstract String getSystemServiceName(@NonNull Class<?> var1);

    public abstract int checkPermission(@NonNull String var1, int var2, int var3);

    public abstract int checkCallingPermission(@NonNull String var1);

    public abstract int checkCallingOrSelfPermission(@NonNull String var1);

    public abstract int checkSelfPermission(@NonNull String var1);

    public abstract void enforcePermission(@NonNull String var1, int var2, int var3, @Nullable String var4);

    public abstract void enforceCallingPermission(@NonNull String var1, @Nullable String var2);

    public abstract void enforceCallingOrSelfPermission(@NonNull String var1, @Nullable String var2);

    public abstract void grantUriPermission(String var1, Uri var2, int var3);

    public abstract void revokeUriPermission(Uri var1, int var2);

    public abstract void revokeUriPermission(String var1, Uri var2, int var3);

    public abstract int checkUriPermission(Uri var1, int var2, int var3, int var4);

    @NonNull
    public int[] checkUriPermissions(@NonNull List<Uri> uris, int pid, int uid, int modeFlags) {
        throw new RuntimeException("Stub!");
    }

    public abstract int checkCallingUriPermission(Uri var1, int var2);

    @NonNull
    public int[] checkCallingUriPermissions(@NonNull List<Uri> uris, int modeFlags) {
        throw new RuntimeException("Stub!");
    }

    public abstract int checkCallingOrSelfUriPermission(Uri var1, int var2);

    @NonNull
    public int[] checkCallingOrSelfUriPermissions(@NonNull List<Uri> uris, int modeFlags) {
        throw new RuntimeException("Stub!");
    }

    public abstract int checkUriPermission(@Nullable Uri var1, @Nullable String var2, @Nullable String var3, int var4, int var5, int var6);

    public abstract void enforceUriPermission(Uri var1, int var2, int var3, int var4, String var5);

    public abstract void enforceCallingUriPermission(Uri var1, int var2, String var3);

    public abstract void enforceCallingOrSelfUriPermission(Uri var1, int var2, String var3);

    public abstract void enforceUriPermission(@Nullable Uri var1, @Nullable String var2, @Nullable String var3, int var4, int var5, int var6, @Nullable String var7);

    public abstract Context createPackageContext(String var1, int var2) throws NameNotFoundException;

    public abstract Context createContextForSplit(String var1) throws NameNotFoundException;

    public abstract Context createConfigurationContext(@NonNull Configuration var1);

    public abstract Context createDisplayContext(@NonNull Display var1);

    @NonNull
    public Context createWindowContext(int type, @Nullable Bundle options) {
        throw new RuntimeException("Stub!");
    }

    @NonNull
    public Context createWindowContext(@NonNull Display display, int type, @Nullable Bundle options) {
        throw new RuntimeException("Stub!");
    }

    @NonNull
    public Context createContext(@NonNull ContextParams contextParams) {
        throw new RuntimeException("Stub!");
    }

    @NonNull
    public Context createAttributionContext(@Nullable String attributionTag) {
        throw new RuntimeException("Stub!");
    }

    public abstract Context createDeviceProtectedStorageContext();

    @Nullable
    public Display getDisplay() {
        throw new RuntimeException("Stub!");
    }

    public boolean isRestricted() {
        throw new RuntimeException("Stub!");
    }

    public abstract boolean isDeviceProtectedStorage();

    public boolean isUiContext() {
        throw new RuntimeException("Stub!");
    }

    public int getThemeResId() {
        throw new RuntimeException("Stub!");
    }
}
