package co.infinum.goldfinger;

public enum Warning {
    /**
     * The image acquired was good.
     */
    GOOD,
    /**
     * Only a partial fingerprint image was detected.
     */
    PARTIAL,
    /**
     * The fingerprint image was too noisy to process due to a detected condition.
     */
    INSUFFICIENT,
    /**
     * The fingerprint image was too noisy due to suspected or detected dirt on the sensor.
     */
    DIRTY,
    /**
     * The fingerprint image was unreadable due to lack of motion.
     */
    TOO_SLOW,
    /**
     * The fingerprint image was incomplete due to quick motion.
     */
    TOO_FAST,
    /**
     * Fingerprint valid but not recognized.
     */
    FAILURE;

    static Warning fromId(int id) {
        switch (id) {
            case 0:
                return GOOD;
            case 1:
                return PARTIAL;
            case 2:
                return INSUFFICIENT;
            case 3:
                return DIRTY;
            case 4:
                return TOO_SLOW;
            case 5:
                return TOO_FAST;
            default:
                return FAILURE;
        }
    }
}
