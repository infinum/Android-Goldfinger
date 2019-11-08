package co.infinum.example;

import co.infinum.goldfinger.Goldfinger;
import co.infinum.goldfinger.rx.RxGoldfinger;
import io.reactivex.observers.DisposableObserver;

public class RxPaymentActivity extends BasePaymentActivity {

    private RxGoldfinger goldfinger;

    @Override
    protected void authenticateUser() {
        goldfinger.authenticate(buildPromptParams())
            .subscribe(new DisposableObserver<Goldfinger.Result>() {
                @Override
                public void onComplete() {
                }

                @Override
                public void onError(Throwable e) {
                }

                @Override
                public void onNext(Goldfinger.Result result) {
                    handleGoldfingerResult(result);
                }
            });
    }

    @Override
    protected void initGoldfinger() {
        goldfinger = new RxGoldfinger.Builder(this)
            .logEnabled(true)
            .build();
    }
}
