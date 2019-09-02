package co.infinum.goldfinger;

/**
 * Thrown if the user has no enrolled fingerprints.
 */
class NoEnrolledFingerprintsException extends Exception {

    NoEnrolledFingerprintsException() {
        super("User has no enrolled fingerprints.");
    }
}
