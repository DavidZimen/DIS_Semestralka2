package fri.uniza.semestralka2.simulation.components

import fri.uniza.semestralka2.core.Service
import fri.uniza.semestralka2.simulation.CompanyEventSimulation
import fri.uniza.semestralka2.simulation.objects.customer.Customer
import fri.uniza.semestralka2.simulation.objects.customer.CustomerState
import fri.uniza.semestralka2.simulation.objects.customer.CustomerType

/**
 * @author David Zimen
 */
class ServingDesk(
    name: String,
    servingStart: Double,
    servingEnd: Double,
    val allowed: Array<CustomerType>,
    override val core: CompanyEventSimulation
) : Service<Customer>(name,servingStart, servingEnd, core) {

    override fun onServingStart(agent: Customer) {
        with(agent) {
            if (!canBeServed(agent)) {
                throw IllegalStateException("Customer type ${agent.type} can not be served at desk $name")
            }
            canBeServed(this)
            state = CustomerState.BEING_SERVED
            serviceDesk = this@ServingDesk
        }
    }

    @Throws(IllegalStateException::class)
    fun canBeServed(customer: Customer): Boolean {
        return !(!allowed.contains(customer.type) || isOccupied)
    }
}