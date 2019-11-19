package co.infinum.goldfinger;

/**
 * Thrown when value decryption fails.
 *
 * @see BiometricCallback#onAuthenticationSucceeded
 */
@SuppressWarnings("WeakerAccess")
public class DecryptionException extends Exception {

    DecryptionException() {
        super("Goldfinger failed to decrypt your data.");
    }
}
