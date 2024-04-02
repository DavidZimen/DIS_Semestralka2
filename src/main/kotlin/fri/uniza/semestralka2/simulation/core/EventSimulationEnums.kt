package fri.uniza.semestralka2.simulation.core

/**
 * Modes of the Event simulation.
 */
enum class EventSimulationMode {
    /**
     * Creates delays event for detailed checking.
     */
    SINGLE,
    /**
     * No delay events. Execute planned replications as fast as possible.
     */
    REPLICATIONS;
}

/**
 * State of the [SimulationCore] in regards tu running.
 */
enum class SimulationState {
    /**
     * Indication if simulation is stopped by user.
     */
    STOPPED,
    /**
     * Indication if simulation is paused by user.
     */
    PAUSED,
    /**
     * Simulation is currently executing.
     */
    RUNNING;
}
