package co.infinum.goldfinger;

import androidx.annotation.NonNull;

/**
 * Wraps data used for encryption or decryption.
 * <p>
 * keyName is used as unique key identifier and is used when creating
 * and loading {@link java.security.Key} from the {@link java.security.KeyStore}
 * <p>
 * value is the actual value that will be encrypted or decrypted when the user
 * successfully authenticates.
 */
public class CryptographyData {

    private final String keyName;
    private final String value;

    public CryptographyData(@NonNull String keyName, @NonNull String value) {
        this.keyName = keyName;
        this.value = value;
    }

    @NonNull
    public String keyName() {
        return keyName;
    }

    @NonNull
    public String value() {
        return value;
    }
}
