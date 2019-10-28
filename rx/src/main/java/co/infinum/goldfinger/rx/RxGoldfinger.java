package co.infinum.goldfinger.rx;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import co.infinum.goldfinger.CipherCryptoHandler;
import co.infinum.goldfinger.CipherFactory;
import co.infinum.goldfinger.Goldfinger;
import co.infinum.goldfinger.MacCryptoHandler;
import co.infinum.goldfinger.MacFactory;
import co.infinum.goldfinger.SignatureCryptoHandler;
import co.infinum.goldfinger.SignatureFactory;
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
        public RxGoldfinger.Builder cipherCryptoHandler(@Nullable CipherCryptoHandler cipherCryptoHandler) {
            goldfingerBuilder.cipherCryptoHandler(cipherCryptoHandler);
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
        public RxGoldfinger.Builder macCryptoHandler(@Nullable MacCryptoHandler macCryptoHandler) {
            goldfingerBuilder.macCryptoHandler(macCryptoHandler);
            return this;
        }

        @NonNull
        public RxGoldfinger.Builder macFactory(@Nullable MacFactory macFactory) {
            goldfingerBuilder.macFactory(macFactory);
            return this;
        }

        @NonNull
        public RxGoldfinger.Builder signatureCryptoHandler(@Nullable SignatureCryptoHandler signatureCryptoHandler) {
            goldfingerBuilder.signatureCryptoHandler(signatureCryptoHandler);
            return this;
        }

        @NonNull
        public RxGoldfinger.Builder signatureFactory(@Nullable SignatureFactory signatureFactory) {
            goldfingerBuilder.signatureFactory(signatureFactory);
            return this;
        }
    }
}
