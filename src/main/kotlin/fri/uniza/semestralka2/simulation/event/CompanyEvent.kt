package fri.uniza.semestralka2.simulation.event

import fri.uniza.semestralka2.core.AbstractEvent
import fri.uniza.semestralka2.simulation.CompanyEventSimulation

/**
 * Abstract event for [CompanyEventSimulation].
 * Extends [AbstractEvent]. Overrides its [AbstractEvent.core] by casting it to [CompanyEventSimulation].
 * @author David Zimen
 */
abstract class CompanyEvent(
    time: Double,
    override val core: CompanyEventSimulation
) : AbstractEvent(time, core)
