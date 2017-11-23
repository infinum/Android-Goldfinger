package co.infinum.goldfinger

import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
import android.test.mock.MockContext
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MarshmallowGoldfingerTest {

    private var context = MockContext()
    @Mock private lateinit var cryptoCreator: CryptoCreator
    @Mock private lateinit var cryptoObject: FingerprintManagerCompat.CryptoObject
    @Mock private lateinit var crypto: Crypto
    @Mock private lateinit var logger: Logger
    @Mock private lateinit var callback: Goldfinger.Callback
    private lateinit var goldfinger: Goldfinger

    @Before
    fun init() {
        goldfinger = MarshmallowGoldfinger(context, cryptoCreator, crypto, logger)
    }

    @Test
    fun decrypt_started() {
        `when`(cryptoCreator.createDecryptionCryptoObject("keyName")).thenReturn(cryptoObject)
        goldfinger.decrypt("keyName", "value", callback)
        verify(cryptoCreator).createDecryptionCryptoObject("keyName")
        verify(callback, never()).onError(Error.UNKNOWN)
    }

    @Test
    fun encrypt_started() {
        `when`(cryptoCreator.createEncryptionCryptoObject("keyName")).thenReturn(cryptoObject)
        goldfinger.encrypt("keyName", "value", callback)
        verify(cryptoCreator).createEncryptionCryptoObject("keyName")
        verify(callback, never()).onError(Error.UNKNOWN)
    }

    @Test
    fun authenticate_started() {
        `when`(cryptoCreator.createAuthenticationCryptoObject(anyString())).thenReturn(cryptoObject)
        goldfinger.authenticate(callback)
        verify(cryptoCreator).createAuthenticationCryptoObject(anyString())
        verify(callback, never()).onError(Error.UNKNOWN)
    }

    @Test
    fun cryptoObjectNull() {
        `when`(cryptoCreator.createDecryptionCryptoObject("keyName")).thenReturn(null)
        goldfinger.decrypt("keyName", "value", callback)
        verify(cryptoCreator).createDecryptionCryptoObject("keyName")
        verify(callback).onError(Error.CRYPTO_OBJECT_INITIALIZATION)
    }
}