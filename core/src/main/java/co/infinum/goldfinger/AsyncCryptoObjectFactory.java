package co.infinum.goldfinger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricPrompt;

class AsyncCryptoObjectFactory {

    private final CryptoObjectFactory cryptoObjectFactory;
    private final ExecutorService executor;
    private Future task;

    AsyncCryptoObjectFactory(@NonNull CryptoObjectFactory cryptoObjectFactory) {
        this.cryptoObjectFactory = cryptoObjectFactory;
        this.executor = Executors.newSingleThreadExecutor();
    }

    void createCryptoObject(
        @NonNull CryptographyData cryptographyData,
        @NonNull Mode mode,
        @NonNull AsyncCryptoObjectFactory.Callback callback
    ) {
        if (task != null && !task.isDone()) {
            task.cancel(true);
        }

        this.task = executor.submit(new CryptoObjectInitRunnable(cryptoObjectFactory, cryptographyData, mode, callback));
    }

    static abstract class Callback {

        abstract void onCryptoObjectCreated(@Nullable BiometricPrompt.CryptoObject cryptoObject);

        boolean canceled = false;

        void cancel() {
            canceled = true;
        }
    }
}
