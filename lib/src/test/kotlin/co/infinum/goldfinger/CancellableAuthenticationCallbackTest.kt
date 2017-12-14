package co.infinum.goldfinger

import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
import android.support.v4.os.CancellationSignal
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.security.Signature

@RunWith(MockitoJUnitRunner::class)
class CancellableAuthenticationCallbackTest {

    @Mock lateinit var crypto: Crypto
    @Mock lateinit var callback: Goldfinger.Callback
    @Mock lateinit var cryptoObject: FingerprintManagerCompat.CryptoObject
    private lateinit var cancellableAuthCallback: CancellableAuthenticationCallback

    @Test
    fun onAuthenticationError_delegated() {
        cancellableAuthCallback = CancellableAuthenticationCallback("", Mode.ENCRYPTION, crypto, callback)
        cancellableAuthCallback.onAuthenticationError(0, "")
        verify(callback).onError(Error.UNKNOWN)
    }

    @Test
    fun onAuthenticationError_canceled() {
        cancellableAuthCallback = CancellableAuthenticationCallback("", Mode.ENCRYPTION, crypto, callback)
        cancellableAuthCallback.cancellationSignal.cancel()
        cancellableAuthCallback.onAuthenticationError(0, "")
        verify(callback, never()).onError(Error.UNKNOWN)
    }

    @Test
    fun onAuthenticationSucceeded_encryptionOk() {
        cancellableAuthCallback = CancellableAuthenticationCallback("", Mode.ENCRYPTION, crypto, callback)
        `when`(crypto.encrypt(cryptoObject, "")).thenReturn("")
        cancellableAuthCallback.onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult(cryptoObject))
        verify(crypto).encrypt(cryptoObject, "")
        verify(callback).onSuccess("")
    }

    @Test
    fun onAuthenticationSucceeded_encryptionFailed() {
        cancellableAuthCallback = CancellableAuthenticationCallback("", Mode.ENCRYPTION, crypto, callback)
        `when`(crypto.encrypt(cryptoObject, "")).thenReturn(null)
        cancellableAuthCallback.onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult(cryptoObject))
        verify(crypto).encrypt(cryptoObject, "")
        verify(callback).onError(Error.ENCRYPTION_FAILED)
    }

    @Test
    fun onAuthenticationSucceeded_decryptionOk() {
        cancellableAuthCallback = CancellableAuthenticationCallback("", Mode.DECRYPTION, crypto, callback)
        `when`(crypto.decrypt(cryptoObject, "")).thenReturn("")
        cancellableAuthCallback.onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult(cryptoObject))
        verify(crypto).decrypt(cryptoObject, "")
        verify(callback).onSuccess("")
    }

    @Test
    fun onAuthenticationSucceeded_decryptionFailed() {
        cancellableAuthCallback = CancellableAuthenticationCallback("", Mode.DECRYPTION, crypto, callback)
        `when`(crypto.decrypt(cryptoObject, "")).thenReturn(null)
        cancellableAuthCallback.onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult(cryptoObject))
        verify(crypto).decrypt(cryptoObject, "")
        verify(callback).onError(Error.DECRYPTION_FAILED)
    }

    @Test
    fun onAuthenticationSucceeded_authenticated() {
        cancellableAuthCallback = CancellableAuthenticationCallback("", Mode.AUTHENTICATE, crypto, callback)
        cancellableAuthCallback.onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult(cryptoObject))
        verify(callback).onSuccess("")
    }

    @Test
    fun onAuthenticationSucceeded_canceled() {
        cancellableAuthCallback = CancellableAuthenticationCallback("", Mode.AUTHENTICATE, crypto, callback)
        cancellableAuthCallback.cancellationSignal.cancel()
        cancellableAuthCallback.onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult(cryptoObject))
        verify(callback, never()).onSuccess("")
    }

    @Test
    fun onAuthenticationHelp_delegated() {
        cancellableAuthCallback = CancellableAuthenticationCallback("", Mode.AUTHENTICATE, crypto, callback)
        cancellableAuthCallback.onAuthenticationHelp(-1, "")
        verify(callback).onWarning(Warning.FAILURE)
    }

    @Test
    fun onAuthenticationHelp_canceled() {
        cancellableAuthCallback = CancellableAuthenticationCallback("", Mode.AUTHENTICATE, crypto, callback)
        cancellableAuthCallback.cancellationSignal.cancel()
        cancellableAuthCallback.onAuthenticationHelp(0, "")
        verify(callback, never()).onWarning(Warning.FAILURE)
    }

    @Test
    fun onAuthenticationFailed_delegated() {
        cancellableAuthCallback = CancellableAuthenticationCallback("", Mode.AUTHENTICATE, crypto, callback)
        cancellableAuthCallback.onAuthenticationFailed()
        verify(callback).onWarning(Warning.FAILURE)
    }

    @Test
    fun onAuthenticationFailed_canceled() {
        cancellableAuthCallback = CancellableAuthenticationCallback("", Mode.AUTHENTICATE, crypto, callback)
        cancellableAuthCallback.cancellationSignal.cancel()
        cancellableAuthCallback.onAuthenticationFailed()
        verify(callback, never()).onWarning(Warning.FAILURE)
    }

    @Test
    fun cancel_delegated() {
        cancellableAuthCallback = CancellableAuthenticationCallback("", Mode.AUTHENTICATE, crypto, callback)
        cancellableAuthCallback.cancel()
        assertEquals(true, cancellableAuthCallback.cancellationSignal.isCanceled)
    }

    @Test
    fun cancel_canceled() {
        cancellableAuthCallback = CancellableAuthenticationCallback("", Mode.AUTHENTICATE, crypto, callback)
        cancellableAuthCallback.cancellationSignal.cancel()
        cancellableAuthCallback.cancel()
        assertEquals(true, cancellableAuthCallback.cancellationSignal.isCanceled)
    }
}
