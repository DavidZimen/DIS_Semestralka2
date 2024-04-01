package fri.uniza.semestralka2.simulation.event.ticket

import fri.uniza.semestralka2.general_utils.isAGreaterThanB
import fri.uniza.semestralka2.simulation.CompanyEventSimulation
import fri.uniza.semestralka2.simulation.event.CompanyEvent
import fri.uniza.semestralka2.simulation.objects.customer.Customer

/**
 * Represents [CompanyEventSimulation.ticketMachine] starting to serve [customer].
 * If [customer] is null, then it takes one from its queue.
 * @author David Zimen
 */
class PrintTicketStartEvent(
    time: Double,
    private val customer: Customer?,
    core: CompanyEventSimulation
) : CompanyEvent(time, core) {

    override fun onExecute() {
        if (customer != null) {
            startTicketPrinting(customer)
        } else {
            core.ticketMachine.removeFromQueue()?.let { startTicketPrinting(it) }
        }
    }

    private fun startTicketPrinting(customer: Customer) {
        val endTime = time + core.ticketMachineGenerator.sample()
        if (isAGreaterThanB(endTime, core.automatClosingTime)) {
            core.ticketMachine.removeAll()
        } else {
            core.ticketMachine.startServing(customer)
            core.scheduleEvent(PrintTicketEndEvent(endTime, customer, core))
        }
    }
}