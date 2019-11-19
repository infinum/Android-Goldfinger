package co.infinum.goldfinger;

/**
 * Thrown when value encryption fails.
 *
 * @see BiometricCallback#onAuthenticationSucceeded
 */
@SuppressWarnings("WeakerAccess")
public class EncryptionException extends Exception {

    EncryptionException() {
        super("Goldfinger failed to encrypt your data.");
    }
}

