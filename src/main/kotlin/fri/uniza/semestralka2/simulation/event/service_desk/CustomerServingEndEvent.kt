package fri.uniza.semestralka2.simulation.event.service_desk

import fri.uniza.semestralka2.simulation.CompanyEventSimulation
import fri.uniza.semestralka2.simulation.event.CompanyEvent
import fri.uniza.semestralka2.simulation.objects.customer.Customer
import fri.uniza.semestralka2.simulation.objects.order.OrderSize

class CustomerServingEndEvent(
    time: Double,
    private val customer: Customer,
    core: CompanyEventSimulation
) : CompanyEvent(time, core) {

    override fun onExecute() {
        if (customer.orderSize == OrderSize.BIG) {
            core.scheduleEvent(DeskOccupationStartEvent(time, customer, core))
        } else {
            core.scheduleEvent(DeskOccupationEndEvent(time, customer, core))
        }
    }
}