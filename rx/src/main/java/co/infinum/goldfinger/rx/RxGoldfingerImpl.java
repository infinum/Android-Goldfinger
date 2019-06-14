package co.infinum.goldfinger.rx;

import androidx.annotation.NonNull;
import co.infinum.goldfinger.Goldfinger;
import co.infinum.goldfinger.GoldfingerParams;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

class RxGoldfingerImpl implements RxGoldfinger {

    private final Goldfinger goldfinger;

    RxGoldfingerImpl(Goldfinger goldfinger) {
        this.goldfinger = goldfinger;
    }

    @NonNull
    @Override
    public Observable<Goldfinger.Result> authenticate(@NonNull final GoldfingerParams params) {
        return Observable.create(new ObservableOnSubscribe<Goldfinger.Result>() {
            @Override
            public void subscribe(ObservableEmitter<Goldfinger.Result> observableEmitter) {
                RxGoldfingerCallback callback = new RxGoldfingerCallback(observableEmitter);
                RxGoldfingerImpl.this.goldfinger.authenticate(params, callback);
            }
        });
    }

    @Override
    public void cancel() {
        goldfinger.cancel();
    }

    @NonNull
    @Override
    public Observable<Goldfinger.Result> decrypt(@NonNull final GoldfingerParams params) {
        return Observable.create(new ObservableOnSubscribe<Goldfinger.Result>() {
            @Override
            public void subscribe(ObservableEmitter<Goldfinger.Result> observableEmitter) {
                RxGoldfingerCallback callback = new RxGoldfingerCallback(observableEmitter);
                RxGoldfingerImpl.this.goldfinger.decrypt(params, callback);
            }
        });
    }

    @NonNull
    @Override
    public Observable<Goldfinger.Result> encrypt(@NonNull final GoldfingerParams params) {
        return Observable.create(new ObservableOnSubscribe<Goldfinger.Result>() {
            @Override
            public void subscribe(ObservableEmitter<Goldfinger.Result> observableEmitter) {
                RxGoldfingerCallback callback = new RxGoldfingerCallback(observableEmitter);
                RxGoldfingerImpl.this.goldfinger.encrypt(params, callback);
            }
        });
    }

    @Override
    public boolean hasFingerprintHardware() {
        return goldfinger.hasFingerprintHardware();
    }
}
