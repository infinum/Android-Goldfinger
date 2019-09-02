package co.infinum.goldfinger;

/**
 * Thrown when value encryption fails.
 *
 * @see AuthenticationCallback
 */
class EncryptionException extends Exception {

    EncryptionException() {
        super("Goldfinger failed to encrypt your data.");
    }
}

