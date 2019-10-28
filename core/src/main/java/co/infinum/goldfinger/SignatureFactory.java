package co.infinum.goldfinger;

import java.security.Signature;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface SignatureFactory {

    @Nullable
    Signature createEncryptionSignature(@NonNull String key);

    @Nullable
    Signature createDecryptionSignature(@NonNull String key);
}
