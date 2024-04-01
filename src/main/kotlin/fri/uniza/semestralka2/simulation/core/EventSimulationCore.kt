package fri.uniza.semestralka2.simulation.core

import java.util.*

/**
 * Abstract core of the Event driven simulation.
 * @author David Zimen
 */
open class EventSimulationCore : SimulationCore() {

    /**
     * Number of replications to be done in 1 simulation run.
     * Base value is [Long.MAX_VALUE] replications.
     */
    var replicationsCount = Long.MAX_VALUE
        set(value) {
            simulationRunningCheck()
            field = value
        }

    /**
     * Number of replications successfully executed.
     */
    var replicationsExecuted = 0L
        private set

    /**
     * [Mode] of the simulation.
     */
    var mode = Mode.REPLICATIONS
        set(value) {
            field = value
            scheduleDelayEvent()
        }

    /**
     * Speed index of the simulation.
     */
    var speed = 1.0
        private set

    /**
     * Current time of the simulation.
     */
    var simulationTime = -1.0

    /**
     * Indication if simulation is stopped by user.
     */
    private var simulationStopped = true

    /**
     * Indication if simulation is paused by user.
     */
    private var simulationPaused = false

    /**
     * [PriorityQueue] of simulation [AbstractEvent]s, that is sorted by [AbstractEvent.time].
     */
    private val eventsQueue = PriorityQueue<AbstractEvent>(compareBy { it.time })

    // PUBLIC FUNCTIONS
    /**
     * Adds [event] to the [eventsQueue] based on its [AbstractEvent.time] of start.
     * @param event New scheduled event.
     * @throws IllegalStateException when [AbstractEvent.core] is not equals to this instance.
     */
    @Throws(IllegalStateException::class)
    fun scheduleEvent(event: AbstractEvent) {
        if (!event.isSameCore(this)) {
            throw IllegalStateException("Event has different core.")
        }
        eventsQueue.add(event)
    }

    /**
     * Runs Monte Carlo simulation with users implementation of abstract methods.
     * Overrides [SimulationCore.runSimulation].
     */
    final override fun runSimulation() {
        replicationsExecuted = 0
        eventsQueue.clear()
        beforeSimulation()
        simulationRun()
    }

    /**
     * Resumes Event-driven simulation.
     * Overrides [SimulationCore.resumeSimulation].
     */
    final override fun resumeSimulation() {
        if (simulationStopped || !simulationPaused) {
            throw IllegalStateException("Cannot be resumed from stopped state.")
        }
        simulationRun()
    }

    /**
     * Stops Event-driven simulation, without possibility to resume.
     * Overrides [SimulationCore.stopSimulation].
     */
    final override fun stopSimulation() {
        simulationStopped = true
    }

    /**
     * Pauses Event-driven simulation, with possibility to resume.
     * Overrides [SimulationCore.pauseSimulation].
     */
    final override fun pauseSimulation() {
        simulationPaused = true
    }

    /**
     * Speeds up the simulation by doubling the current speed.
     * Overrides [SimulationCore.speedUpSimulation].
     */
    final override fun speedUpSimulation() {
        speed = minOf(speed * 2, MAX_SPEED_UP)
    }

    /**
     * Decreases current [speed] by half.
     * Overrides [SimulationCore.slowDownSimulation].
     */
    final override fun slowDownSimulation() {
        speed = maxOf(speed / 2, MIN_SLOW_DOWN)
    }

    // PROTECTED FUNCTIONS
    /**
     * Checks if the simulation is running or is paused.
     * @throws IllegalStateException when running or paused.
     */
    @Throws(IllegalStateException::class)
    protected fun simulationRunningCheck() {
        if (!simulationStopped || simulationPaused) {
            throw IllegalStateException("Simulation is running. Cannot set new replications count !!!")
        }
    }

    /**
     * Method to execute before whole simulation.
     */
    protected open fun beforeSimulation() { }

    /**
     * Method to execute before every single replication.
     */
    protected open fun beforeReplication() { }

    /**
     * Method to execute after all replications are finished.
     */
    protected open fun afterSimulation() { }

    /**
     * Method to execute after every single replication.
     */
    protected open fun afterReplication() { }

    // PRIVATE FUNCTIONS
    /**
     * Executes replication until [shouldContinueSimulation] returns false.
     * If simulation was not paused, the executes [afterSimulation] method.
     */
    private fun simulationRun() {
        simulationStopped = false
        simulationPaused = false

        do {
            beforeReplication()
            executeEvents()
            replicationsExecuted++
            afterReplication()
        } while (shouldContinueSimulation())

        if (!simulationPaused) {
            afterSimulation()
            simulationStopped = true
        }
    }

    /**
     * Executes [AbstractEvent]s from [eventsQueue] until it is empty or told to stop.
     * @throws IllegalStateException when time of current event is less than [simulationTime].
     */
    @Throws(IllegalStateException::class)
    private fun executeEvents() {
        var event: AbstractEvent
        while (eventsQueue.isNotEmpty()) {
            event = eventsQueue.poll()
            if (event.time < simulationTime) {
                throw IllegalStateException("Time of event cannot be before simulation current time !!!")
            }
            event.execute()
            if (shouldStopSimulation()) {
                break
            }
        }
    }

    /**
     * Schedules new delay event for the simulation.
     * Only if [mode] is [Mode.SINGLE].
     */
    private fun scheduleDelayEvent() {
        if (mode == Mode.SINGLE) {
            scheduleEvent(DelayEvent(simulationTime + BASE_DELAY_MILLIS))
        }
    }

    /**
     * Function to determined if simulation run was stopped or paused.
     */
    private fun shouldStopSimulation() = simulationStopped || simulationPaused

    /**
     * Function to determine if execution of planned replications should continue.
     */
    private fun shouldContinueSimulation() = replicationsExecuted < replicationsCount && !shouldStopSimulation()

    companion object {
        private const val MAX_SPEED_UP = 1_000.0
        private const val MIN_SLOW_DOWN = 0.000_1
        private const val BASE_DELAY_MILLIS = 1_000L
    }

    /**
     * Modes of the Event simulation.
     */
    enum class Mode {
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
     * Class for delay event to simulate flow of the time in simulation.
     * Only in [Mode.SINGLE].
     */
    private inner class DelayEvent(time: Double) : AbstractEvent(time, this) {
        override fun onExecute() {
            with(this@EventSimulationCore) {
                scheduleDelayEvent()
                Thread.sleep((BASE_DELAY_MILLIS / speed).toLong())
            }
        }
    }
}