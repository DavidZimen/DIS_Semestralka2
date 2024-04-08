package fri.uniza.semestralka2.core

import fri.uniza.semestralka2.statistics.ContinuousStatistic
import java.util.*
import java.util.Queue

/**
 * Queue for agents to wait before being served.
 * @author David Zimen
 */
open class Queue<T>(
    private val maxLength: Int = Int.MAX_VALUE,
    /**
     * Time from when the [Service] should be active.
     */
    var servingStartTime: Double,

    /**
     * Time until when the [Service] should be active.
     */
    var servingEndTime: Double,

    /**
     * [EventSimulationCore] to which the [Queue] belongs.
     */
    protected open val core: EventSimulationCore
) {

    /**
     * Queue for objects, if services is occupied.
     */
    protected open val queue: Queue<T> = LinkedList()

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
     * @return Whether queue size is at [maxLength].
     */
    val isAtMaximumCapacity: Boolean
        get() = queueLength == maxLength

    /**
     * Stats for calculating average queue length.
     */
    var queueStats = ContinuousStatistic(servingStartTime)
        private set

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

    @Throws(IllegalStateException::class)
    fun addToQueue(agent: T) {
        if (queue.contains(agent)) {
            throw IllegalStateException("Agent already in queue.")
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
}