package co.infinum.goldfinger.rx;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import co.infinum.goldfinger.Crypto;
import co.infinum.goldfinger.CryptoFactory;
import co.infinum.goldfinger.Goldfinger;
import io.reactivex.Observable;

public interface RxGoldfinger {

    /**
     * @see Goldfinger#hasFingerprintHardware()
     */
    boolean hasFingerprintHardware();

    /**
     * @see Goldfinger#hasEnrolledFingerprint()
     */
    boolean hasEnrolledFingerprint();

    /**
     * @see Goldfinger#authenticate(Goldfinger.Callback)
     * @see Goldfinger.Result
     */
    Observable<Goldfinger.Result> authenticate();

    /**
     * @see Goldfinger#decrypt(String, String, Goldfinger.Callback)
     * @see Goldfinger.Result
     */
    Observable<Goldfinger.Result> decrypt(String keyName, String value);

    /**
     * @see Goldfinger#encrypt(String, String, Goldfinger.Callback)
     * @see Goldfinger.Result
     */
    Observable<Goldfinger.Result> encrypt(String keyName, String value);

    /**
     * @see Goldfinger#cancel()
     */
    void cancel();

    class Builder {

        private Goldfinger.Builder baseBuilder;

        public Builder(@NonNull Context context) {
            this.baseBuilder = new Goldfinger.Builder(context);
        }

        @NonNull
        public RxGoldfinger build() {
            return new RxGoldfingerImpl(baseBuilder.build());
        }

        @NonNull
        public RxGoldfinger.Builder setCrypto(@Nullable Crypto crypto) {
            baseBuilder.setCrypto(crypto);
            return this;
        }

        @NonNull
        public RxGoldfinger.Builder setCryptoFactory(@Nullable CryptoFactory cryptoFactory) {
            baseBuilder.setCryptoFactory(cryptoFactory);
            return this;
        }

        @NonNull
        public RxGoldfinger.Builder setLogEnabled(boolean logEnabled) {
            baseBuilder.setLogEnabled(logEnabled);
            return this;
        }
    }
}
