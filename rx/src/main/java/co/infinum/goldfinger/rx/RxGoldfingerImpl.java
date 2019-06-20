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

    @Override
    public Observable<Goldfinger.Result> authenticate() {
        return Observable.create(new ObservableOnSubscribe<Goldfinger.Result>() {
            @Override
            public void subscribe(ObservableEmitter<Goldfinger.Result> observableEmitter) {
                callback = new RxGoldfingerCallback(observableEmitter);
                goldfinger.authenticate(callback);
            }
        });
    }

    @Override
    public void cancel() {
        if (callback != null) {
            callback.cancel();
        }
        goldfinger.cancel();
    }

    @Override
    public Observable<Goldfinger.Result> decrypt(final String keyName, final String value) {
        return Observable.create(new ObservableOnSubscribe<Goldfinger.Result>() {
            @Override
            public void subscribe(ObservableEmitter<Goldfinger.Result> observableEmitter) {
                callback = new RxGoldfingerCallback(observableEmitter);
                goldfinger.decrypt(keyName, value, callback);
            }
        });
    }

    @Override
    public Observable<Goldfinger.Result> encrypt(final String keyName, final String value) {
        return Observable.create(new ObservableOnSubscribe<Goldfinger.Result>() {
            @Override
            public void subscribe(ObservableEmitter<Goldfinger.Result> observableEmitter) {
                callback = new RxGoldfingerCallback(observableEmitter);
                goldfinger.encrypt(keyName, value, callback);
            }
        });
    }

    @Override
    public boolean hasEnrolledFingerprint() {
        return goldfinger.hasEnrolledFingerprint();
    }

    @Override
    public boolean hasFingerprintHardware() {
        return goldfinger.hasFingerprintHardware();
    }
}
