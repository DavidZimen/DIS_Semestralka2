package fri.uniza.semestralka2.simulation.components

import fri.uniza.semestralka2.simulation.CompanyEventSimulation
import fri.uniza.semestralka2.simulation.event.service_desk.CustomerServingStartEvent
import fri.uniza.semestralka2.simulation.objects.customer.Customer
import fri.uniza.semestralka2.simulation.objects.customer.CustomerState
import fri.uniza.semestralka2.simulation.objects.customer.CustomerType
import fri.uniza.semestralka2.statistics.ContinuousStatistic
import java.util.*

/**
 * @author David Zimen
 */
class ServingDeskQueue(
    private val maxLength: Int = Int.MAX_VALUE,
    startTime: Double,
    private val core: CompanyEventSimulation
) {

    var stats = ContinuousStatistic(startTime)
        private set

    private val baseQueue = PriorityQueue(compareBy<Customer> { it.type }.thenBy { it.ticketTime } )

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

    fun isEmpty(type: CustomerType): Boolean {
        return when (type) {
            CustomerType.ONLINE -> onlineQueue.isEmpty()
            else -> baseQueue.isEmpty()
        }
    }

    fun canAdd() = baseQueue.size + onlineQueue.size < maxLength

    fun isAtMaximumCapacity() = baseQueue.size + onlineQueue.size == maxLength
}