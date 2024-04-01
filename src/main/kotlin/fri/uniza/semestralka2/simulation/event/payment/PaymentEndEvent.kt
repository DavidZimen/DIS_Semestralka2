package fri.uniza.semestralka2.simulation.event.payment

import fri.uniza.semestralka2.simulation.CompanyEventSimulation
import fri.uniza.semestralka2.simulation.event.CompanyEvent
import fri.uniza.semestralka2.simulation.event.customer.CustomerExitEvent
import fri.uniza.semestralka2.simulation.event.service_desk.DeskOccupationEndEvent
import fri.uniza.semestralka2.simulation.objects.customer.Customer
import fri.uniza.semestralka2.simulation.objects.customer.CustomerState
import fri.uniza.semestralka2.simulation.objects.order.OrderSize

class PaymentEndEvent(
    time: Double,
    private val customer: Customer,
    core: CompanyEventSimulation
) : CompanyEvent(time, core) {

    override fun onExecute() {
        if (customer.cashDeskPaid == null) {
            throw IllegalStateException("Customer has to have Cash desk assigned.")
        }

        // finish serving and schedule next customer
        customer.cashDeskPaid?.finishServing(customer)
        core.scheduleEvent(PaymentStartEvent(time, customer.cashDeskPaid!!, null, core))

        if (customer.orderSize == OrderSize.BIG) {
            val pickingEndTime = time + core.orderRetrievalTimeGenerator.sample()
            customer.state = CustomerState.PICKING_UP
            core.scheduleEvent(DeskOccupationEndEvent(pickingEndTime, customer, core))
        } else {
            core.scheduleEvent(CustomerExitEvent(time, customer, core))
        }
    }
}