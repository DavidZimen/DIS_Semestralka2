package fri.uniza.semestralka2.simulation.core

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
     * [EventSimulationCore] to which the [Service] belongs.
     */
    protected open val core: EventSimulationCore
) {
    val queueLength: Int
        get() = queue.size

    val isQueueEmpty: Boolean
        get() = queue.isEmpty()

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
    var serving: T? = null
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
    }

    fun removeFromQueue(): T? {
        if (queue.isEmpty()) {
            return null
        }
        val agent = queue.remove()
        onQueueRemove(agent)
        return agent
    }

    fun removeAll() {
        val removedList = queue.toList()
        onRemovedElements(removedList)
        queue.clear()
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

        private var totalArea = 0.0

        private var lastValue: Pair<State, Double>? = null

        fun calculateWorkFlow() {
            if (lastValue == null) {
                lastValue = state to core.simulationTime
                return
            }

            val timeDifference = core.simulationTime - lastValue!!.second
            totalArea += timeDifference / 2 * (state.value + lastValue!!.first.value)
            averageWorkload = totalArea / core.simulationTime
            lastValue = state to core.simulationTime
        }
    }
}
