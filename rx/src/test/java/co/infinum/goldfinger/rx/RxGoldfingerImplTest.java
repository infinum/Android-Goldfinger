package co.infinum.goldfinger.rx;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import co.infinum.goldfinger.Goldfinger;
import io.reactivex.observers.DisposableObserver;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RxGoldfingerImplTest {

    private static String KEY = "key";
    private static String VALUE = "value";

    @Mock private Goldfinger goldfinger;
    private DisposableObserver<Goldfinger.Result> observer = new TestObserver();
    @InjectMocks private RxGoldfingerImpl rxGoldfinger;

    @Test
    public void authenticate_delegatedOnSubscribe() {
        rxGoldfinger.authenticate().subscribe(observer);
        verify(goldfinger).authenticate(any(Goldfinger.Callback.class));
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
        rxGoldfinger.decrypt(KEY, VALUE).subscribe(observer);
        verify(goldfinger).decrypt(matches(KEY), matches(VALUE), any(Goldfinger.Callback.class));
    }

    @Test
    public void decrypt_notDelegated() {
        rxGoldfinger.decrypt(KEY, VALUE);
        verify(goldfinger, never()).decrypt(matches(KEY), matches(VALUE), any(Goldfinger.Callback.class));
    }

    @Test
    public void encrypt_delegatedOnSubscribe() {
        rxGoldfinger.encrypt(KEY, VALUE).subscribe(observer);
        verify(goldfinger).encrypt(matches(KEY), matches(VALUE), any(Goldfinger.Callback.class));
    }

    @Test
    public void encrypt_notDelegated() {
        rxGoldfinger.encrypt(KEY, VALUE);
        verify(goldfinger, never()).encrypt(matches(KEY), matches(VALUE), any(Goldfinger.Callback.class));
    }
}
