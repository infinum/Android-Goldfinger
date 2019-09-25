package co.infinum.goldfinger;

/**
 * Thrown when value decryption fails.
 *
 * @see BiometricCallback#onAuthenticationSucceeded
 */
class DecryptionException extends Exception {

    DecryptionException() {
        super("Goldfinger failed to decrypt your data.");
    }
}
