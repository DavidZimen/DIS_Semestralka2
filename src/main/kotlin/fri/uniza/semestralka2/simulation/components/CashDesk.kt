package fri.uniza.semestralka2.simulation.components

import fri.uniza.semestralka2.core.Service
import fri.uniza.semestralka2.simulation.CompanyEventSimulation
import fri.uniza.semestralka2.simulation.objects.customer.Customer
import fri.uniza.semestralka2.simulation.objects.customer.CustomerState

/**
 * Service for [CompanyEventSimulation] for customers to pay their orders.
 * Allows waiting in queue, serving.
 * Also updates statistic attributes in [Customer] class.
 * Keeps track of employee utilization.
 * @author David Zimen
 */
open class CashDesk(
    name: String,
    servingStart: Double,
    servingEnd: Double,
    override val core: CompanyEventSimulation
) : Service<Customer>(name, servingStart, servingEnd, core) {

    /**
     * Stars serving provided [agent].
     * Changes its [Customer.state] and [Customer.cashDeskPaid].
     * @throws IllegalStateException when [Customer.cashDeskQueue] is not the same as this instance.
     */
    @Throws(IllegalStateException::class)
    override fun onServingStart(agent: Customer) {
        // if customer waited in different queue
        if (agent.cashDeskQueue != null && agent.cashDeskQueue != this) {
            throw IllegalStateException("Customer ${agent.id} came from ${agent.cashDeskQueue?.name} " +
                    "and trying to serve at $name."
            )
        }

        with(agent) {
            cashDeskPaid = this@CashDesk
            state = CustomerState.PAYING
        }
    }

    /**
     * Adds [agent] to [queue].
     * Sets [Customer.cashDeskQueueStartTime], [Customer.cashDeskQueue] and [Customer.state] attributes.
     */
    @Throws(IllegalStateException::class)
    override fun onQueueAdd(agent: Customer) {
        // if customer id waiting in
        if (agent.cashDeskQueue != null) {
            throw IllegalStateException("Customer ${agent.id} assigned to ${agent.cashDeskQueue?.name} " +
                    "while trying to enter $name"
            )
        }

        with(agent) {
            cashDeskQueueStartTime = core.simulationTime
            cashDeskQueue = this@CashDesk
            state = CustomerState.WAITING_FOR_PAY
        }

        super.onQueueAdd(agent)
    }

    /**
     * Removes [agent] from queue and adds time spent in [queue] to [CompanyEventSimulation.replicationStats].
     */
    override fun onQueueRemove(agent: Customer) {
        if (agent.cashDeskQueueStartTime == -1.0) {
            core.replicationStats.cashDeskQueueTime.addEntry(0)
        } else {
            core.replicationStats.cashDeskQueueTime.addEntry(core.simulationTime - agent.cashDeskQueueStartTime)
        }
    }
}
