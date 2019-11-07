package co.infinum.goldfinger.rx;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import co.infinum.goldfinger.Goldfinger;
import co.infinum.goldfinger.crypto.CipherCrypter;
import co.infinum.goldfinger.crypto.CipherFactory;
import co.infinum.goldfinger.crypto.MacCrypter;
import co.infinum.goldfinger.crypto.MacFactory;
import co.infinum.goldfinger.crypto.SignatureCrypter;
import co.infinum.goldfinger.crypto.SignatureFactory;
import io.reactivex.Observable;

@SuppressWarnings("unused")
public interface RxGoldfinger {

    /**
     * @see Goldfinger#canAuthenticate
     */
    boolean canAuthenticate();

    /**
     * @see Goldfinger#hasFingerprintHardware
     */
    boolean hasFingerprintHardware();

    /**
     * @see Goldfinger#hasEnrolledFingerprint
     */
    boolean hasEnrolledFingerprint();

    /**
     * @see Goldfinger#authenticate
     */
    @NonNull
    Observable<Goldfinger.Result> authenticate(@NonNull Goldfinger.PromptParams params);

    /**
     * @see Goldfinger#encrypt
     */
    Observable<Goldfinger.Result> encrypt(@NonNull Goldfinger.PromptParams params, @NonNull String key, @NonNull String value);

    /**
     * @see Goldfinger#decrypt
     */
    Observable<Goldfinger.Result> decrypt(@NonNull Goldfinger.PromptParams params, @NonNull String key, @NonNull String value);

    /**
     * @see Goldfinger#cancel
     */
    void cancel();

    /**
     * @see Goldfinger.Builder
     */
    @SuppressWarnings("unused")
    class Builder {

        private Goldfinger.Builder goldfingerBuilder;

        public Builder(@NonNull Context context) {
            this.goldfingerBuilder = new Goldfinger.Builder(context);
        }

        @NonNull
        public RxGoldfinger build() {
            return new RxGoldfingerImpl(goldfingerBuilder.build());
        }

        @NonNull
        public RxGoldfinger.Builder cipherCrypter(@Nullable CipherCrypter cipherCrypter) {
            goldfingerBuilder.cipherCrypter(cipherCrypter);
            return this;
        }

        @NonNull
        public RxGoldfinger.Builder cipherFactory(@Nullable CipherFactory cipherFactory) {
            goldfingerBuilder.cipherFactory(cipherFactory);
            return this;
        }

        @NonNull
        public RxGoldfinger.Builder logEnabled(boolean logEnabled) {
            goldfingerBuilder.logEnabled(logEnabled);
            return this;
        }

        @NonNull
        public RxGoldfinger.Builder macCrypter(@Nullable MacCrypter macCrypter) {
            goldfingerBuilder.macCrypter(macCrypter);
            return this;
        }

        @NonNull
        public RxGoldfinger.Builder macFactory(@Nullable MacFactory macFactory) {
            goldfingerBuilder.macFactory(macFactory);
            return this;
        }

        @NonNull
        public RxGoldfinger.Builder signatureCrypter(@Nullable SignatureCrypter signatureCrypter) {
            goldfingerBuilder.signatureCrypter(signatureCrypter);
            return this;
        }

        @NonNull
        public RxGoldfinger.Builder signatureFactory(@Nullable SignatureFactory signatureFactory) {
            goldfingerBuilder.signatureFactory(signatureFactory);
            return this;
        }
    }
}
