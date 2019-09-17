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

    @Mock private Goldfinger goldfinger;
    @Mock private FragmentActivity activity;
    private DisposableObserver<Goldfinger.Result> observer = new TestObserver();
    @InjectMocks private RxGoldfingerImpl rxGoldfinger;

    @Test
    public void authenticate_delegatedOnSubscribe() {
        Goldfinger.Params params = params();
        rxGoldfinger.authenticate(params).subscribe(observer);
        verify(goldfinger).authenticate(eq(params), any(Goldfinger.Callback.class));
    }

    @Test
    public void authenticate_notDelegated() {
        Goldfinger.Params params = params();
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
        Goldfinger.Params params = decryptParams();
        rxGoldfinger.authenticate(params).subscribe(observer);
        verify(goldfinger).authenticate(eq(params), any(Goldfinger.Callback.class));
    }

    @Test
    public void decrypt_notDelegated() {
        Goldfinger.Params params = decryptParams();
        rxGoldfinger.authenticate(params);
        verify(goldfinger, never()).authenticate(eq(params), any(Goldfinger.Callback.class));
    }

    @Test
    public void encrypt_delegatedOnSubscribe() {
        Goldfinger.Params params = encryptParams();
        rxGoldfinger.authenticate(params).subscribe(observer);
        verify(goldfinger).authenticate(eq(params), any(Goldfinger.Callback.class));
    }

    @Test
    public void encrypt_notDelegated() {
        Goldfinger.Params params = encryptParams();
        rxGoldfinger.authenticate(params);
        verify(goldfinger, never()).authenticate(eq(params), any(Goldfinger.Callback.class));
    }

    private Goldfinger.Params decryptParams() {
        return new Goldfinger.Params.Builder(activity).title("Title").negativeButtonText("Text").decrypt("key", "value").build();
    }

    private Goldfinger.Params encryptParams() {
        return new Goldfinger.Params.Builder(activity).title("Title").negativeButtonText("Text").encrypt("key", "value").build();
    }

    private Goldfinger.Params params() {
        return new Goldfinger.Params.Builder(activity).title("Title").negativeButtonText("Text").build();
    }
}
