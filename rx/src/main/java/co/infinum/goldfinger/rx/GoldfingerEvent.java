package co.infinum.goldfinger.rx;

import co.infinum.goldfinger.Error;
import co.infinum.goldfinger.Goldfinger;

/**
 * Empty superclass of all GoldfingerEvents.
 */
public abstract class GoldfingerEvent {

    /**
     * @see Goldfinger.Callback#onError(Error)
     */
    public static class OnError extends GoldfingerEvent {

        private final Error error;

        OnError(Error error) {
            this.error = error;
        }

        public Error error() {
            return error;
        }
    }

    /**
     * @see Goldfinger.Callback#onReady()
     */
    public static class OnReady extends GoldfingerEvent {
    }

    /**
     * @see Goldfinger.Callback#onSuccess(String)
     */
    public static class OnSuccess extends GoldfingerEvent {

        private final String value;

        OnSuccess(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }
}