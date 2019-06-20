package co.infinum.goldfinger.rx;

import co.infinum.goldfinger.Goldfinger;
import io.reactivex.observers.DisposableObserver;

public class TestObserver extends DisposableObserver<Goldfinger.Result> {

    @Override
    public void onComplete() {
    }

    @Override
    public void onError(Throwable e) {
    }

    @Override
    public void onNext(Goldfinger.Result goldfingerEvent) {
    }
}
