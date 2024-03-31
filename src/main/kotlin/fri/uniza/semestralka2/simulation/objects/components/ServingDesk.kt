package fri.uniza.semestralka2.simulation.objects.components

import fri.uniza.semestralka2.simulation.CompanyEventSimulation
import fri.uniza.semestralka2.simulation.core.Service
import fri.uniza.semestralka2.simulation.objects.customer.Customer
import fri.uniza.semestralka2.simulation.objects.customer.CustomerState
import fri.uniza.semestralka2.simulation.objects.customer.CustomerType

/**
 * @author David Zimen
 */
class ServingDesk(
    name: String,
    val allowed: Array<CustomerType>,
    override val core: CompanyEventSimulation
) : Service<Customer>(name, core) {

    override fun onServingStart(agent: Customer) {
        canBeServed(agent)
        agent.state = CustomerState.BEING_SERVED
    }

    @Throws(IllegalStateException::class)
    private fun canBeServed(customer: Customer) {
        if (!allowed.contains(customer.type)) {
            throw IllegalStateException("Customer type ${customer.type} can not be served at desk $name")
        }
    }
}