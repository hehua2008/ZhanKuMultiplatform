package android.content;

/**
 * @formatter:off
 */
@SuppressWarnings({"unchecked", "deprecation", "all"})
public abstract class Context {

public Context() { throw new RuntimeException("Stub!"); }

public abstract android.content.res.AssetManager getAssets();

public abstract android.content.res.Resources getResources();

public abstract android.content.pm.PackageManager getPackageManager();

public abstract ContentResolver getContentResolver();

public abstract android.os.Looper getMainLooper();

public java.util.concurrent.Executor getMainExecutor() { throw new RuntimeException("Stub!"); }

public abstract Context getApplicationContext();

public void registerComponentCallbacks(ComponentCallbacks callback) { throw new RuntimeException("Stub!"); }

public void unregisterComponentCallbacks(ComponentCallbacks callback) { throw new RuntimeException("Stub!"); }

@androidx.annotation.NonNull
public final CharSequence getText(int resId) { throw new RuntimeException("Stub!"); }

@androidx.annotation.NonNull
public final String getString(int resId) { throw new RuntimeException("Stub!"); }

@androidx.annotation.NonNull
public final String getString(int resId, Object... formatArgs) { throw new RuntimeException("Stub!"); }

public final int getColor(int id) { throw new RuntimeException("Stub!"); }

@androidx.annotation.Nullable
public final android.graphics.drawable.Drawable getDrawable(int id) { throw new RuntimeException("Stub!"); }

@androidx.annotation.NonNull
public final android.content.res.ColorStateList getColorStateList(int id) { throw new RuntimeException("Stub!"); }

public abstract void setTheme(int resid);

@android.view.ViewDebug.ExportedProperty(deepExport=true)
public abstract android.content.res.Resources.Theme getTheme();

@androidx.annotation.NonNull
public final android.content.res.TypedArray obtainStyledAttributes(@androidx.annotation.NonNull int[] attrs) { throw new RuntimeException("Stub!"); }

@androidx.annotation.NonNull
public final android.content.res.TypedArray obtainStyledAttributes(int resid, @androidx.annotation.NonNull int[] attrs) throws android.content.res.Resources.NotFoundException { throw new RuntimeException("Stub!"); }

@androidx.annotation.NonNull
public final android.content.res.TypedArray obtainStyledAttributes(@androidx.annotation.Nullable android.util.AttributeSet set, @androidx.annotation.NonNull int[] attrs) { throw new RuntimeException("Stub!"); }

@androidx.annotation.NonNull
public final android.content.res.TypedArray obtainStyledAttributes(@androidx.annotation.Nullable android.util.AttributeSet set, @androidx.annotation.NonNull int[] attrs, int defStyleAttr, int defStyleRes) { throw new RuntimeException("Stub!"); }

public abstract ClassLoader getClassLoader();

public abstract String getPackageName();

@androidx.annotation.NonNull
public String getOpPackageName() { throw new RuntimeException("Stub!"); }

@androidx.annotation.Nullable
public String getAttributionTag() { throw new RuntimeException("Stub!"); }

@androidx.annotation.NonNull
public AttributionSource getAttributionSource() { throw new RuntimeException("Stub!"); }

@androidx.annotation.Nullable
public ContextParams getParams() { throw new RuntimeException("Stub!"); }

public abstract android.content.pm.ApplicationInfo getApplicationInfo();

public abstract String getPackageResourcePath();

public abstract String getPackageCodePath();

public abstract SharedPreferences getSharedPreferences(String name, int mode);

public abstract boolean moveSharedPreferencesFrom(Context sourceContext, String name);

public abstract boolean deleteSharedPreferences(String name);

public abstract java.io.FileInputStream openFileInput(String name) throws java.io.FileNotFoundException;

public abstract java.io.FileOutputStream openFileOutput(String name, int mode) throws java.io.FileNotFoundException;

public abstract boolean deleteFile(String name);

public abstract java.io.File getFileStreamPath(String name);

public abstract java.io.File getDataDir();

public abstract java.io.File getFilesDir();

public abstract java.io.File getNoBackupFilesDir();

@androidx.annotation.Nullable
public abstract java.io.File getExternalFilesDir(@androidx.annotation.Nullable String type);

public abstract java.io.File[] getExternalFilesDirs(String type);

public abstract java.io.File getObbDir();

public abstract java.io.File[] getObbDirs();

public abstract java.io.File getCacheDir();

public abstract java.io.File getCodeCacheDir();

@androidx.annotation.Nullable
public abstract java.io.File getExternalCacheDir();

public abstract java.io.File[] getExternalCacheDirs();

@Deprecated
public abstract java.io.File[] getExternalMediaDirs();

public abstract String[] fileList();

public abstract java.io.File getDir(String name, int mode);

public abstract android.database.sqlite.SQLiteDatabase openOrCreateDatabase(String name, int mode, android.database.sqlite.SQLiteDatabase.CursorFactory factory);

public abstract android.database.sqlite.SQLiteDatabase openOrCreateDatabase(String name, int mode, android.database.sqlite.SQLiteDatabase.CursorFactory factory, @androidx.annotation.Nullable android.database.DatabaseErrorHandler errorHandler);

public abstract boolean moveDatabaseFrom(Context sourceContext, String name);

public abstract boolean deleteDatabase(String name);

public abstract java.io.File getDatabasePath(String name);

public abstract String[] databaseList();

@Deprecated
public abstract android.graphics.drawable.Drawable getWallpaper();

@Deprecated
public abstract android.graphics.drawable.Drawable peekWallpaper();

@Deprecated
public abstract int getWallpaperDesiredMinimumWidth();

@Deprecated
public abstract int getWallpaperDesiredMinimumHeight();

@Deprecated
public abstract void setWallpaper(android.graphics.Bitmap bitmap) throws java.io.IOException;

@Deprecated
public abstract void setWallpaper(java.io.InputStream data) throws java.io.IOException;

@Deprecated
public abstract void clearWallpaper() throws java.io.IOException;

public abstract void startActivity(Intent intent);

public abstract void startActivity(Intent intent, @androidx.annotation.Nullable android.os.Bundle options);

public abstract void startActivities(Intent[] intents);

public abstract void startActivities(Intent[] intents, android.os.Bundle options);

public abstract void startIntentSender(IntentSender intent, @androidx.annotation.Nullable Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags) throws IntentSender.SendIntentException;

public abstract void startIntentSender(IntentSender intent, @androidx.annotation.Nullable Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, @androidx.annotation.Nullable android.os.Bundle options) throws IntentSender.SendIntentException;

public abstract void sendBroadcast(Intent intent);

public abstract void sendBroadcast(Intent intent, @androidx.annotation.Nullable String receiverPermission);

public void sendBroadcastWithMultiplePermissions(@androidx.annotation.NonNull Intent intent, @androidx.annotation.NonNull String[] receiverPermissions) { throw new RuntimeException("Stub!"); }

public abstract void sendOrderedBroadcast(Intent intent, @androidx.annotation.Nullable String receiverPermission);

public abstract void sendOrderedBroadcast(@androidx.annotation.NonNull Intent intent, @androidx.annotation.Nullable String receiverPermission, @androidx.annotation.Nullable BroadcastReceiver resultReceiver, @androidx.annotation.Nullable android.os.Handler scheduler, int initialCode, @androidx.annotation.Nullable String initialData, @androidx.annotation.Nullable android.os.Bundle initialExtras);

public abstract void sendBroadcastAsUser(Intent intent, android.os.UserHandle user);

public abstract void sendBroadcastAsUser(Intent intent, android.os.UserHandle user, @androidx.annotation.Nullable String receiverPermission);

public abstract void sendOrderedBroadcastAsUser(Intent intent, android.os.UserHandle user, @androidx.annotation.Nullable String receiverPermission, BroadcastReceiver resultReceiver, @androidx.annotation.Nullable android.os.Handler scheduler, int initialCode, @androidx.annotation.Nullable String initialData, @androidx.annotation.Nullable android.os.Bundle initialExtras);

public void sendOrderedBroadcast(@androidx.annotation.NonNull Intent intent, @androidx.annotation.Nullable String receiverPermission, @androidx.annotation.Nullable String receiverAppOp, @androidx.annotation.Nullable BroadcastReceiver resultReceiver, @androidx.annotation.Nullable android.os.Handler scheduler, int initialCode, @androidx.annotation.Nullable String initialData, @androidx.annotation.Nullable android.os.Bundle initialExtras) { throw new RuntimeException("Stub!"); }

@Deprecated
public abstract void sendStickyBroadcast(Intent intent);

@Deprecated
public void sendStickyBroadcast(@androidx.annotation.NonNull Intent intent, @androidx.annotation.Nullable android.os.Bundle options) { throw new RuntimeException("Stub!"); }

@Deprecated
public abstract void sendStickyOrderedBroadcast(Intent intent, BroadcastReceiver resultReceiver, @androidx.annotation.Nullable android.os.Handler scheduler, int initialCode, @androidx.annotation.Nullable String initialData, @androidx.annotation.Nullable android.os.Bundle initialExtras);

@Deprecated
public abstract void removeStickyBroadcast(Intent intent);

@Deprecated
public abstract void sendStickyBroadcastAsUser(Intent intent, android.os.UserHandle user);

@Deprecated
public abstract void sendStickyOrderedBroadcastAsUser(Intent intent, android.os.UserHandle user, BroadcastReceiver resultReceiver, @androidx.annotation.Nullable android.os.Handler scheduler, int initialCode, @androidx.annotation.Nullable String initialData, @androidx.annotation.Nullable android.os.Bundle initialExtras);

@Deprecated
public abstract void removeStickyBroadcastAsUser(Intent intent, android.os.UserHandle user);

@androidx.annotation.Nullable
public abstract Intent registerReceiver(@androidx.annotation.Nullable BroadcastReceiver receiver, IntentFilter filter);

@androidx.annotation.Nullable
public abstract Intent registerReceiver(@androidx.annotation.Nullable BroadcastReceiver receiver, IntentFilter filter, int flags);

@androidx.annotation.Nullable
public abstract Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, @androidx.annotation.Nullable String broadcastPermission, @androidx.annotation.Nullable android.os.Handler scheduler);

