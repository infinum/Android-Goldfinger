package co.infinum.example;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import co.infinum.goldfinger.Goldfinger;

@RequiresApi(Build.VERSION_CODES.M)
public class LoginActivity extends BaseLoginActivity {

    private Goldfinger goldfinger;

    @Override
    protected void decryptBiometricPin(String encryptedPin) {
        goldfinger.decrypt(buildPromptParams(), "fp_pin", encryptedPin, new Goldfinger.Callback() {
            @Override
            public void onError(@NonNull Exception e) {
                handleGoldfingerError();
            }

            @Override
            public void onResult(@NonNull Goldfinger.Result result) {
                handleGoldfingerResult(result);
            }
        });
    }

    @Override
    protected void initGoldfinger() {
        this.goldfinger = new Goldfinger.Builder(this)
            .logEnabled(true)
            .build();
    }
}
