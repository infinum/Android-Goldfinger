package co.infinum.goldfinger.rx;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import androidx.fragment.app.FragmentActivity;
import co.infinum.goldfinger.Goldfinger;
import io.reactivex.observers.DisposableObserver;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RxGoldfingerImplTest {

    private static final String KEY = "key";
    private static final String VALUE = "value";

    @Mock private Goldfinger goldfinger;
    @Mock private FragmentActivity activity;
    private DisposableObserver<Goldfinger.Result> observer = new TestObserver();
    @InjectMocks private RxGoldfingerImpl rxGoldfinger;

    @Test
    public void authenticate_delegatedOnSubscribe() {
        Goldfinger.PromptParams params = params();
        rxGoldfinger.authenticate(params).subscribe(observer);
        verify(goldfinger).authenticate(eq(params), any(Goldfinger.Callback.class));
    }

    @Test
    public void authenticate_notDelegated() {
        Goldfinger.PromptParams params = params();
        rxGoldfinger.authenticate(params);
        verify(goldfinger, never()).authenticate(eq(params), any(Goldfinger.Callback.class));
    }

    @Test
    public void cancel_delegated() {
        rxGoldfinger.cancel();
        verify(goldfinger).cancel();
    }

    @Test
    public void decrypt_delegatedOnSubscribe() {
        Goldfinger.PromptParams params = params();
        rxGoldfinger.decrypt(params, KEY, VALUE).subscribe(observer);
        verify(goldfinger).decrypt(eq(params), eq(KEY), eq(VALUE), any(Goldfinger.Callback.class));
    }

    @Test
    public void decrypt_notDelegated() {
        Goldfinger.PromptParams params = params();
        rxGoldfinger.decrypt(params, KEY, VALUE);
        verify(goldfinger, never()).decrypt(eq(params), eq(KEY), eq(VALUE), any(Goldfinger.Callback.class));
    }

    @Test
    public void encrypt_delegatedOnSubscribe() {
        Goldfinger.PromptParams params = params();
        rxGoldfinger.encrypt(params, KEY, VALUE).subscribe(observer);
        verify(goldfinger).encrypt(eq(params), eq(KEY), eq(VALUE), any(Goldfinger.Callback.class));
    }

    @Test
    public void encrypt_notDelegated() {
        Goldfinger.PromptParams params = params();
        rxGoldfinger.encrypt(params, KEY, VALUE);
        verify(goldfinger, never()).encrypt(eq(params), eq(KEY), eq(VALUE), any(Goldfinger.Callback.class));
    }

    private Goldfinger.PromptParams params() {
        return new Goldfinger.PromptParams.Builder(activity).title("Title").negativeButtonText("Text").build();
    }
}
