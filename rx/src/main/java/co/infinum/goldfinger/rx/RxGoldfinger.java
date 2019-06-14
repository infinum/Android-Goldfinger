package co.infinum.goldfinger.rx;

import android.content.Context;

import androidx.annotation.NonNull;
import co.infinum.goldfinger.CryptoObjectFactory;
import co.infinum.goldfinger.CryptographyHandler;
import co.infinum.goldfinger.Goldfinger;
import co.infinum.goldfinger.GoldfingerCallback;
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
     * @see Goldfinger#decrypt(GoldfingerParams, GoldfingerCallback)
     */
    @NonNull
    Observable<Goldfinger.Result> decrypt(@NonNull GoldfingerParams params);

    /**
     * @see Goldfinger#encrypt(GoldfingerParams, GoldfingerCallback)
     */
    @NonNull
    Observable<Goldfinger.Result> encrypt(@NonNull GoldfingerParams params);

    /**
     * @see Goldfinger#cancel()
     */
    void cancel();

    @SuppressWarnings("unused")
    class Builder {

        private Goldfinger.Builder goldfingerBuilder;

        public Builder(Context context) {
            this.goldfingerBuilder = new Goldfinger.Builder(context);
        }

        public RxGoldfinger build() {
            return new RxGoldfingerImpl(goldfingerBuilder.build());
        }

        public RxGoldfinger.Builder cryptoObjectFactory(CryptoObjectFactory cryptoObjectFactory) {
            goldfingerBuilder.cryptoObjectFactory(cryptoObjectFactory);
            return this;
        }

        public RxGoldfinger.Builder cryptographyHandler(CryptographyHandler cryptographyHandler) {
            goldfingerBuilder.cryptographyHandler(cryptographyHandler);
            return this;
        }

        public RxGoldfinger.Builder logEnabled(boolean logEnabled) {
            goldfingerBuilder.logEnabled(logEnabled);
            return this;
        }
    }
}
