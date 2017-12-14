package co.infinum.goldfinger

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.support.annotation.RequiresApi
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
import android.util.Base64
import java.security.Key
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec

interface CryptoCreator {

    /**
     * Create CryptoObject for authentication call. Return null if invalid.
     */
    fun createAuthenticationCryptoObject(keyName: String): FingerprintManagerCompat.CryptoObject?

    /**
     * Create CryptoObject for encryption call. Return null if invalid.
     */
    fun createEncryptionCryptoObject(keyName: String): FingerprintManagerCompat.CryptoObject?

    /**
     * Create CryptoObject for decryption call. Return null if invalid.
     */
    fun createDecryptionCryptoObject(keyName: String): FingerprintManagerCompat.CryptoObject?

    @RequiresApi(Build.VERSION_CODES.M)
    class Default(context: Context, private val logger: Logger) : CryptoCreator {

        private var keyStore: KeyStore? = null
        private var keyGenerator: KeyGenerator? = null
        private val sharedPrefs = context.getSharedPreferences("<<Goldfinger IV storage>>", Context.MODE_PRIVATE)

        init {
            try {
                keyStore = KeyStore.getInstance("AndroidKeyStore")
                keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            } catch (e: Exception) {
                logger.log(e)
            }
        }

        override fun createAuthenticationCryptoObject(keyName: String) = createCryptoObject(keyName, Mode.AUTHENTICATE)
        override fun createEncryptionCryptoObject(keyName: String) = createCryptoObject(keyName, Mode.ENCRYPTION)
        override fun createDecryptionCryptoObject(keyName: String) = createCryptoObject(keyName, Mode.DECRYPTION)

        private fun createCryptoObject(keyName: String, mode: Mode): FingerprintManagerCompat.CryptoObject? {
            if (keyStore == null || keyGenerator == null) {
                return null
            }

            return try {
                val key = if (mode == Mode.DECRYPTION) loadKey(keyName) else createKey(keyName)
                val cipher = createCipher(keyName, key, mode)
                FingerprintManagerCompat.CryptoObject(cipher as Cipher)
            } catch (e: Exception) {
                logger.log(e)
                null
            }
        }

        private fun createKey(keyName: String): Key {
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(keyName, KeyProperties.PURPOSE_DECRYPT or KeyProperties.PURPOSE_ENCRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .setUserAuthenticationRequired(true)
                    .setInvalidatedByBiometricEnrollmentCompat(true)
                    .build()
            keyGenerator!!.init(keyGenParameterSpec)
            keyGenerator!!.generateKey()
            return loadKey(keyName)
        }

        private fun loadKey(keyName: String): Key {
            keyStore!!.load(null)
            return keyStore!!.getKey(keyName, null)
        }

        private fun createCipher(keyName: String, key: Key, mode: Mode): Cipher? {
            val transformation = "${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_CBC}/${KeyProperties.ENCRYPTION_PADDING_PKCS7}"
            val cipher = Cipher.getInstance(transformation)
            if (mode == Mode.DECRYPTION) {
                val iv = loadIv(keyName)
                cipher.init(mode.cipherMode, key, IvParameterSpec(iv))
            } else {
                cipher.init(mode.cipherMode, key)
                saveIv(keyName, cipher.iv)
            }
            return cipher
        }

        private fun loadIv(keyName: String) = Base64.decode(sharedPrefs.getString(keyName, ""), Base64.DEFAULT)
        private fun saveIv(keyName: String, iv: ByteArray) = sharedPrefs.edit().putString(keyName, Base64.encodeToString(iv, Base64.DEFAULT)).apply()

        private fun KeyGenParameterSpec.Builder.setInvalidatedByBiometricEnrollmentCompat(invalidated: Boolean): KeyGenParameterSpec.Builder {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                setInvalidatedByBiometricEnrollment(invalidated)
            }
            return this
        }
    }
}