package co.infinum.example;

import android.os.Build;

import androidx.annotation.RequiresApi;

/**
 * Service simulates backend request
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class MockLoginService {

    public MockLoginService() {
    }

    /**
     * Mock real login where you would send PIN, they would do voodoo magic, return
     * whether its true or false.
     */
    public void login(String pin, Callback callback) {
        String realPin = SharedPrefs.getPin();
        if (pin.equals(realPin)) {
            callback.onSuccess();
        } else {
            callback.onFailure();
        }
    }

    interface Callback {

        void onSuccess();
        void onFailure();
    }
}
