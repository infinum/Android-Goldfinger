package co.infinum.goldfinger;

import javax.crypto.Mac;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface MacFactory {

    @Nullable
    Mac createEncryptionMac(@NonNull String key);

    @Nullable
    Mac createDecryptionMac(@NonNull String key);
}
