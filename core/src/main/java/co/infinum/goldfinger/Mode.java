package co.infinum.goldfinger;

import javax.crypto.Cipher;

enum Mode {
    AUTHENTICATION(-1),
    DECRYPTION(Cipher.DECRYPT_MODE),
    ENCRYPTION(Cipher.ENCRYPT_MODE);

    private final int cipherMode;

    Mode(int cipherMode) {
        this.cipherMode = cipherMode;
    }

    public int cipherMode() {
        return cipherMode;
    }
}