@androidx.annotation.Nullable
public abstract Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, @androidx.annotation.Nullable String broadcastPermission, @androidx.annotation.Nullable android.os.Handler scheduler, int flags);

public abstract void unregisterReceiver(BroadcastReceiver receiver);

@androidx.annotation.Nullable
public abstract ComponentName startService(Intent service);

@androidx.annotation.Nullable
public abstract ComponentName startForegroundService(Intent service);

public abstract boolean stopService(Intent service);

public abstract boolean bindService(Intent service, @androidx.annotation.NonNull ServiceConnection conn, int flags);

public boolean bindService(@androidx.annotation.NonNull Intent service, int flags, @androidx.annotation.NonNull java.util.concurrent.Executor executor, @androidx.annotation.NonNull ServiceConnection conn) { throw new RuntimeException("Stub!"); }

public boolean bindIsolatedService(@androidx.annotation.NonNull Intent service, int flags, @androidx.annotation.NonNull String instanceName, @androidx.annotation.NonNull java.util.concurrent.Executor executor, @androidx.annotation.NonNull ServiceConnection conn) { throw new RuntimeException("Stub!"); }

public boolean bindServiceAsUser(@androidx.annotation.NonNull Intent service, @androidx.annotation.NonNull ServiceConnection conn, int flags, @androidx.annotation.NonNull android.os.UserHandle user) { throw new RuntimeException("Stub!"); }

