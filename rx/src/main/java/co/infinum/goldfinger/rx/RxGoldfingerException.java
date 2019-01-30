package co.infinum.goldfinger.rx;

import co.infinum.goldfinger.Error;

public class RxGoldfingerException extends Exception {

    private final Error error;

    RxGoldfingerException(Error error) {
        this.error = error;
    }

    public Error error() {
        return error;
    }
}
