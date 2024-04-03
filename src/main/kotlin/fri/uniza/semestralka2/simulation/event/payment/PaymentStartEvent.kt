package fri.uniza.semestralka2.simulation.event.payment

import fri.uniza.semestralka2.simulation.CompanyEventSimulation
import fri.uniza.semestralka2.simulation.components.CashDesk
import fri.uniza.semestralka2.simulation.event.CompanyEvent
import fri.uniza.semestralka2.simulation.objects.customer.Customer

class PaymentStartEvent(
    time: Double,
    private val cashDesk: CashDesk,
    private val customer: Customer?,
    core: CompanyEventSimulation,
) : CompanyEvent(time, core) {

    override fun onExecute() {
        // retrieve customer and generate payment type
        val finalCustomer = customer ?: cashDesk.removeFromQueue() ?: return
        cashDesk.startServing(finalCustomer)

        // schedule end for cash desk
        val endTime = time + core.getPaymentTimeGenerator(finalCustomer.paymentType!!).sample()
        core.scheduleEvent(PaymentEndEvent(endTime, finalCustomer, core))
    }
}