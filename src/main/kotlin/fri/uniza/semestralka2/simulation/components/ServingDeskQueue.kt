package fri.uniza.semestralka2.simulation.components

import fri.uniza.semestralka2.simulation.CompanyEventSimulation
import fri.uniza.semestralka2.simulation.event.service_desk.CustomerServingStartEvent
import fri.uniza.semestralka2.simulation.objects.customer.Customer
import fri.uniza.semestralka2.simulation.objects.customer.CustomerState
import fri.uniza.semestralka2.simulation.objects.customer.CustomerType
import fri.uniza.semestralka2.statistics.ContinuousStatistic
import java.util.*

/**
 * Queue for [ServingDesk].
 * Is divided into two [PriorityQueue]s. One for [CustomerType.ONLINE] and other for everyone else.
 * [onlineQueue] is prioritized by [Customer.ticketTime].
 * [baseQueue] is prioritized by [Customer.type] and then by [Customer.ticketTime].
 * @author David Zimen
 */
class ServingDeskQueue(
    private val maxLength: Int = Int.MAX_VALUE,
    startTime: Double,
    private val core: CompanyEventSimulation
) {

    /**
     * Stats regarding queue length at given time points.
     */
    var stats = ContinuousStatistic(startTime)
        private set

    /**
     * Queue for [CustomerType.COMMON] and [CustomerType.CONTRACTED].
     */
    private val baseQueue = PriorityQueue(compareBy<Customer> { it.type }.thenBy { it.ticketTime } )

    /**
     * Queue for [CustomerType.ONLINE].
     */
    private val onlineQueue = PriorityQueue(compareBy<Customer> { it.ticketTime })

    @Throws(IllegalStateException::class)
    fun add(customer: Customer) {
        if (baseQueue.size + onlineQueue.size == maxLength) {
            throw IllegalStateException("Queue for serving desks is full.")
        }

        when (customer.type) {
            CustomerType.ONLINE -> onlineQueue.add(customer)
            else -> baseQueue.add(customer)
        }

        customer.state = CustomerState.WAITING_FOR_SERVING
        customer.servingDeskQueueStartTime = core.simulationTime

        // schedule serving if customer can be served right away
        for (serviceDesk in core.serviceDesks) {
            if (serviceDesk.canBeServed(customer)) {
                core.scheduleEvent(CustomerServingStartEvent(core.simulationTime, serviceDesk, core))
                break
            }
        }
        stats.addEntry(baseQueue.size + onlineQueue.size, core.simulationTime)
    }

    /**
     * Removes customer of [type] from queue.
     * Adds information to statistics.
     * @return Removed [Customer] from queue or null when there is no customer of provided [type].
     */
    fun remove(type: CustomerType): Customer? {
        val customer = when (type) {
            CustomerType.ONLINE -> onlineQueue.poll()
            else -> baseQueue.poll()
        }

        // add statistics
        stats.addEntry(baseQueue.size + onlineQueue.size, core.simulationTime)
        customer?.let {
            if (customer.servingDeskQueueStartTime == -1.0) {
                core.replicationStats.servingDeskQueueTime.addEntry(0)
            } else {
                core.replicationStats.servingDeskQueueTime.addEntry(core.simulationTime - customer.servingDeskQueueStartTime)
            }
        }

        return customer
    }

    /**
     * Checks if [ServingDeskQueue] is empty.
     */
    fun isEmpty(type: CustomerType): Boolean {
        return when (type) {
            CustomerType.ONLINE -> onlineQueue.isEmpty()
            else -> baseQueue.isEmpty()
        }
    }

    /**
     * If some other customer can be added to queue.
     */
    fun canAdd() = baseQueue.size + onlineQueue.size < maxLength

    /**
     * @return Whether queue size is at [maxLength].
     */
    fun isAtMaximumCapacity() = baseQueue.size + onlineQueue.size == maxLength
}