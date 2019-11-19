package co.infinum.goldfinger;

/**
 * Internal enum used to differentiate Fingerprint authentication modes.
 * Authentication does not have to work with cipher, while both
 * Decryption and Encryption should.
 * <p>
 * Contains cipherMode parameter that is used on Goldfinger initialization.
 */
enum Mode {
    AUTHENTICATION, DECRYPTION, ENCRYPTION
}
