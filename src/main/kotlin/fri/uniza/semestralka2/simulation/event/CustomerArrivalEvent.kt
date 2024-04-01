package fri.uniza.semestralka2.simulation.event

import fri.uniza.semestralka2.general_utils.isALessThanB
import fri.uniza.semestralka2.simulation.CompanyEventSimulation
import fri.uniza.semestralka2.simulation.objects.customer.Customer

/**
 * Generates new customer for [CompanyEventSimulation].
 * Schedules new [CustomerArrivalEvent] if [CompanyEventSimulation.automatClosingTime] is not exceeded.
 * Schedules [TicketEventStartEvent] or adds [Customer] to [CompanyEventSimulation.ticketMachine] queue.
 */
class CustomerArrivalEvent(time: Double, core: CompanyEventSimulation) : CompanyEvent(time, core) {
    override fun onExecute() {
        val typeProb = core.customerTypeGenerator.sample()
        val customer = Customer(typeProb).apply { systemStartTime = time }

        val ticketMachine = core.ticketMachine
        if (ticketMachine.isOccupied || !core.serviceDeskQueue.canAdd()) {
            ticketMachine.addToQueue(customer)
        } else {
            core.scheduleEvent(PrintTicketStartEvent(time, customer, core))
        }

        // schedule new arrival if ticket machine is operating
        val arrivalTime = time + core.arrivalGenerator.sample()
        if (isALessThanB(arrivalTime, core.automatClosingTime)) {
            core.scheduleEvent(CustomerArrivalEvent(arrivalTime, core))
        }
    }
}