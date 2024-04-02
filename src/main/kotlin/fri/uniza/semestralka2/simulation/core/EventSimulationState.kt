package fri.uniza.semestralka2.simulation.core

import java.time.LocalTime

/**
 * Abstract class to be extended for keeping state of the [EventSimulationCore].
 */
open class EventSimulationState(
    var time: LocalTime = LocalTime.now(),
    var state: SimulationState = SimulationState.STOPPED,
    var speed: Double = 1.0
)