package co.infinum.goldfinger;

class DecryptionException extends Exception {

    DecryptionException() {
        super("Goldfinger failed to decrypt your data.");
    }
}
