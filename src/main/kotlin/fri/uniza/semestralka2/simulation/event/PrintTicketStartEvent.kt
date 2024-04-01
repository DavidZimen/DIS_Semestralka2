package fri.uniza.semestralka2.simulation.event

import fri.uniza.semestralka2.simulation.CompanyEventSimulation
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

    //TODO logic to empty queue when past its time

    override fun onExecute() {
        if (customer != null) {
            startTicketPrinting(customer)
        } else {
            core.ticketMachine.removeFromQueue()?.let { startTicketPrinting(it) }
        }
    }

    private fun startTicketPrinting(customer: Customer) {
        core.ticketMachine.startServing(customer)
        val endTime = time + core.automatTimeGenerator.sample()
        core.scheduleEvent(PrintTicketEndEvent(endTime, customer, core))
    }
}