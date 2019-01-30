package co.infinum.goldfinger;

import android.util.Log;

import java.util.Locale;

class LogUtils {

    private static final String TAG = "Goldfinger";
    private static boolean enabled = false;

    private LogUtils() {
        /* Hide constructor */
    }

    static void log(String message, Object... args) {
        if (enabled) {
            Log.i(TAG, String.format(Locale.US, message, args));
        }
    }

    static void log(Throwable t) {
        if (enabled) {
            Log.e(TAG, t.toString());
        }
    }

    static void setEnabled(boolean enabled) {
        LogUtils.enabled = enabled;
    }
}
