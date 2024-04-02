package fri.uniza.semestralka2.simulation.core

import fri.uniza.semestralka2.general_utils.secondsToLocalTime
import fri.uniza.semestralka2.observer.Observable
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
     * [EventSimulationMode] of the simulation.
     */
    var mode = EventSimulationMode.REPLICATIONS
        set(value) {
            field = value
            scheduleDelayEvent()
        }

    /**
     * Current time of the simulation.
     */
    var simulationTime = -1.0

    /**
     * End of the simulation as defined by the user.
     */
    var simulationEndTime = -1.0
        protected set

    /**
     * [Observable] for enabling subscription on [EventSimulationState].
     * Possible to override.
     */
    open var simulationStateObservable = Observable<EventSimulationState>()
        protected set

    /**
     * For keeping changes until choosing to notify [simulationStateObservable] subscribers.
     * Attribute for [simulationTime] is automatically set at the end of [AbstractEvent] execution.
     */
    open lateinit var simulationState: EventSimulationState

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
     * Updates [simulationState] with current value.
     */
    open fun updateSimulationState() {
        simulationState.state = state
        simulationState.time = simulationTime.secondsToLocalTime()
        simulationState.speed = speed
    }

    /**
     * Runs Monte Carlo simulation with users implementation of abstract methods.
     * Overrides [SimulationCore.onRunSimulation].
     */
    final override fun onRunSimulation() {
        replicationsExecuted = 0
        eventsQueue.clear()
        resetSpeed()
        beforeSimulation()
        simulationRun(true)
    }

    /**
     * Resumes Event-driven simulation.
     * Overrides [SimulationCore.onResumeSimulation].
     */
    final override fun onResumeSimulation() {
        simulationRun(false)
    }

    // PROTECTED FUNCTIONS
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

    /**
     * Checks if the simulation is running or is paused.
     * @throws IllegalStateException when running or paused.
     */
    @Throws(IllegalStateException::class)
    protected fun simulationRunningCheck() {
        if (state != SimulationState.STOPPED) {
            throw IllegalStateException("Simulation is running. Cannot set new replications count !!!")
        }
    }

    // PRIVATE FUNCTIONS
    /**
     * Executes replication until [shouldContinueSimulation] returns false.
     * If simulation was not paused, the executes [afterSimulation] method.
     */
    private fun simulationRun(fromBeginning: Boolean) {
        do {
            if (fromBeginning) {
                beforeReplication()
                scheduleDelayEvent()
            }
            if (executeEvents()) {
                replicationsExecuted++
                afterReplication()
            }
        } while (shouldContinueSimulation())

        if (state == SimulationState.RUNNING) {
            afterSimulation()
            stopSimulation()
        }
    }

    /**
     * Executes [AbstractEvent]s from [eventsQueue] until it is empty or told to stop.
     * @throws IllegalStateException when time of current event is less than [simulationTime].
     * @return True if all events were successfully executed, false otherwise.
     */
    @Throws(IllegalStateException::class)
    private fun executeEvents(): Boolean {
        var event: AbstractEvent
        while (eventsQueue.isNotEmpty()) {
            event = eventsQueue.poll()
            if (event.time < simulationTime) {
                throw IllegalStateException("Time of event cannot be before simulation current time !!!")
            }
            event.execute()
            if (shouldStopSimulation()) {
                return false
            }
        }
        return true
    }

    /**
     * Schedules new delay event for the simulation.
     * Only if [mode] is [EventSimulationMode.SINGLE].
     */
    private fun scheduleDelayEvent() {
        if (mode == EventSimulationMode.REPLICATIONS) {
            return
        }
        val newTime = simulationTime + (SEC_BETWEEN_DELAYS * speed)
        if (newTime < simulationEndTime || eventsQueue.any { it !is DelayEvent }) {
            scheduleEvent(DelayEvent(newTime))
        }
    }

    /**
     * Function to determined if simulation run was stopped or paused.
     */
    private fun shouldStopSimulation() = state != SimulationState.RUNNING

    /**
     * Function to determine if execution of planned replications should continue.
     */
    private fun shouldContinueSimulation() = replicationsExecuted < replicationsCount && !shouldStopSimulation()

    companion object {
        private const val DELAY_MILLIS = 100L
        private const val SEC_BETWEEN_DELAYS = 1.0
    }

    /**
     * Class for delay event to simulate flow of the time in simulation.
     * Only in [EventSimulationMode.SINGLE].
     */
    private inner class DelayEvent(time: Double) : AbstractEvent(time, this) {
        override fun onExecute() {
            with(this@EventSimulationCore) {
                scheduleDelayEvent()
                Thread.sleep((DELAY_MILLIS / speed).toLong())
            }
        }
    }
}
