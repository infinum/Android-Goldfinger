package co.infinum.goldfinger;

import javax.crypto.Cipher;

/**
 * Internal enum used to differentiate Fingerprint authentication modes.
 * Authentication does not have to work with cipher, while both
 * Decryption and Encryption should.
 * <p>
 * Contains cipherMode parameter that is used on Goldfinger initialization.
 */
enum Mode {
    AUTHENTICATION(69),
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
