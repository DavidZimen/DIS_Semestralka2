package fri.uniza.semestralka2.simulation.event.customer

import fri.uniza.semestralka2.simulation.CompanyEventSimulation
import fri.uniza.semestralka2.simulation.event.CompanyEvent
import fri.uniza.semestralka2.simulation.objects.customer.Customer
import fri.uniza.semestralka2.simulation.objects.customer.CustomerState

class CustomerExitEvent(
    time: Double,
    private val customer: Customer,
    core: CompanyEventSimulation
) : CompanyEvent(time, core) {

    override fun onExecute() {
        customer.state = CustomerState.EXITED
        core.replicationStats.systemTime.addEntry(time - customer.systemStartTime)
        core.replicationStats.lastCustomerExit = time
        core.replicationStats.customersServed++
        core.moveToSink(customer)
    }
}