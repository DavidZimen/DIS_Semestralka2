package fri.uniza.semestralka2.simulation.event.service_desk

import fri.uniza.semestralka2.general_utils.minutesToSeconds
import fri.uniza.semestralka2.simulation.CompanyEventSimulation
import fri.uniza.semestralka2.simulation.components.ServingDesk
import fri.uniza.semestralka2.simulation.event.CompanyEvent
import fri.uniza.semestralka2.simulation.event.ticket.PrintTicketStartEvent
import fri.uniza.semestralka2.simulation.objects.customer.CustomerType

class CustomerServingStartEvent(
    time: Double,
    private val servingDesk: ServingDesk,
    core: CompanyEventSimulation
) : CompanyEvent(time, core) {

    override fun onExecute() {
        val isAtMaxCapacity = core.serviceDeskQueue.isAtMaximumCapacity()
        val customer = core.serviceDeskQueue.remove(servingDesk.allowed[0]) ?: return

        servingDesk.startServing(customer)
        val (orderDictation, orderCreation) = if (customer.type == CustomerType.ONLINE) {
            0.0 to core.onlineHandoverTimeGenerator.sample()
        } else {
            core.orderDictationGenerator.sample() to core.getOrderCreationGenerator(customer.orderType!!).sample().minutesToSeconds()
        }

        // schedule serving end
        val endTime = time + orderDictation + orderCreation
        core.scheduleEvent(CustomerServingEndEvent(endTime, customer, core))

        // schedule ticket printing if queue is full before serving customer
        if (isAtMaxCapacity && !core.ticketMachine.isQueueEmpty && !core.ticketMachine.isOccupied) {
            core.scheduleEvent(PrintTicketStartEvent(time, null, core))
        }
    }
}