public void updateServiceGroup(@androidx.annotation.NonNull ServiceConnection conn, int group, int importance) { throw new RuntimeException("Stub!"); }

public abstract void unbindService(@androidx.annotation.NonNull ServiceConnection conn);

public abstract boolean startInstrumentation(@androidx.annotation.NonNull ComponentName className, @androidx.annotation.Nullable String profileFile, @androidx.annotation.Nullable android.os.Bundle arguments);

public abstract Object getSystemService(@androidx.annotation.NonNull String name);

public final <T> T getSystemService(@androidx.annotation.NonNull Class<T> serviceClass) { throw new RuntimeException("Stub!"); }

@androidx.annotation.Nullable
public abstract String getSystemServiceName(@androidx.annotation.NonNull Class<?> serviceClass);

public abstract int checkPermission(@androidx.annotation.NonNull String permission, int pid, int uid);

public abstract int checkCallingPermission(@androidx.annotation.NonNull String permission);

public abstract int checkCallingOrSelfPermission(@androidx.annotation.NonNull String permission);

public abstract int checkSelfPermission(@androidx.annotation.NonNull String permission);

public abstract void enforcePermission(@androidx.annotation.NonNull String permission, int pid, int uid, @androidx.annotation.Nullable String message);

public abstract void enforceCallingPermission(@androidx.annotation.NonNull String permission, @androidx.annotation.Nullable String message);

