package co.infinum.goldfinger;

class InitializationException extends Exception {

    InitializationException() {
        super("Goldfinger failed to initialize.");
    }
}
