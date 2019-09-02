package co.infinum.goldfinger.rx;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import co.infinum.goldfinger.Goldfinger;
import co.infinum.goldfinger.GoldfingerParams;
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
    public Observable<Goldfinger.Result> authenticate(@NonNull final GoldfingerParams goldfingerParams) {
        return Observable.create(new ObservableOnSubscribe<Goldfinger.Result>() {
            @Override
            public void subscribe(ObservableEmitter<Goldfinger.Result> observableEmitter) {
                callback = new RxGoldfingerCallback(observableEmitter);
                goldfinger.authenticate(goldfingerParams, callback);
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

    @NonNull
    @Override
    public Observable<Goldfinger.Result> decrypt(@NonNull final GoldfingerParams goldfingerParams) {
        return Observable.create(new ObservableOnSubscribe<Goldfinger.Result>() {
            @Override
            public void subscribe(ObservableEmitter<Goldfinger.Result> observableEmitter) {
                callback = new RxGoldfingerCallback(observableEmitter);
                goldfinger.decrypt(goldfingerParams, callback);
            }
        });
    }

    @NonNull
    @Override
    public Observable<Goldfinger.Result> encrypt(@NonNull final GoldfingerParams goldfingerParams) {
        return Observable.create(new ObservableOnSubscribe<Goldfinger.Result>() {
            @Override
            public void subscribe(ObservableEmitter<Goldfinger.Result> observableEmitter) {
                callback = new RxGoldfingerCallback(observableEmitter);
                goldfinger.encrypt(goldfingerParams, callback);
            }
        });
    }

    @Override
    public boolean hasFingerprintHardware() {
        return goldfinger.hasFingerprintHardware();
    }
}
