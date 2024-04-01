package fri.uniza.semestralka2.simulation.components

import fri.uniza.semestralka2.simulation.CompanyEventSimulation
import fri.uniza.semestralka2.simulation.core.Service
import fri.uniza.semestralka2.simulation.objects.customer.Customer
import fri.uniza.semestralka2.simulation.objects.customer.CustomerState

/**
 * Service for printing tickets after customer arrived into company.
 * @author David Zimen
 */
class TicketMachine(
    name: String,
    override val core: CompanyEventSimulation
) : Service<Customer>(name, core) {

    override fun onServingStart(agent: Customer) {
        agent.state = CustomerState.PRINTING_TICKET
    }

    override fun onServingEnd(agent: Customer) {
        agent.ticketTime = core.simulationTime
    }

    override fun onQueueAdd(agent: Customer) {
        with(agent) {
            automatStartTime = core.simulationTime
            state = CustomerState.WAITING_FOR_TICKET
        }
        // TODO queue length stats
    }

    override fun onQueueRemove(agent: Customer) {
        core.replicationStats.ticketQueueTime.addEntry(core.simulationTime - agent.automatStartTime)
        // TODO queue length stats
    }

    override fun onRemovedElements(removedList: List<Customer>) {
        removedList.forEach {
            it.state = CustomerState.LEFT_TICKET_MACHINE
            // TODO statistics to ticket machine queue time and queue length
        }
        core.ticketMachineSink.addAll(removedList)
    }
}