package co.infinum.goldfinger

enum class Error {
    /** The hardware is unavailable. */
    UNAVAILABLE,
    /** Error state returned when the sensor was unable to process the current image. */
    UNABLE_TO_PROCESS,
    /** Error state returned when the current request has been running too long. */
    TIMEOUT,
    /** Error state returned for operations like enrollment; the operation cannot be completed because there's not enough storage remaining to complete the operation. */
    NOT_ENOUGH_SPACE,
    /** The operation was canceled because the fingerprint sensor is unavailable. */
    CANCELED,
    /** The operation was canceled because the API is locked out due to too many attempts. */
    LOCKOUT,
    /** CryptoCreator failed to initialize CryptoObject. */
    CRYPTO_OBJECT_INITIALIZATION,
    /** Crypto failed to decrypt the value. */
    DECRYPTION_FAILED,
    /** Crypto failed to encrypt the value. */
    ENCRYPTION_FAILED,
    /** Unknown error happened. */
    UNKNOWN;

    companion object {
        internal fun fromId(id: Int): Error {
            return when (id) {
                1 -> UNAVAILABLE
                2 -> UNABLE_TO_PROCESS
                3 -> TIMEOUT
                4 -> NOT_ENOUGH_SPACE
                5 -> CANCELED
                7 -> LOCKOUT
                else -> UNKNOWN
            }
        }
    }
}