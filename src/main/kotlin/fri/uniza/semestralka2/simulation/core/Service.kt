package fri.uniza.semestralka2.simulation.core

import fri.uniza.semestralka2.statistics.ContinuousStatistic
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
    var servingStartTime: Double,

    /**
     * Time until when the [Service] should be active.
     */
    var servingEndTime: Double,

    /**
     * [EventSimulationCore] to which the [Service] belongs.
     */
    protected open val core: EventSimulationCore
) {
    /**
     * Current length of the [queue].
     */
    val queueLength: Int
        get() = queue.size

    /**
     * Indicator if [queue] contains no customer.
     */
    val isQueueEmpty: Boolean
        get() = queue.isEmpty()

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
     * Stats for calculating average queue length.
     */
    var queueStats = ContinuousStatistic(servingStartTime)
        private set

    /**
     * Agent currently served by [Service].
     */
    private var serving: T? = null
        private set(value) {
            state = if (value == null) State.FREE else State.OCCUPIED
            field = value
        }

    /**
     * Queue for objects, if services is occupied.
     */
    private val queue: Queue<T> = LinkedList()

    // OPEN FUNCTIONS
    /**
     * Action te be executed when calling [startServing].
     */
    protected open fun onServingStart(agent: T) { }

    /**
     * Action te be executed when calling [finishServing].
     */
    protected open fun onServingEnd(agent: T) { }

    /**
     * Action te be executed when calling [addToQueue].
     */
    protected open fun onQueueAdd(agent: T) { }

    /**
     * Action te be executed when calling [removeFromQueue].
     */
    protected open fun onQueueRemove(agent: T) { }

    /**
     * Action to be executed on removed elements from [queue].
     */
    protected open fun onRemovedElements(removedList: List<T>) { }

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

    @Throws(IllegalStateException::class)
    fun addToQueue(agent: T) {
        if (queue.contains(agent)) {
            throw IllegalStateException("Agent already in queue $name")
        }
        onQueueAdd(agent)
        queue.add(agent)
        queueStats.addEntry(queueLength, core.simulationTime)
    }

    fun removeFromQueue(): T? {
        if (queue.isEmpty()) {
            return null
        }
        val agent = queue.peek()
        onQueueRemove(agent)
        queueStats.addEntry(queueLength - 1, core.simulationTime)
        return queue.poll()
    }

    fun removeAll() {
        val removedList = queue.toList()
        onRemovedElements(removedList)
        queue.clear()
        queueStats.addEntry(queueLength, core.simulationTime)
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
