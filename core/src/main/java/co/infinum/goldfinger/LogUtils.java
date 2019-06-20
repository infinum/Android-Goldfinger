package co.infinum.goldfinger;

import android.util.Log;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

class LogUtils {

    private static final String TAG = "Goldfinger";
    private static boolean enabled = false;

    private LogUtils() {
    }

    static void log(@NonNull String message, Object... args) {
        if (enabled) {
            Log.i(TAG, String.format(Locale.US, message, args));
        }
    }

    static void log(@NonNull Throwable t) {
        if (enabled) {
            Log.e(TAG, t.toString());
        }
    }

    static void setEnabled(boolean enabled) {
        LogUtils.enabled = enabled;
    }
}
