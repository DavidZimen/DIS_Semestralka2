package fri.uniza.semestralka2.simulation.objects.components

import fri.uniza.semestralka2.simulation.objects.customer.Customer
import fri.uniza.semestralka2.simulation.objects.customer.CustomerType
import java.util.*

/**
 * @author David Zimen
 */
class ServingDeskQueue(private val maxLength: Int = Int.MAX_VALUE) {

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
    }

    fun remove(type: CustomerType): Customer? {
        return when (type) {
            CustomerType.ONLINE -> onlineQueue.poll()
            else -> baseQueue.poll()
        }
    }

    fun isEmpty(type: CustomerType): Boolean {
        return when (type) {
            CustomerType.ONLINE -> onlineQueue.isEmpty()
            else -> baseQueue.isEmpty()
        }
    }

    fun canAdd() = baseQueue.size + onlineQueue.size < maxLength
}