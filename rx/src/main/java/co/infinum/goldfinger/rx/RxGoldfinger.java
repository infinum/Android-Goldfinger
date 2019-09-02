package co.infinum.goldfinger.rx;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import co.infinum.goldfinger.CryptoObjectFactory;
import co.infinum.goldfinger.CryptographyHandler;
import co.infinum.goldfinger.Goldfinger;
import co.infinum.goldfinger.GoldfingerParams;
import io.reactivex.Observable;

public interface RxGoldfinger {

    /**
     * @see Goldfinger#hasFingerprintHardware()
     */
    boolean hasFingerprintHardware();

    /**
     * @see Goldfinger#authenticate(GoldfingerParams, GoldfingerCallback)
     */
    @NonNull
    Observable<Goldfinger.Result> authenticate(@NonNull GoldfingerParams params);

    /**
     * @see Goldfinger#decrypt(String, String, Goldfinger.Callback)
     * @see Goldfinger.Result
     */
    Observable<Goldfinger.Result> decrypt(@NonNull GoldfingerParams params);

    /**
     * @see Goldfinger#encrypt(String, String, Goldfinger.Callback)
     * @see Goldfinger.Result
     */
    Observable<Goldfinger.Result> encrypt(@NonNull GoldfingerParams params);

    /**
     * @see Goldfinger#cancel()
     */
    void cancel();

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
        public RxGoldfinger.Builder cryptoObjectFactory(@Nullable CryptoObjectFactory cryptoObjectFactory) {
            goldfingerBuilder.cryptoObjectFactory(cryptoObjectFactory);
            return this;
        }

        @NonNull
        public RxGoldfinger.Builder cryptographyHandler(@Nullable CryptographyHandler cryptographyHandler) {
            goldfingerBuilder.cryptographyHandler(cryptographyHandler);
            return this;
        }

        @NonNull
        public RxGoldfinger.Builder setLogEnabled(boolean logEnabled) {
            goldfingerBuilder.logEnabled(logEnabled);
            return this;
        }
    }
}
