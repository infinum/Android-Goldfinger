package co.infinum.goldfinger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import androidx.biometric.BiometricPrompt;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CancellableAuthenticationCallbackTest {

    @Mock private GoldfingerCallback callback;
    @Mock private BiometricPrompt.CryptoObject cryptoObject;
    @Mock private CryptographyHandler cryptographyHandler;
    private InternalCallback internalCallback;

    @Test
    public void cancel_canceled() {
        cancellableCallback = newInstance(Mode.AUTHENTICATION);
        cancellableCallback.cancellationSignal.cancel();
        cancellableCallback.cancel();
        assertEquals(true, cancellableCallback.cancellationSignal.isCanceled());
    }

    @Test
    public void cancel_delegated() {
        cancellableCallback = newInstance(Mode.AUTHENTICATION);
        cancellableCallback.cancel();
        assertEquals(true, cancellableCallback.cancellationSignal.isCanceled());
    }

    @Test
    public void onAuthenticationError_cancelDelegated() {
        when(clock.isBeforeNow(anyLong())).thenReturn(true);
        cancellableCallback = newInstance(Mode.ENCRYPTION);
        cancellableCallback.onAuthenticationError(5, "");
        verify(callback).onError(Error.CANCELED);
    }

    @Test
    public void onAuthenticationError_cancelIgnored() {
        when(clock.isBeforeNow(anyLong())).thenReturn(false);
        cancellableCallback = newInstance(Mode.ENCRYPTION);
        cancellableCallback.onAuthenticationError(5, "");
        verify(callback, never()).onError(Error.CANCELED);
    }

    @Test
    public void onAuthenticationError_canceled() {
        cancellableCallback = newInstance(Mode.ENCRYPTION);
        cancellableCallback.cancellationSignal.cancel();
        cancellableCallback.onAuthenticationError(0, "");
        verify(callback, never()).onError(Error.UNKNOWN);
    }

    @Test
    public void onAuthenticationError_delegated() {
        cancellableCallback = newInstance(Mode.ENCRYPTION);
        cancellableCallback.onAuthenticationError(0, "");
        verify(callback).onError(Error.UNKNOWN);
    }

    @Test
    public void onAuthenticationFailed_canceled() {
        cancellableCallback = newInstance(Mode.AUTHENTICATION);
        cancellableCallback.cancellationSignal.cancel();
        cancellableCallback.onAuthenticationFailed();
        verify(callback, never()).onError(Error.FAILURE);
    }

    @Test
    public void onAuthenticationFailed_delegated() {
        cancellableCallback = newInstance(Mode.AUTHENTICATION);
        cancellableCallback.onAuthenticationFailed();
        verify(callback).onError(Error.FAILURE);
    }

    @Test
    public void onAuthenticationHelp_canceled() {
        cancellableCallback = newInstance(Mode.AUTHENTICATION);
        cancellableCallback.cancellationSignal.cancel();
        cancellableCallback.onAuthenticationHelp(0, "");
        verify(callback, never()).onError(Error.FAILURE);
    }

    @Test
    public void onAuthenticationHelp_delegated() {
        cancellableCallback = newInstance(Mode.AUTHENTICATION);
        cancellableCallback.onAuthenticationHelp(-1, "");
        verify(callback).onError(Error.FAILURE);
    }

    @Test
    public void onAuthenticationSucceeded_authenticated() {
        cancellableCallback = newInstance(Mode.AUTHENTICATION);
        cancellableCallback.onAuthenticationSucceeded(new FingerprintManagerCompat.AuthenticationResult(cryptoObject));
        verify(callback).onSuccess("");
    }

    @Test
    public void onAuthenticationSucceeded_canceled() {
        cancellableCallback = newInstance(Mode.AUTHENTICATION);
        cancellableCallback.cancellationSignal.cancel();
        cancellableCallback.onAuthenticationSucceeded(new FingerprintManagerCompat.AuthenticationResult(cryptoObject));
        verify(callback, never()).onSuccess("");
    }

    @Test
    public void onAuthenticationSucceeded_decryptionFailed() {
        cancellableCallback = newInstance(Mode.DECRYPTION);
        when(crypto.decrypt(cryptoObject, "")).thenReturn(null);
        cancellableCallback.onAuthenticationSucceeded(new FingerprintManagerCompat.AuthenticationResult(cryptoObject));
        verify(crypto).decrypt(cryptoObject, "");
        verify(callback).onError(Error.DECRYPTION_FAILED);
    }

    @Test
    public void onAuthenticationSucceeded_decryptionOk() {
        cancellableCallback = newInstance(Mode.DECRYPTION);
        when(crypto.decrypt(cryptoObject, "")).thenReturn("");
        cancellableCallback.onAuthenticationSucceeded(new FingerprintManagerCompat.AuthenticationResult(cryptoObject));
        verify(crypto).decrypt(cryptoObject, "");
        verify(callback).onSuccess("");
    }

    @Test
    public void onAuthenticationSucceeded_encryptionFailed() {
        cancellableCallback = newInstance(Mode.ENCRYPTION);
        when(crypto.encrypt(cryptoObject, "")).thenReturn(null);
        cancellableCallback.onAuthenticationSucceeded(new FingerprintManagerCompat.AuthenticationResult(cryptoObject));
        verify(crypto).encrypt(cryptoObject, "");
        verify(callback).onError(Error.ENCRYPTION_FAILED);
    }

    @Test
    public void onAuthenticationSucceeded_encryptionOk() {
        cancellableCallback = newInstance(Mode.ENCRYPTION);
        when(crypto.encrypt(cryptoObject, "")).thenReturn("");
        cancellableCallback.onAuthenticationSucceeded(new FingerprintManagerCompat.AuthenticationResult(cryptoObject));
        verify(crypto).encrypt(cryptoObject, "");
        verify(callback).onSuccess("");
    }

    private InternalCallback newInstance(Mode mode) {
        return new InternalCallback(cryptographyHandler, mode, new CryptographyData("", ""), callback);
    }
}

