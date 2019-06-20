package co.infinum.goldfinger;

/**
 * Singleton class that wraps time functions.
 * It is mainly needed for tests.
 */
class Clock {

    private static Clock instance;

    private Clock() {
    }

    static Clock instance() {
        if (instance == null) {
            instance = new Clock();
        }
        return instance;
    }

    long currentTimeMs() {
        return System.currentTimeMillis();
    }

    boolean isBeforeNow(long timeMs) {
        return timeMs < currentTimeMs();
    }
}
