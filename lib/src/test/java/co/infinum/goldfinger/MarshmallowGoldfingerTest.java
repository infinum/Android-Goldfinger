package co.infinum.goldfinger;

import android.content.Context;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.test.mock.MockContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MarshmallowGoldfingerTest {

    private Context context = new MockContext();
    @Mock private CryptoCreator cryptoCreator;
    @Mock private FingerprintManagerCompat.CryptoObject cryptoObject;
    @Mock private Crypto crypto;
    @Mock private Goldfinger.Callback callback;
    private Goldfinger goldfinger;

    @Before
    public void init() {
        goldfinger = new MarshmallowGoldfinger(context, cryptoCreator, crypto);
    }

    @Test
    public void decrypt_started() {
        when(cryptoCreator.createDecryptionCryptoObject("keyName")).thenReturn(cryptoObject);
        goldfinger.decrypt("keyName", "value", callback);
        verify(cryptoCreator).createDecryptionCryptoObject("keyName");
        verify(callback, never()).onError(Error.UNKNOWN);
    }

    @Test
    public void encrypt_started() {
        when(cryptoCreator.createEncryptionCryptoObject("keyName")).thenReturn(cryptoObject);
        goldfinger.encrypt("keyName", "value", callback);
        verify(cryptoCreator).createEncryptionCryptoObject("keyName");
        verify(callback, never()).onError(Error.UNKNOWN);
    }

    @Test
    public void authenticate_started() {
        when(cryptoCreator.createAuthenticationCryptoObject(anyString())).thenReturn(cryptoObject);
        goldfinger.authenticate(callback);
        verify(cryptoCreator).createAuthenticationCryptoObject(anyString());
        verify(callback, never()).onError(Error.UNKNOWN);
    }

    @Test
    public void cryptoObjectNull() {
        when(cryptoCreator.createDecryptionCryptoObject("keyName")).thenReturn(null);
        goldfinger.decrypt("keyName", "value", callback);
        verify(cryptoCreator).createDecryptionCryptoObject("keyName");
        verify(callback).onError(Error.CRYPTO_OBJECT_INIT);
    }
}
