package fri.uniza.semestralka2.core

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
     * Enables for implementation of own logic.
     * ### No need to update [EventSimulationCore.simulationTime].
     */
    protected abstract fun onExecute()

    /**
     * Sets [EventSimulationCore.simulationTime] to [time]
     * and executes the [onExecute] user implemented logic.
     */
    fun execute() {
        core.simulationTime = time
        onExecute()
        updateState()
    }

    /**
     * Compares [core] with provided [core].
     */
    fun isSameCore(core: EventSimulationCore) = this.core === core

    /**
     * Sets [EventSimulationState.time] to [EventSimulationCore.simulationTime]
     * and emits new value to [EventSimulationCore.simulationStateObservable].
     */
    private fun updateState() {
        if (core.mode == EventSimulationMode.REPLICATIONS) {
            return
        }
        core.updateSimulationState()
        core.simulationStateObservable.next(core.simulationState)
    }
}