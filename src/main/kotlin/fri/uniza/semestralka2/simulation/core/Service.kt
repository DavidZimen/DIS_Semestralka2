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

    /**
     * Agent currently served by [Service].
     */
    protected var serving: T? = null
        private set

    /**
     * Queue for objects, if services is occupied.
     */
    protected val queue: Queue<T> = LinkedList()

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


    // PUBLIC FUNCTIONS
    @Throws(IllegalStateException::class)
    fun startServing(agent: T) {
        if (serving != null) {
            throw IllegalStateException("Cannot start serving while serving another at $name")
        }
        onServingStart(agent)
        serving = agent
    }

    @Throws(IllegalStateException::class)
    fun finishServing(agent: T) {
        if (agent != serving) {
            throw IllegalStateException("Provided agent is not the same as serving agent in $name.")
        }
        onServingEnd(agent)
        serving = null
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
}