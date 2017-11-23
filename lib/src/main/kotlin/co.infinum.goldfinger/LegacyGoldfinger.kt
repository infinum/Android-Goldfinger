package co.infinum.goldfinger

/**
 * Legacy implementation for pre-Marshmallow devices.
 */
internal class LegacyGoldfinger : Goldfinger {
    override fun authenticate(callback: Goldfinger.Callback) {}
    override fun decrypt(keyName: String, value: String, callback: Goldfinger.Callback) {}
    override fun encrypt(keyName: String, value: String, callback: Goldfinger.Callback) {}
    override fun hasFingerprintHardware() = false
    override fun hasEnabledLockScreen() = false
    override fun hasEnrolledFingerprint() = false
    override fun cancel() {}
}