package fri.uniza.semestralka2.core

import java.util.*

/**
 * @author David Zimen
 */
open class Service<T>(
    /**
     * Unique name of the [Service].
     */
    val name: String,

    /**
     * Time from when the [Service] should be active.
     */
    servingStartTime: Double,

    /**
     * Time until when the [Service] should be active.
     */
    servingEndTime: Double,

    /**
     * [EventSimulationCore] to which the [Service] belongs.
     */
    core: EventSimulationCore
) : Queue<T>(Int.MAX_VALUE, servingStartTime, servingEndTime, core) {

    /**
     * Indicator if employee is currently serving.
     */
    val isOccupied: Boolean
        get() = serving != null

    /**
     * Current [State] of the [Service].
     */
    var state = State.FREE
        private set

    /**
     * Workload of the [Service].
     */
    var workload = Workload()
        private set

    /**
     * Agent currently served by [Service].
     */
    private var serving: T? = null
        private set(value) {
            state = if (value == null) State.FREE else State.OCCUPIED
            field = value
        }

    // OPEN FUNCTIONS
    /**
     * Action te be executed when calling [startServing].
     */
    protected open fun onServingStart(agent: T) { }

    /**
     * Action te be executed when calling [finishServing].
     */
    protected open fun onServingEnd(agent: T) { }

    // PUBLIC FUNCTIONS
    @Throws(IllegalStateException::class)
    fun startServing(agent: T) {
        if (serving != null) {
            throw IllegalStateException("Cannot start serving while serving another at $name")
        }
        onServingStart(agent)
        serving = agent
        workload.calculateWorkFlow()
    }

    @Throws(IllegalStateException::class)
    fun finishServing(agent: T) {
        if (agent != serving) {
            throw IllegalStateException("Provided agent is not the same as serving agent in $name.")
        }
        onServingEnd(agent)
        serving = null
        workload.calculateWorkFlow()
    }

    /**
     * Possible states of the [Service].
     */
    enum class State(val value: Int) {
        FREE(0),
        OCCUPIED(1);
    }

    /**
     * Class to calculate percent work load of the [Service].
     */
     inner class Workload {
        var averageWorkload = 0.0
            private set

        private var timeOccupied = 0.0

        private var lastValue = state to servingStartTime

        fun calculateWorkFlow() {
            val endTime = if (servingEndTime < 0.0) core.simulationTime else servingEndTime
            timeOccupied += lastValue.first.value * (core.simulationTime - lastValue.second)
            averageWorkload = timeOccupied / (endTime - servingStartTime)
            lastValue = state to core.simulationTime
        }
    }
}
