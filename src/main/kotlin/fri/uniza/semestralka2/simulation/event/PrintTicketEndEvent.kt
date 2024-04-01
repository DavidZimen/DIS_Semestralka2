package fri.uniza.semestralka2.simulation.event

import fri.uniza.semestralka2.simulation.CompanyEventSimulation
import fri.uniza.semestralka2.simulation.objects.customer.Customer

/**
 * Finishes printing ticket for the [customer].
 * Schedules [PrintTicketStartEvent] if some [Customer] is in its queue.
 * @author David Zimen
 */
class PrintTicketEndEvent(
    time: Double,
    private val customer: Customer,
    core: CompanyEventSimulation
) : CompanyEvent(time, core) {

    override fun onExecute() {
        with(core.ticketMachine) {
            finishServing(customer)

            // schedule new ticket print if queue is not empty and service queue is not full
            if (!isQueueEmpty && core.serviceDeskQueue.canAdd()) {
                core.scheduleEvent(PrintTicketStartEvent(time, null, core))
            }

            // TODO plan for customer to go to queue
        }
    }
}