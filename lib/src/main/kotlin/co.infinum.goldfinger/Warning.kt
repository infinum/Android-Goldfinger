package co.infinum.goldfinger

enum class Warning {
    /** The image acquired was good. */
    GOOD,
    /** Only a partial fingerprint image was detected. */
    PARTIAL,
    /** The fingerprint image was too noisy to process due to a detected condition. */
    INSUFFICIENT,
    /** The fingerprint image was too noisy due to suspected or detected dirt on the sensor. */
    DIRTY,
    /** The fingerprint image was unreadable due to lack of motion. */
    TOO_SLOW,
    /** The fingerprint image was incomplete due to quick motion. */
    TOO_FAST,
    /** Fingerprint valid but not recognized. */
    FAILURE;

    companion object {
        internal fun fromId(id: Int): Warning {
            return when (id) {
                0 -> GOOD
                1 -> PARTIAL
                2 -> INSUFFICIENT
                3 -> DIRTY
                4 -> TOO_SLOW
                5 -> TOO_FAST
                else -> FAILURE
            }
        }
    }
}