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
    public Observable<Goldfinger.Result> authenticate(@NonNull final Goldfinger.Params goldfingerParams) {
        return Observable.create(new ObservableOnSubscribe<Goldfinger.Result>() {
            @Override
            public void subscribe(ObservableEmitter<Goldfinger.Result> observableEmitter) {
                callback = new RxGoldfingerCallback(observableEmitter);
                goldfinger.authenticate(goldfingerParams, callback);
            }
        });
    }

    @Override
    public boolean canAuthenticate() {
        return goldfinger.canAuthenticate();
    }

    @Override
    public void cancel() {
        if (callback != null) {
            callback.cancel();
        }
        goldfinger.cancel();
    }
}
