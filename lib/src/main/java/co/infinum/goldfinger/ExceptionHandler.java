package co.infinum.goldfinger;

/**
 * Implement custom exception handling.
 */
public interface ExceptionHandler {

    void onException(Throwable t);

    /**
     * Empty default implementation.
     */
    class Default implements ExceptionHandler {

        @Override
        public void onException(Throwable t) {
        }
    }
}
