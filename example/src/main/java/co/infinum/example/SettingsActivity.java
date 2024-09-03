package co.infinum.example;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import co.infinum.goldfinger.Goldfinger;

@RequiresApi(Build.VERSION_CODES.M)
public class SettingsActivity extends BaseSettingsActivity {

    private Goldfinger goldfinger;

    @Override
    public void initGoldfinger() {
        goldfinger = new Goldfinger.Builder(this)
            .logEnabled(true)
            .build();
    }

    @Override
    protected void encryptBiometricPin(String pin) {
        goldfinger.encrypt(buildPromptParams(), "fp_pin", pin, new Goldfinger.Callback() {
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
}
