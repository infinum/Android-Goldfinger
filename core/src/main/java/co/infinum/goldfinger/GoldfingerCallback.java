package co.infinum.goldfinger;

public interface GoldfingerCallback {

    /**
     * User successfully authenticated.
     *
     * @param value This value can be one of:
     *              1) Empty string - {@link Goldfinger#authenticate(GoldfingerParams, GoldfingerCallback)}
     *              2) Encrypted string - {@link Goldfinger#encrypt(GoldfingerParams, GoldfingerCallback)}
     *              3) Decrypted string - {@link Goldfinger#decrypt(GoldfingerParams, GoldfingerCallback)}
     */
    void onSuccess(String value);

    /**
     * @see Error
     */
    void onError(Error error);
}

