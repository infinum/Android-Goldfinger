package co.infinum.goldfinger.rx;

import co.infinum.goldfinger.Error;
import co.infinum.goldfinger.Goldfinger;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

import static co.infinum.goldfinger.LogUtils.log;

class RxGoldfingerImpl implements RxGoldfinger {

    private static final String MULTIPLE_SUBSCRIBERS_ISSUE =
        "Only single subscriber should be used. Fingerprint authentication is implemented "
            + "as cold observable and for that reason it is started on first subscribe. Because of "
            + "that, other subscribers might miss already dispatched events.";

    private final Goldfinger goldfinger;
    private PublishSubject<GoldfingerEvent> subject;
    private final Goldfinger.Callback callback = new Goldfinger.Callback() {

        @Override
        public void onError(Error error) {
            subject.onNext(new GoldfingerEvent.OnError(error));
            if (error.isCritical()) {
                subject.onComplete();
            }
        }

        @Override
        public void onReady() {
            subject.onNext(new GoldfingerEvent.OnReady());
        }

        @Override
        public void onSuccess(String value) {
            subject.onNext(new GoldfingerEvent.OnSuccess(value));
            subject.onComplete();
        }
    };

    RxGoldfingerImpl(Goldfinger goldfinger) {
        this.goldfinger = goldfinger;
    }

    @Override
    public Observable<GoldfingerEvent> authenticate() {
        createNewObservable();
        return subject.doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) {
                if (!subject.hasObservers()) {
                    goldfinger.authenticate(callback);
                } else {
                    log(MULTIPLE_SUBSCRIBERS_ISSUE);
                }
            }
        });
    }

    @Override
    public void cancel() {
        goldfinger.cancel();
    }

    @Override
    public Observable<GoldfingerEvent> decrypt(final String keyName, final String value) {
        createNewObservable();
        return subject.doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) {
                if (!subject.hasObservers()) {
                    goldfinger.decrypt(keyName, value, callback);
                } else {
                    log(MULTIPLE_SUBSCRIBERS_ISSUE);
                }
            }
        });
    }

    @Override
    public Observable<GoldfingerEvent> encrypt(final String keyName, final String value) {
        createNewObservable();
        return subject.doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) {
                if (!subject.hasObservers()) {
                    goldfinger.encrypt(keyName, value, callback);
                } else {
                    log(MULTIPLE_SUBSCRIBERS_ISSUE);
                }
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

    private void completeObservable() {
        goldfinger.cancel();
        if (this.subject != null && !this.subject.hasComplete()) {
            this.subject.onComplete();
            this.subject = null;
        }
    }

    private void createNewObservable() {
        completeObservable();
        this.subject = PublishSubject.create();
    }
}
