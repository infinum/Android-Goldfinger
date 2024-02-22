package co.infinum.goldfinger.crypto.impl;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

/**
 * Unlocked Cipher factory which can be reused in the app if needed
 * outside of Goldfinger flow.
 * <p>
 * Standard use case is PIN/Biometrics login flow. If user does not want
 * to use biometrics, you can reuse this cipher to encrypt/decrypt his PIN
 * without the need of user authentication.
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class UnlockedAesCipherFactory extends AesCipherFactory {

    public UnlockedAesCipherFactory(@NonNull Context context) {
        super(context);
    }

    @Override
    protected boolean isUserAuthRequired() {
        return false;
    }
}
