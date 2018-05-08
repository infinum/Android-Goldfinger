package co.infinum.goldfinger.rx;

import android.content.Context;

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
     */
    Observable<GoldfingerEvent> authenticate();

    /**
     * @see Goldfinger#decrypt(String, String, Goldfinger.Callback)
     */
    Observable<GoldfingerEvent> decrypt(String keyName, String value);

    /**
     * @see Goldfinger#encrypt(String, String, Goldfinger.Callback)
     */
    Observable<GoldfingerEvent> encrypt(String keyName, String value);

    /**
     * @see Goldfinger#cancel()
     */
    void cancel();

    class Builder {

        private Goldfinger.Builder baseBuilder;

        public Builder(Context context) {
            this.baseBuilder = new Goldfinger.Builder(context);
        }

        public RxGoldfinger build() {
            return new RxGoldfingerImpl(baseBuilder.build());
        }

        public RxGoldfinger.Builder setCrypto(Crypto crypto) {
            baseBuilder.setCrypto(crypto);
            return this;
        }

        public RxGoldfinger.Builder setCryptoFactory(CryptoFactory cryptoFactory) {
            baseBuilder.setCryptoFactory(cryptoFactory);
            return this;
        }

        public RxGoldfinger.Builder setLogEnabled(boolean logEnabled) {
            baseBuilder.setLogEnabled(logEnabled);
            return this;
        }
    }
}
