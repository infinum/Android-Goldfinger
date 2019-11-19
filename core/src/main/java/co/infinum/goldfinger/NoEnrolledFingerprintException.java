package co.infinum.goldfinger;

/**
 * Thrown if the user has no enrolled fingerprints.
 */
@SuppressWarnings("WeakerAccess")
public class NoEnrolledFingerprintException extends Exception {

    NoEnrolledFingerprintException() {
        super("User has no enrolled fingerprint.");
    }
}
