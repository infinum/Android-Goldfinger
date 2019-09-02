package co.infinum.goldfinger;

/**
 * Thrown when value decryption fails.
 *
 * @see AuthenticationCallback
 */
class DecryptionException extends Exception {

    DecryptionException() {
        super("Goldfinger failed to decrypt your data.");
    }
}
