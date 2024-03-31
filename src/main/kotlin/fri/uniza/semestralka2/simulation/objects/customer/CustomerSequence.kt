package fri.uniza.semestralka2.simulation.objects.customer

/**
 * Sequence of [Long] numbers for customers ids generated to simulation.
 * Very simple sequence without room for customization.
 * @author David Zimen
 */
object CustomerSequence {
    /**
     * Holds last generated value.
     */
    private var number = 0L

    /**
     * Provides next value from sequence.
     */
    val next: Long
        get() = ++number

    /**
     * Resets [number] back to 0.
     */
    fun reset() {
        number = 0L
    }
}