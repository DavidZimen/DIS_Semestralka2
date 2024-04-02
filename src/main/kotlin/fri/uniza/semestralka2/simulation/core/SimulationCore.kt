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
        private set

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
    }

    /**
     * Speeds up the simulation run by doubling the current speed.
     */
    fun speedUpSimulation() {
        speed = minOf(speed * 2, MAX_SPEED_UP)
    }

    /**
     * Slows down the simulation run by half.
     */
    fun slowDownSimulation() {
        speed = maxOf(speed / 2, MIN_SLOW_DOWN)
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

    /**
     * Resets [speed] back to base 1.0 value.
     */
    protected fun resetSpeed() {
        speed = 1.0
    }

    companion object {
        private const val MAX_SPEED_UP = 1_000.0
        private const val MIN_SLOW_DOWN = 0.000_1
    }
}