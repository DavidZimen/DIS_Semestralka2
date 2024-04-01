package fri.uniza.semestralka2.simulation.event.service_desk

import fri.uniza.semestralka2.simulation.CompanyEventSimulation
import fri.uniza.semestralka2.simulation.event.customer.CustomerExitEvent
import fri.uniza.semestralka2.simulation.event.payment.PaymentStartEvent
import fri.uniza.semestralka2.simulation.objects.customer.Customer
import fri.uniza.semestralka2.simulation.objects.order.OrderSize

class DeskOccupationEndEvent(
    time: Double,
    customer: Customer,
    core: CompanyEventSimulation
) : DeskOccupationEvent(time, customer, core) {

    override fun onExecute() {
        if (PaymentStartEvent.COUNT == 308) {
            println("Error")
        }

        if (customer.serviceDesk == null) {
            throw IllegalStateException("Customer has to have Service desk assigned.")
        }

        // finish serving and schedule event to take new customer
        customer.serviceDesk!!.finishServing(customer)
        core.scheduleEvent(CustomerServingStartEvent(time, customer.serviceDesk!!, core))

        // either exit or go to cash desk
        if (customer.orderSize == OrderSize.BIG) {
            core.scheduleEvent(CustomerExitEvent(time, customer, core))
        } else {
            chooseCashDesk()?.let {
                core.scheduleEvent(PaymentStartEvent(time, it, customer, core))
            }
        }
    }
}