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
    servingStart: Double,
    servingEnd: Double,
    override val core: CompanyEventSimulation
) : Service<Customer>(name, servingStart, servingEnd, core) {

    override fun onServingStart(agent: Customer) {
        agent.state = CustomerState.PRINTING_TICKET
    }

    override fun onServingEnd(agent: Customer) {
        agent.ticketTime = core.simulationTime
    }

    override fun onQueueAdd(agent: Customer) {
        with(agent) {
            ticketMachineQueueStartTime = core.simulationTime
            state = CustomerState.WAITING_FOR_TICKET
        }
    }

    override fun onQueueRemove(agent: Customer) {
        core.replicationStats.ticketQueueTime.addEntry(core.simulationTime - agent.ticketMachineQueueStartTime)
    }

    override fun onRemovedElements(removedList: List<Customer>) {
        removedList.forEach {
            it.state = CustomerState.LEFT_TICKET_MACHINE
        }
        core.moveToTicketMachineSink(removedList)
    }
}