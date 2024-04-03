package fri.uniza.semestralka2.simulation.core

/**
 * Simulation core with important methods [runSimulation], [stopSimulation], [resumeSimulation] and [pauseSimulation].
 * All methods are open, so their implementation is not mandatory, but highly recommended.
 * @author David Zimen
 */
open class SimulationCore {

    /**
     * Speed index of the simulation.
     */
    var speed = 1.0
        set(value) {
            field = when {
                value > MAX_SPEED_UP -> MAX_SPEED_UP
                value < MIN_SLOW_DOWN -> MIN_SLOW_DOWN
                else -> value
            }
        }

    /**
     * Current state of the simulation.
     */
    var state = SimulationState.STOPPED
        private set

    /**
     * Runs the simulation with provided configuration.
     */
    @Throws(IllegalStateException::class)
    fun runSimulation() {
        if (state != SimulationState.STOPPED) {
            throw IllegalStateException("Running can only be started from ${SimulationState.STOPPED} state.")
        }
        state = SimulationState.RUNNING
        onRunSimulation()
    }

    /**
     * Stops simulation, without possibility to resume.
     */
    @Throws(IllegalStateException::class)
    fun stopSimulation() {
        if (state != SimulationState.RUNNING) {
            throw IllegalStateException("Simulation can only be stopped from ${SimulationState.RUNNING} state.")
        }
        state = SimulationState.STOPPED
        onStopSimulation()
    }

    /**
     * Pauses simulation at given point, with possibility to resume..
     */
    @Throws(IllegalStateException::class)
    fun pauseSimulation() {
        if (state != SimulationState.RUNNING) {
            throw IllegalStateException("Simulation can only be paused from ${SimulationState.RUNNING} state.")
        }
        state = SimulationState.PAUSED
        onPauseSimulation()
    }

    /**
     * Resumes simulation to the point when it was stopped.
     */
    fun resumeSimulation() {
        if (state != SimulationState.PAUSED) {
            throw IllegalStateException("Simulation can only be resumed from ${SimulationState.PAUSED} state.")
        }
        state = SimulationState.RUNNING
        onResumeSimulation()
    }

    /**
     * Logic for [runSimulation]
     */
    open fun onRunSimulation() { }

    /**
     * Logic for [stopSimulation]
     */
    open fun onStopSimulation() { }

    /**
     * Logic for [pauseSimulation]
     */
    open fun onPauseSimulation() { }

    /**
     * Logic for [resumeSimulation]
     */
    open fun onResumeSimulation() { }

    companion object {
        const val MAX_SPEED_UP = 100.0
        const val MIN_SLOW_DOWN = 0.25
    }
}