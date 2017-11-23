package co.infinum.goldfinger

import javax.crypto.Cipher

internal enum class Mode(val cipherMode: Int) {
    ENCRYPTION(Cipher.ENCRYPT_MODE),
    AUTHENTICATE(Cipher.ENCRYPT_MODE),
    DECRYPTION(Cipher.DECRYPT_MODE)
}