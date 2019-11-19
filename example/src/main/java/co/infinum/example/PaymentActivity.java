package co.infinum.example;

import androidx.annotation.NonNull;
import co.infinum.goldfinger.Goldfinger;

public class PaymentActivity extends BasePaymentActivity {

    private Goldfinger goldfinger;

    @Override
    protected void authenticateUser() {
        this.goldfinger.authenticate(buildPromptParams(), new Goldfinger.Callback() {
            @Override
            public void onError(@NonNull Exception e) {
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
