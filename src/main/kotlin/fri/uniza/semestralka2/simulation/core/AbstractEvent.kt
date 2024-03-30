package fri.uniza.semestralka2.simulation.core

/**
 * Abstract event class for [EventSimulationCore].
 * @author David Zimen
 */
abstract class AbstractEvent(
    /**
     * Time of the [AbstractEvent] execution.
     */
    val time: Double,

    /**
     * [EventSimulationCore] to which the [AbstractEvent] belongs.
     * Possible to override with custom implementation of [EventSimulationCore].
     */
    protected open val core: EventSimulationCore
) {

    /**
     * Executes the [AbstractEvent] logic.
     */
    abstract fun execute()
}