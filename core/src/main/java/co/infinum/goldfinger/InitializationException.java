package co.infinum.goldfinger;

/**
 * Thrown when value Goldfinger authentication initialization fails.
 * Usually because it fails to create CryptoObject.
 *
 * Also https://issuetracker.google.com/issues/65578763
 */
class InitializationException extends Exception {

    InitializationException() {
        super("Goldfinger failed to initialize.");
    }
}
