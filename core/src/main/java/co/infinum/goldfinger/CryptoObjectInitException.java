package co.infinum.goldfinger;

/**
 * Thrown when value CryptoObject initialization fails.
 * Usually because {@link android.security.keystore.KeyPermanentlyInvalidatedException} is thrown.
 *
 * Also be aware of https://issuetracker.google.com/issues/65578763
 */
class CryptoObjectInitException extends Exception {

    CryptoObjectInitException() {
        super("CryptoObject failed to create.");
    }
}
