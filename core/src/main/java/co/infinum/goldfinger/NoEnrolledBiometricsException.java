package co.infinum.goldfinger;

/**
 * Thrown if the user has no enrolled biometrics.
 */
@SuppressWarnings("WeakerAccess")
public class NoEnrolledBiometricsException extends Exception {

    NoEnrolledBiometricsException() {
        super("User has no enrolled biometrics.");
    }
}
