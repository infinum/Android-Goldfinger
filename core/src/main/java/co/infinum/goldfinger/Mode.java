package co.infinum.goldfinger;

import javax.crypto.Cipher;

enum Mode {
    AUTHENTICATION(Cipher.ENCRYPT_MODE),
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