public abstract void enforceCallingOrSelfPermission(@androidx.annotation.NonNull String permission, @androidx.annotation.Nullable String message);

public abstract void grantUriPermission(String toPackage, android.net.Uri uri, int modeFlags);

public abstract void revokeUriPermission(android.net.Uri uri, int modeFlags);

public abstract void revokeUriPermission(String toPackage, android.net.Uri uri, int modeFlags);

public abstract int checkUriPermission(android.net.Uri uri, int pid, int uid, int modeFlags);

@androidx.annotation.NonNull
public int[] checkUriPermissions(@androidx.annotation.NonNull java.util.List<android.net.Uri> uris, int pid, int uid, int modeFlags) { throw new RuntimeException("Stub!"); }

public abstract int checkCallingUriPermission(android.net.Uri uri, int modeFlags);

@androidx.annotation.NonNull
public int[] checkCallingUriPermissions(@androidx.annotation.NonNull java.util.List<android.net.Uri> uris, int modeFlags) { throw new RuntimeException("Stub!"); }

public abstract int checkCallingOrSelfUriPermission(android.net.Uri uri, int modeFlags);

@androidx.annotation.NonNull
public int[] checkCallingOrSelfUriPermissions(@androidx.annotation.NonNull java.util.List<android.net.Uri> uris, int modeFlags) { throw new RuntimeException("Stub!"); }

public abstract int checkUriPermission(@androidx.annotation.Nullable android.net.Uri uri, @androidx.annotation.Nullable String readPermission, @androidx.annotation.Nullable String writePermission, int pid, int uid, int modeFlags);

public abstract void enforceUriPermission(android.net.Uri uri, int pid, int uid, int modeFlags, String message);

public abstract void enforceCallingUriPermission(android.net.Uri uri, int modeFlags, String message);

public abstract void enforceCallingOrSelfUriPermission(android.net.Uri uri, int modeFlags, String message);

public abstract void enforceUriPermission(@androidx.annotation.Nullable android.net.Uri uri, @androidx.annotation.Nullable String readPermission, @androidx.annotation.Nullable String writePermission, int pid, int uid, int modeFlags, @androidx.annotation.Nullable String message);

public abstract Context createPackageContext(String packageName, int flags) throws android.content.pm.PackageManager.NameNotFoundException;

public abstract Context createContextForSplit(String splitName) throws android.content.pm.PackageManager.NameNotFoundException;

public abstract Context createConfigurationContext(@androidx.annotation.NonNull android.content.res.Configuration overrideConfiguration);

public abstract Context createDisplayContext(@androidx.annotation.NonNull android.view.Display display);

@androidx.annotation.NonNull
public Context createWindowContext(int type, @androidx.annotation.Nullable android.os.Bundle options) { throw new RuntimeException("Stub!"); }

@androidx.annotation.NonNull
public Context createWindowContext(@androidx.annotation.NonNull android.view.Display display, int type, @androidx.annotation.Nullable android.os.Bundle options) { throw new RuntimeException("Stub!"); }

