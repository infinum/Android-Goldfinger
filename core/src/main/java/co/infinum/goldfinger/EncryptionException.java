package co.infinum.goldfinger;

class EncryptionException extends Exception {

    EncryptionException() {
        super("Goldfinger failed to encrypt your data.");
    }
}

