package co.infinum.goldfinger.rx;

import androidx.annotation.NonNull;
import co.infinum.goldfinger.Goldfinger;
import co.infinum.goldfinger.GoldfingerParams;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

class RxGoldfingerImpl implements RxGoldfinger {

    private final Goldfinger goldfinger;

    RxGoldfingerImpl(Goldfinger goldfinger) {
        this.goldfinger = goldfinger;
    }

    @NonNull
    @Override
    public Single<String> authenticate(@NonNull final GoldfingerParams params) {
        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(SingleEmitter<String> emitter) {
                RxGoldfingerCallback callback = new RxGoldfingerCallback(emitter);
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
    public Single<String> decrypt(@NonNull final GoldfingerParams params) {
        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(SingleEmitter<String> emitter) {
                RxGoldfingerCallback callback = new RxGoldfingerCallback(emitter);
                RxGoldfingerImpl.this.goldfinger.decrypt(params, callback);
            }
        });
    }

    @NonNull
    @Override
    public Single<String> encrypt(@NonNull final GoldfingerParams params) {
        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(SingleEmitter<String> emitter) {
                RxGoldfingerCallback callback = new RxGoldfingerCallback(emitter);
                RxGoldfingerImpl.this.goldfinger.encrypt(params, callback);
            }
        });
    }

    @Override
    public boolean hasFingerprintHardware() {
        return goldfinger.hasFingerprintHardware();
    }
}
