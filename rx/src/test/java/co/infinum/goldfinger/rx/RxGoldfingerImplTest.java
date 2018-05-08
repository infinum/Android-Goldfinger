package co.infinum.goldfinger.rx;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import co.infinum.goldfinger.Goldfinger;
import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RxGoldfingerImplTest {

    private static String KEY = "key";
    private static String VALUE = "value";

    @Mock private Goldfinger goldfinger;
    private DisposableObserver<GoldfingerEvent> observer1 = new TestObserver();
    private DisposableObserver<GoldfingerEvent> observer2 = new TestObserver();
    @InjectMocks private RxGoldfingerImpl rxGoldfinger;

    @Test
    public void authenticate_delegatedOnSubscribe() {
        rxGoldfinger.authenticate().subscribe(observer1);
        verify(goldfinger).authenticate(any(Goldfinger.Callback.class));
    }

    @Test
    public void authenticate_delegatedOnce() {
        Observable<GoldfingerEvent> observable = rxGoldfinger.authenticate();
        observable.subscribe(observer1);
        observable.subscribe(observer2);
        verify(goldfinger, times(1)).authenticate(any(Goldfinger.Callback.class));
    }

    @Test
    public void authenticate_notDelegated() {
        rxGoldfinger.authenticate();
        verify(goldfinger, never()).authenticate(any(Goldfinger.Callback.class));
    }

    @Test
    public void cancel_delegated() {
        rxGoldfinger.cancel();
        verify(goldfinger).cancel();
    }

    @Test
    public void decrypt_delegatedOnSubscribe() {
        rxGoldfinger.decrypt(KEY, VALUE).subscribe(observer1);
        verify(goldfinger).decrypt(matches(KEY), matches(VALUE), any(Goldfinger.Callback.class));
    }

    @Test
    public void decrypt_delegatedOnce() {
        Observable<GoldfingerEvent> observable = rxGoldfinger.decrypt(KEY, VALUE);
        observable.subscribe(observer1);
        observable.subscribe(observer2);
        verify(goldfinger, times(1)).decrypt(matches(KEY), matches(VALUE), any(Goldfinger.Callback.class));
    }

    @Test
    public void decrypt_notDelegated() {
        rxGoldfinger.decrypt(KEY, VALUE);
        verify(goldfinger, never()).decrypt(matches(KEY), matches(VALUE), any(Goldfinger.Callback.class));
    }

    @Test
    public void encrypt_delegatedOnSubscribe() {
        rxGoldfinger.encrypt(KEY, VALUE).subscribe(observer1);
        verify(goldfinger).encrypt(matches(KEY), matches(VALUE), any(Goldfinger.Callback.class));
    }

    @Test
    public void encrypt_delegatedOnce() {
        Observable<GoldfingerEvent> observable = rxGoldfinger.encrypt(KEY, VALUE);
        observable.subscribe(observer1);
        observable.subscribe(observer2);
        verify(goldfinger, times(1)).encrypt(matches(KEY), matches(VALUE), any(Goldfinger.Callback.class));
    }

    @Test
    public void encrypt_notDelegated() {
        rxGoldfinger.encrypt(KEY, VALUE);
        verify(goldfinger, never()).encrypt(matches(KEY), matches(VALUE), any(Goldfinger.Callback.class));
    }
}
