package co.infinum.goldfinger.rx;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import co.infinum.goldfinger.Goldfinger;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

class RxGoldfingerImpl implements RxGoldfinger {

    @NonNull private final Goldfinger goldfinger;
    @Nullable private RxGoldfingerCallback callback;

    RxGoldfingerImpl(@NonNull Goldfinger goldfinger) {
        this.goldfinger = goldfinger;
    }

    @NonNull
    @Override
    public Observable<Goldfinger.Result> authenticate(@NonNull final Goldfinger.PromptParams params) {
        return Observable.create(new ObservableOnSubscribe<Goldfinger.Result>() {
            @Override
            public void subscribe(ObservableEmitter<Goldfinger.Result> observableEmitter) {
                callback = new RxGoldfingerCallback(observableEmitter);
                goldfinger.authenticate(params, callback);
            }
        });
    }

     @Override
    public boolean canAuthenticate() {
        return goldfinger.canAuthenticate();
    }

    @Override
    public boolean canAuthenticate(int authenticators) {
        return goldfinger.canAuthenticate(authenticators);
    }

    @Override
    public void cancel() {
        if (callback != null) {
            callback.cancel();
        }
        goldfinger.cancel();
    }

    @Override
    public Observable<Goldfinger.Result> decrypt(
        @NonNull final Goldfinger.PromptParams params,
        @NonNull final String key,
        @NonNull final String value
    ) {
        return Observable.create(new ObservableOnSubscribe<Goldfinger.Result>() {
            @Override
            public void subscribe(ObservableEmitter<Goldfinger.Result> observableEmitter) {
                callback = new RxGoldfingerCallback(observableEmitter);
                goldfinger.decrypt(params, key, value, callback);
            }
        });
    }

    @Override
    public Observable<Goldfinger.Result> encrypt(
        @NonNull final Goldfinger.PromptParams params,
        @NonNull final String key,
        @NonNull final String value
    ) {
        return Observable.create(new ObservableOnSubscribe<Goldfinger.Result>() {
            @Override
            public void subscribe(ObservableEmitter<Goldfinger.Result> observableEmitter) {
                callback = new RxGoldfingerCallback(observableEmitter);
                goldfinger.encrypt(params, key, value, callback);
            }
        });
    }

    @Override
    public boolean hasEnrolledFingerprint() {
        return goldfinger.hasEnrolledFingerprint();
    }

    @Override
    public boolean hasEnrolledFingerprint(int authenticators) {
        return goldfinger.hasEnrolledFingerprint(authenticators);
    }

    @Override
    public boolean hasEnrolledBiometrics(int authenticators) {
        return goldfinger.hasEnrolledBiometrics(authenticators);
    }

    @Override
    public boolean hasFingerprintHardware() {
        return goldfinger.hasFingerprintHardware();
    }

    @Override
    public boolean hasFingerprintHardware(int authenticators) {
        return goldfinger.hasFingerprintHardware(authenticators);
    }

    @Override
    public boolean hasBiometricHardware(int authenticators) {
        return goldfinger.hasBiometricHardware(authenticators);
    }
}