@androidx.annotation.NonNull
public Context createContext(@androidx.annotation.NonNull ContextParams contextParams) { throw new RuntimeException("Stub!"); }

@androidx.annotation.NonNull
public Context createAttributionContext(@androidx.annotation.Nullable String attributionTag) { throw new RuntimeException("Stub!"); }

public abstract Context createDeviceProtectedStorageContext();

@androidx.annotation.Nullable
public android.view.Display getDisplay() { throw new RuntimeException("Stub!"); }

public boolean isRestricted() { throw new RuntimeException("Stub!"); }

public abstract boolean isDeviceProtectedStorage();

public boolean isUiContext() { throw new RuntimeException("Stub!"); }

public static final String ACCESSIBILITY_SERVICE = "accessibility";

public static final String ACCOUNT_SERVICE = "account";

public static final String ACTIVITY_SERVICE = "activity";

public static final String ALARM_SERVICE = "alarm";

public static final String APPWIDGET_SERVICE = "appwidget";

public static final String APP_OPS_SERVICE = "appops";

public static final String APP_SEARCH_SERVICE = "app_search";

public static final String AUDIO_SERVICE = "audio";

public static final String BATTERY_SERVICE = "batterymanager";

public static final int BIND_ABOVE_CLIENT = 8; // 0x8

public static final int BIND_ADJUST_WITH_ACTIVITY = 128; // 0x80

public static final int BIND_ALLOW_OOM_MANAGEMENT = 16; // 0x10

public static final int BIND_AUTO_CREATE = 1; // 0x1

public static final int BIND_DEBUG_UNBIND = 2; // 0x2

public static final int BIND_EXTERNAL_SERVICE = -2147483648; // 0x80000000

public static final int BIND_IMPORTANT = 64; // 0x40

public static final int BIND_INCLUDE_CAPABILITIES = 4096; // 0x1000

public static final int BIND_NOT_FOREGROUND = 4; // 0x4

public static final int BIND_NOT_PERCEPTIBLE = 256; // 0x100

public static final int BIND_WAIVE_PRIORITY = 32; // 0x20

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

public static final int CONTEXT_IGNORE_SECURITY = 2; // 0x2

public static final int CONTEXT_INCLUDE_CODE = 1; // 0x1

public static final int CONTEXT_RESTRICTED = 4; // 0x4

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

public static final int MODE_APPEND = 32768; // 0x8000

public static final int MODE_ENABLE_WRITE_AHEAD_LOGGING = 8; // 0x8

@Deprecated public static final int MODE_MULTI_PROCESS = 4; // 0x4

public static final int MODE_NO_LOCALIZED_COLLATORS = 16; // 0x10

public static final int MODE_PRIVATE = 0; // 0x0

@Deprecated public static final int MODE_WORLD_READABLE = 1; // 0x1

@Deprecated public static final int MODE_WORLD_WRITEABLE = 2; // 0x2

public static final String NETWORK_STATS_SERVICE = "netstats";

public static final String NFC_SERVICE = "nfc";

public static final String NOTIFICATION_SERVICE = "notification";

public static final String NSD_SERVICE = "servicediscovery";

public static final String PEOPLE_SERVICE = "people";

public static final String PERFORMANCE_HINT_SERVICE = "performance_hint";

public static final String POWER_SERVICE = "power";

public static final String PRINT_SERVICE = "print";

public static final int RECEIVER_VISIBLE_TO_INSTANT_APPS = 1; // 0x1

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

@Deprecated public static final String VIBRATOR_SERVICE = "vibrator";

public static final String VPN_MANAGEMENT_SERVICE = "vpn_management";

public static final String WALLPAPER_SERVICE = "wallpaper";

public static final String WIFI_AWARE_SERVICE = "wifiaware";

public static final String WIFI_P2P_SERVICE = "wifip2p";

public static final String WIFI_RTT_RANGING_SERVICE = "wifirtt";

public static final String WIFI_SERVICE = "wifi";

public static final String WINDOW_SERVICE = "window";

public int getThemeResId() { throw new RuntimeException("Stub!"); }
}
