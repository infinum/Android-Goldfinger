package co.infinum.goldfinger

import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
import android.util.Base64
import javax.crypto.Cipher

interface Crypto {

    /**
     * Encrypt value with unlocked cryptoObject. Return null if encryption fails.
     */
    fun encrypt(cryptoObject: FingerprintManagerCompat.CryptoObject, value: String): String?

    /**
     * Decrypt value with unlocked cryptoObject. Return null if decryption fails.
     */
    fun decrypt(cryptoObject: FingerprintManagerCompat.CryptoObject, value: String): String?

    class Default(private val logger: Logger) : Crypto {

        override fun encrypt(cryptoObject: FingerprintManagerCompat.CryptoObject, value: String): String? {
            return try {
                val cipher = cryptoObject.cipher as Cipher
                val encryptedBytes = cipher.doFinal(value.toByteArray())
                Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
            } catch (e: Exception) {
                logger.log(e)
                null
            }
        }

        override fun decrypt(cryptoObject: FingerprintManagerCompat.CryptoObject, value: String): String? {
            return try {
                val cipher = cryptoObject.cipher as Cipher
                val decryptedBytes = cipher.doFinal(Base64.decode(value, Base64.DEFAULT))
                String(decryptedBytes)
            } catch (e: Exception) {
                logger.log(e)
                null
            }
        }
    }
}