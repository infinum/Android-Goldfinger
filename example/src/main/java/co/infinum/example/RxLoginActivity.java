package co.infinum.example;

import android.os.Build;

import androidx.annotation.RequiresApi;
import co.infinum.goldfinger.Goldfinger;
import co.infinum.goldfinger.rx.RxGoldfinger;
import io.reactivex.observers.DisposableObserver;

@RequiresApi(Build.VERSION_CODES.M)
public class RxLoginActivity extends BaseLoginActivity {

    private RxGoldfinger goldfinger;

    @Override
    protected void decryptFingerprintPin(String encryptedPin) {
        goldfinger.decrypt(buildPromptParams(), "fp_pin", encryptedPin)
            .subscribe(new DisposableObserver<Goldfinger.Result>() {
                @Override
                public void onComplete() {
                }

                @Override
                public void onError(Throwable e) {
                    handleGoldfingerError();
                }

                @Override
                public void onNext(Goldfinger.Result result) {
                    handleGoldfingerResult(result);
                }
            });
    }

    @Override
    protected void initGoldfinger() {
        this.goldfinger = new RxGoldfinger.Builder(this)
            .logEnabled(true)
            .build();
    }
}
