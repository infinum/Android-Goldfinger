package co.infinum.goldfinger.rx;

import androidx.annotation.NonNull;
import co.infinum.goldfinger.Goldfinger;
import io.reactivex.ObservableEmitter;

class RxGoldfingerCallback implements Goldfinger.Callback {

    @NonNull private final ObservableEmitter<Goldfinger.Result> emitter;

    RxGoldfingerCallback(@NonNull ObservableEmitter<Goldfinger.Result> emitter) {
        this.emitter = emitter;
    }

    @Override
    public void onError(@NonNull Exception e) {
        if (!emitter.isDisposed()) {
            emitter.onError(e);
        }
    }

    @Override
    public void onResult(@NonNull Goldfinger.Result result) {
        if (emitter.isDisposed()) {
            return;
        }

        emitter.onNext(result);
        if (result.type() == Goldfinger.Type.SUCCESS || result.type() == Goldfinger.Type.ERROR) {
            emitter.onComplete();
        }
    }

    void cancel() {
        if (!emitter.isDisposed()) {
            emitter.onComplete();
        }
    }
}
