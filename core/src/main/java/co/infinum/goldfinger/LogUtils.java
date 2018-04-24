package co.infinum.goldfinger;

import android.support.annotation.RestrictTo;
import android.util.Log;

import java.util.Locale;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class LogUtils {

    private static final String TAG = "Goldfinger";
    private static boolean enabled = false;

    public static void log(String message, Object... args) {
        if (enabled) {
            Log.i(TAG, String.format(Locale.US, message, args));
        }
    }

    public static void log(Throwable t) {
        if (enabled) {
            Log.e(TAG, t.toString());
        }
    }

    public static void setEnabled(boolean enabled) {
        LogUtils.enabled = enabled;
    }
}
