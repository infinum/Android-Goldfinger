package co.infinum.goldfinger.rx;

import co.infinum.goldfinger.Error;
import co.infinum.goldfinger.GoldfingerCallback;
import io.reactivex.SingleEmitter;

class RxGoldfingerCallback implements GoldfingerCallback {

    private final SingleEmitter<String> emitter;

    RxGoldfingerCallback(SingleEmitter<String> emitter) {
        this.emitter = emitter;
    }

    @Override
    public void onError(Error error) {
        if (!emitter.isDisposed()) {
            emitter.onError(new RxGoldfingerException(error));
        }
    }

    @Override
    public void onSuccess(String value) {
        if (!emitter.isDisposed()) {
            emitter.onSuccess(value);
        }
    }
}
