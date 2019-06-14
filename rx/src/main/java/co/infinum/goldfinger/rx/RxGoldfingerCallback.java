package co.infinum.goldfinger.rx;

import co.infinum.goldfinger.Error;
import co.infinum.goldfinger.Goldfinger;
import co.infinum.goldfinger.GoldfingerCallback;
import io.reactivex.ObservableEmitter;

class RxGoldfingerCallback extends GoldfingerCallback {

    private final ObservableEmitter<Goldfinger.Result> emitter;

    RxGoldfingerCallback(ObservableEmitter<Goldfinger.Result> emitter) {
        this.emitter = emitter;
    }

    @Override
    public void onError(Error error) {
        if (!emitter.isDisposed()) {
            emitter.onError(new RxGoldfingerException(error));
        }
    }

    @Override
    public void onFail() {
        if (!emitter.isDisposed()) {
            emitter.onNext(new Goldfinger.Result(Goldfinger.Reason.FAIL, null));
        }
    }

    @Override
    public void onSuccess(Goldfinger.Result result) {
        if (!emitter.isDisposed()) {
            emitter.onNext(result);
            emitter.onComplete();
        }
    }
}
