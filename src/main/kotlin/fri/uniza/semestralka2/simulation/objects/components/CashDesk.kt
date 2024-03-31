package fri.uniza.semestralka2.simulation.objects.components

import fri.uniza.semestralka2.simulation.CompanyEventSimulation
import fri.uniza.semestralka2.simulation.core.Service
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
    override val core: CompanyEventSimulation
) : Service<Customer>(name, core) {

    override fun onServingStart(agent: Customer) {
        // if customer waited in different queue
        if (agent.cashDeskQueue != null && agent.cashDeskQueue?.name != name) {
            throw IllegalStateException("Customer ${agent.id} came from ${agent.cashDeskQueue?.name} " +
                    "and trying to serve at $name."
            )
        }

        with(agent) {
            cashDeskStartTime = core.simulationTime
            cashDeskPaid = this@CashDesk
            state = CustomerState.PAYING
        }
    }

    @Throws(IllegalStateException::class)
    override fun onQueueAdd(agent: Customer) {
        // if customer id waiting in
        if (agent.cashDeskQueue != null) {
            throw IllegalStateException("Customer ${agent.id} assigned to ${agent.cashDeskQueue?.name} " +
                    "while trying to enter $name"
            )
        }

        with(agent) {
            cashDeskQueue = this@CashDesk
            state = CustomerState.WAITING_FOR_PAY
        }
    }

    override fun onQueueRemove(agent: Customer) {
        // TODO add to statistics in core
    }
}
