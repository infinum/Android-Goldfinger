package co.infinum.goldfinger;

import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CancellableAuthenticationCallbackTest {

    @Mock Crypto crypto;
    @Mock Logger logger;
    @Mock Goldfinger.Callback callback;
    @Mock FingerprintManagerCompat.CryptoObject cryptoObject;
    private CancellableAuthenticationCallback cancellableCallback;

    @Test
    public void onAuthenticationError_delegated() {
        cancellableCallback = new CancellableAuthenticationCallback(crypto, logger, Mode.ENCRYPTION, "", callback);
        cancellableCallback.onAuthenticationError(0, "");
        verify(callback).onError(Error.UNKNOWN);
    }

    @Test
    public void onAuthenticationError_canceled() {
        cancellableCallback = new CancellableAuthenticationCallback(crypto, logger, Mode.ENCRYPTION, "", callback);
        cancellableCallback.cancellationSignal.cancel();
        cancellableCallback.onAuthenticationError(0, "");
        verify(callback, never()).onError(Error.UNKNOWN);
    }

    @Test
    public void onAuthenticationSucceeded_encryptionOk() {
        cancellableCallback = new CancellableAuthenticationCallback(crypto, logger, Mode.ENCRYPTION, "", callback);
        when(crypto.encrypt(cryptoObject, "")).thenReturn("");
        cancellableCallback.onAuthenticationSucceeded(new FingerprintManagerCompat.AuthenticationResult(cryptoObject));
        verify(crypto).encrypt(cryptoObject, "");
        verify(callback).onSuccess("");
    }

    @Test
    public void onAuthenticationSucceeded_encryptionFailed() {
        cancellableCallback = new CancellableAuthenticationCallback(crypto, logger, Mode.ENCRYPTION, "", callback);
        when(crypto.encrypt(cryptoObject, "")).thenReturn(null);
        cancellableCallback.onAuthenticationSucceeded(new FingerprintManagerCompat.AuthenticationResult(cryptoObject));
        verify(crypto).encrypt(cryptoObject, "");
        verify(callback).onError(Error.ENCRYPTION_FAILED);
    }

    @Test
    public void onAuthenticationSucceeded_decryptionOk() {
        cancellableCallback = new CancellableAuthenticationCallback(crypto, logger, Mode.DECRYPTION, "", callback);
        when(crypto.decrypt(cryptoObject, "")).thenReturn("");
        cancellableCallback.onAuthenticationSucceeded(new FingerprintManagerCompat.AuthenticationResult(cryptoObject));
        verify(crypto).decrypt(cryptoObject, "");
        verify(callback).onSuccess("");
    }

    @Test
    public void onAuthenticationSucceeded_decryptionFailed() {
        cancellableCallback = new CancellableAuthenticationCallback(crypto, logger, Mode.DECRYPTION, "", callback);
        when(crypto.decrypt(cryptoObject, "")).thenReturn(null);
        cancellableCallback.onAuthenticationSucceeded(new FingerprintManagerCompat.AuthenticationResult(cryptoObject));
        verify(crypto).decrypt(cryptoObject, "");
        verify(callback).onError(Error.DECRYPTION_FAILED);
    }

    @Test
    public void onAuthenticationSucceeded_authenticated() {
        cancellableCallback = new CancellableAuthenticationCallback(crypto, logger, Mode.AUTHENTICATION, "", callback);
        cancellableCallback.onAuthenticationSucceeded(new FingerprintManagerCompat.AuthenticationResult(cryptoObject));
        verify(callback).onSuccess("");
    }

    @Test
    public void onAuthenticationSucceeded_canceled() {
        cancellableCallback = new CancellableAuthenticationCallback(crypto, logger, Mode.AUTHENTICATION, "", callback);
        cancellableCallback.cancellationSignal.cancel();
        cancellableCallback.onAuthenticationSucceeded(new FingerprintManagerCompat.AuthenticationResult(cryptoObject));
        verify(callback, never()).onSuccess("");
    }

    @Test
    public void onAuthenticationHelp_delegated() {
        cancellableCallback = new CancellableAuthenticationCallback(crypto, logger, Mode.AUTHENTICATION, "", callback);
        cancellableCallback.onAuthenticationHelp(-1, "");
        verify(callback).onWarning(Warning.FAILURE);
    }

    @Test
    public void onAuthenticationHelp_canceled() {
        cancellableCallback = new CancellableAuthenticationCallback(crypto, logger, Mode.AUTHENTICATION, "", callback);
        cancellableCallback.cancellationSignal.cancel();
        cancellableCallback.onAuthenticationHelp(0, "");
        verify(callback, never()).onWarning(Warning.FAILURE);
    }

    @Test
    public void onAuthenticationFailed_delegated() {
        cancellableCallback = new CancellableAuthenticationCallback(crypto, logger, Mode.AUTHENTICATION, "", callback);
        cancellableCallback.onAuthenticationFailed();
        verify(callback).onWarning(Warning.FAILURE);
    }

    @Test
    public void onAuthenticationFailed_canceled() {
        cancellableCallback = new CancellableAuthenticationCallback(crypto, logger, Mode.AUTHENTICATION, "", callback);
        cancellableCallback.cancellationSignal.cancel();
        cancellableCallback.onAuthenticationFailed();
        verify(callback, never()).onWarning(Warning.FAILURE);
    }

    @Test
    public void cancel_delegated() {
        cancellableCallback = new CancellableAuthenticationCallback(crypto, logger, Mode.AUTHENTICATION, "", callback);
        cancellableCallback.cancel();
        assertEquals(true, cancellableCallback.cancellationSignal.isCanceled());
    }

    @Test
    public void cancel_canceled() {
        cancellableCallback = new CancellableAuthenticationCallback(crypto, logger, Mode.AUTHENTICATION, "", callback);
        cancellableCallback.cancellationSignal.cancel();
        cancellableCallback.cancel();
        assertEquals(true, cancellableCallback.cancellationSignal.isCanceled());
    }
}

