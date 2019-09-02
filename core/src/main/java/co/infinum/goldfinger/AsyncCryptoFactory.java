package co.infinum.goldfinger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

/**
 * Class to create CryptoObject asynchronously
 */
class AsyncCryptoFactory {

    private final CryptoFactory cryptoFactory;
    private final ExecutorService executor;
    private Future task;

    AsyncCryptoFactory(@NonNull CryptoFactory cryptoFactory) {
        this.cryptoFactory = cryptoFactory;
        this.executor = Executors.newSingleThreadExecutor();
    }

    void createCryptoObject(@NonNull String keyName, @NonNull Mode mode, @NonNull AsyncCryptoFactory.Callback callback) {
        if (task != null && !task.isDone()) {
            task.cancel(true);
        }

        this.task = executor.submit(new CryptoObjectInitRunnable(cryptoFactory, keyName, mode, callback));
    }

    static abstract class Callback {

        abstract void onCryptoObjectCreated(@Nullable FingerprintManagerCompat.CryptoObject cryptoObject);

        boolean canceled = false;

        void cancel() {
            canceled = true;
        }
    }
}
