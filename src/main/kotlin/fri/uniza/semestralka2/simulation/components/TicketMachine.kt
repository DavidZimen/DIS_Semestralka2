package fri.uniza.semestralka2.simulation.components

import fri.uniza.semestralka2.core.Service
import fri.uniza.semestralka2.simulation.CompanyEventSimulation
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

    /**
     * Sets [agent]s state to [CustomerState.PRINTING_TICKET].
     */
    override fun onServingStart(agent: Customer) {
        agent.state = CustomerState.PRINTING_TICKET
    }

    /**
     * When ticket is printed, it sets [Customer.ticketTime] to current simulation time.
     */
    override fun onServingEnd(agent: Customer) {
        agent.ticketTime = core.simulationTime
    }

    /**
     * Sets state of the [agent] to to [CustomerState.IN_TICKET_QUEUE].
     */
    override fun onQueueAdd(agent: Customer) = with(agent) {
        ticketMachineQueueStartTime = core.simulationTime
        state = CustomerState.IN_TICKET_QUEUE
    }

    /**
     * Adds time of the agent in [queue] to [CompanyEventSimulation.replicationStats].
     */
    override fun onQueueRemove(agent: Customer) {
        core.replicationStats.ticketQueueTime.addEntry(core.simulationTime - agent.ticketMachineQueueStartTime)
    }

    /**
     * To all customers in [removedList] sets state to [CustomerState.LEFT_TICKET_MACHINE].
     * Also moves them to [CompanyEventSimulation.ticketMachineSink], so it is clear, they were not served.
     */
    override fun onRemovedElements(removedList: List<Customer>) {
        removedList.forEach {
            it.state = CustomerState.LEFT_TICKET_MACHINE
        }
        core.moveToTicketMachineSink(removedList)
    }
}