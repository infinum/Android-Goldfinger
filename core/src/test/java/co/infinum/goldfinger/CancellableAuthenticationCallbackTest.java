package co.infinum.goldfinger;

import android.hardware.fingerprint.FingerprintManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import androidx.annotation.NonNull;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CancellableAuthenticationCallbackTest {

    private Goldfinger.Result result;
    private Exception exception;
    private Goldfinger.Callback callback = new Goldfinger.Callback() {
        @Override
        public void onError(@NonNull Exception e) {
            CancellableAuthenticationCallbackTest.this.exception = e;
        }

        @Override
        public void onResult(@NonNull Goldfinger.Result result) {
            CancellableAuthenticationCallbackTest.this.result = result;
        }
    };
    private CancellableAuthenticationCallback cancellableCallback;
    @Mock private Crypto crypto;
    @Mock private FingerprintManagerCompat.CryptoObject cryptoObject;

    @Test
    public void cancel_canceled() {
        cancellableCallback = newInstance(Mode.AUTHENTICATION);
        cancellableCallback.cancellationSignal.cancel();
        assertTrue(cancellableCallback.cancellationSignal.isCanceled());
    }

    @Test
    public void cancel_delegated() {
        cancellableCallback = newInstance(Mode.AUTHENTICATION);
        cancellableCallback.cancel();
        assertTrue(cancellableCallback.cancellationSignal.isCanceled());
    }

    @Before
    public void init() {
        this.exception = null;
        this.result = null;
    }

    @Test
    public void onAuthenticationError_cancelDelegated() {
        cancellableCallback = newInstance(Mode.ENCRYPTION);
        cancellableCallback.onAuthenticationError(-1, "");
        assertEquals(Goldfinger.Type.ERROR, result.type());
    }

    @Test
    public void onAuthenticationError_cancelIgnored() {
        cancellableCallback = newInstance(Mode.ENCRYPTION);
        cancellableCallback.cancel();
        cancellableCallback.onAuthenticationError(FingerprintManager.FINGERPRINT_ERROR_CANCELED, "");
        assertNull(result);
        assertNull(exception);
    }

    @Test
    public void onAuthenticationError_canceled() {
        cancellableCallback = newInstance(Mode.ENCRYPTION);
        cancellableCallback.cancel();
        cancellableCallback.onAuthenticationError(-1, "");
        assertNull(result);
        assertNull(exception);
    }

    @Test
    public void onAuthenticationError_delegated() {
        cancellableCallback = newInstance(Mode.ENCRYPTION);
        cancellableCallback.onAuthenticationError(-1, "");
        assertEquals(Goldfinger.Type.ERROR, result.type());
    }

    @Test
    public void onAuthenticationFailed_canceled() {
        cancellableCallback = newInstance(Mode.AUTHENTICATION);
        cancellableCallback.cancel();
        cancellableCallback.onAuthenticationFailed();
        assertNull(result);
        assertNull(exception);
    }

    @Test
    public void onAuthenticationFailed_delegated() {
        cancellableCallback = newInstance(Mode.AUTHENTICATION);
        cancellableCallback.onAuthenticationFailed();
        assertEquals(Goldfinger.Type.INFO, result.type());
        assertEquals(Goldfinger.Reason.AUTHENTICATION_FAIL, result.reason());
    }

    @Test
    public void onAuthenticationHelp_canceled() {
        cancellableCallback = newInstance(Mode.AUTHENTICATION);
        cancellableCallback.cancel();
        cancellableCallback.onAuthenticationHelp(-1, "");
        assertNull(result);
        assertNull(exception);
    }

    @Test
    public void onAuthenticationHelp_delegated() {
        cancellableCallback = newInstance(Mode.AUTHENTICATION);
        cancellableCallback.onAuthenticationHelp(-1, "");
        assertEquals(Goldfinger.Type.INFO, result.type());
    }

    @Test
    public void onAuthenticationSucceeded_authenticated() {
        cancellableCallback = newInstance(Mode.AUTHENTICATION);
        cancellableCallback.onAuthenticationSucceeded(new FingerprintManagerCompat.AuthenticationResult(cryptoObject));
        assertEquals(Goldfinger.Type.SUCCESS, result.type());
        assertEquals(Goldfinger.Reason.AUTHENTICATION_SUCCESS, result.reason());
    }

    @Test
    public void onAuthenticationSucceeded_canceled() {
        cancellableCallback = newInstance(Mode.AUTHENTICATION);
        cancellableCallback.cancel();
        cancellableCallback.onAuthenticationSucceeded(new FingerprintManagerCompat.AuthenticationResult(cryptoObject));
        assertNull(result);
        assertNull(exception);
    }

    @Test
    public void onAuthenticationSucceeded_decryptionFailed() {
        cancellableCallback = newInstance(Mode.DECRYPTION);
        when(crypto.decrypt(cryptoObject, "")).thenReturn(null);
        cancellableCallback.onAuthenticationSucceeded(new FingerprintManagerCompat.AuthenticationResult(cryptoObject));
        verify(crypto).decrypt(cryptoObject, "");
        assertTrue(exception instanceof DecryptionException);
    }

    @Test
    public void onAuthenticationSucceeded_decryptionOk() {
        cancellableCallback = newInstance(Mode.DECRYPTION);
        when(crypto.decrypt(cryptoObject, "")).thenReturn("test");
        cancellableCallback.onAuthenticationSucceeded(new FingerprintManagerCompat.AuthenticationResult(cryptoObject));
        verify(crypto).decrypt(cryptoObject, "");
        assertEquals(Goldfinger.Type.SUCCESS, result.type());
        assertEquals(Goldfinger.Reason.AUTHENTICATION_SUCCESS, result.reason());
        assertEquals("test", result.value());
    }

    @Test
    public void onAuthenticationSucceeded_encryptionFailed() {
        cancellableCallback = newInstance(Mode.ENCRYPTION);
        when(crypto.encrypt(cryptoObject, "")).thenReturn(null);
        cancellableCallback.onAuthenticationSucceeded(new FingerprintManagerCompat.AuthenticationResult(cryptoObject));
        verify(crypto).encrypt(cryptoObject, "");
        assertTrue(exception instanceof EncryptionException);
    }

    @Test
    public void onAuthenticationSucceeded_encryptionOk() {
        cancellableCallback = newInstance(Mode.ENCRYPTION);
        when(crypto.encrypt(cryptoObject, "")).thenReturn("test");
        cancellableCallback.onAuthenticationSucceeded(new FingerprintManagerCompat.AuthenticationResult(cryptoObject));
        verify(crypto).encrypt(cryptoObject, "");
        assertEquals(Goldfinger.Type.SUCCESS, result.type());
        assertEquals(Goldfinger.Reason.AUTHENTICATION_SUCCESS, result.reason());
        assertEquals("test", result.value());
    }

    private CancellableAuthenticationCallback newInstance(Mode mode) {
        return new CancellableAuthenticationCallback(crypto, mode, "", callback);
    }
}

