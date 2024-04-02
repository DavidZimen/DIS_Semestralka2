package fri.uniza.semestralka2.simulation.event

import fri.uniza.semestralka2.simulation.CompanyEventSimulation
import fri.uniza.semestralka2.simulation.core.AbstractEvent
import fri.uniza.semestralka2.simulation.core.EventSimulationCore

/**
 * Abstract event for [CompanyEventSimulation].
 * Extends [AbstractEvent]. Overrides its [AbstractEvent.core] by casting it to [CompanyEventSimulation].
 * @author David Zimen
 */
abstract class CompanyEvent(
    time: Double,
    override val core: CompanyEventSimulation
) : AbstractEvent(time, core) {


    /**
     * Enables for implementation of own logic.
     * ### No need to update [EventSimulationCore.simulationTime].
     */
    abstract fun onExecute()

    /**
     * Overrides method from [AbstractEvent] and adds updating
     * state after finishing custom [onExecute] method.
     */
    override fun onExecuteEvent() {
        onExecute()
        core.updateState()
    }
}
