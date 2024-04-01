package fri.uniza.semestralka2.simulation.event.service_desk

import fri.uniza.semestralka2.simulation.CompanyEventSimulation
import fri.uniza.semestralka2.simulation.event.payment.PaymentStartEvent
import fri.uniza.semestralka2.simulation.objects.customer.Customer

class DeskOccupationStartEvent(
    time: Double,
    customer: Customer,
    core: CompanyEventSimulation
) : DeskOccupationEvent(time, customer, core) {

    override fun onExecute() {
        chooseCashDesk()?.let {
            core.scheduleEvent(PaymentStartEvent(time, it, customer, core))
        }
    }
}