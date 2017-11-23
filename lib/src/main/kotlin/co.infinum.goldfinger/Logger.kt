package co.infinum.goldfinger

interface Logger {

    /**
     * Log throwable.
     */
    fun log(t: Throwable)

    /**
     * Log message.
     */
    fun log(message: String)

    class Default : Logger {
        override fun log(t: Throwable) {}
        override fun log(message: String) {}
    }
}