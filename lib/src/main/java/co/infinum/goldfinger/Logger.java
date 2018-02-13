package co.infinum.goldfinger;

import android.util.Log;

import java.util.Locale;

class Logger {

    private static final String TAG = "Goldfinger";

    private final boolean enabled;

    Logger(boolean enabled) {
        this.enabled = enabled;
    }

    void log(String message, Object... args) {
        if (enabled) {
            Log.i(TAG, String.format(Locale.US, message, args));
        }
    }

    void log(Throwable t) {
        if (enabled) {
            Log.e(TAG, t.toString());
        }
    }
}
