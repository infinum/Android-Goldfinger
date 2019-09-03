package co.infinum.goldfinger.rx;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import co.infinum.goldfinger.CryptoObjectFactory;
import co.infinum.goldfinger.CryptographyHandler;
import co.infinum.goldfinger.Goldfinger;
import io.reactivex.Observable;

@SuppressWarnings("unused")
public interface RxGoldfinger {

    /**
     * @see Goldfinger#canAuthenticate()
     */
    boolean canAuthenticate();

    /**
     * @see Goldfinger#authenticate
     */
    @NonNull
    Observable<Goldfinger.Result> authenticate(@NonNull Goldfinger.Params params);

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
