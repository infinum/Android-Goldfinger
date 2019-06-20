package co.infinum.goldfinger;

class NoEnrolledFingerprintsException extends Exception {

    NoEnrolledFingerprintsException() {
        super("User has no enrolled fingerprints.");
    }
}
