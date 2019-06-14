package co.infinum.goldfinger;

import androidx.biometric.BiometricPrompt;

public abstract class GoldfingerCallback {

    /**
     * User successfully authenticated.
     *
     * @see Goldfinger.Result
     * @see BiometricPrompt.AuthenticationCallback#onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult)
     */
    public abstract void onSuccess(Goldfinger.Result result);

    /**
     * Unrecoverable error happened, fingerprint authentication is complete.
     *
     * @see Error
     * @see BiometricPrompt.AuthenticationCallback#onAuthenticationError(int, CharSequence)
     */
    public abstract void onError(Error error);

    /**
     * Recoverable error when user is not recognized.
     *
     * @see BiometricPrompt.AuthenticationCallback#onAuthenticationFailed()
     */
    public void onFail() {}
}

