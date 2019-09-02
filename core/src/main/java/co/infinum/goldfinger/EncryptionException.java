package co.infinum.goldfinger;

/**
 * Thrown when value encryption fails.
 *
 * @see CancellableAuthenticationCallback
 */
class EncryptionException extends Exception {

    EncryptionException() {
        super("Goldfinger failed to encrypt your data.");
    }
}

