package fri.uniza.semestralka2.simulation.components

import fri.uniza.semestralka2.simulation.CompanyEventSimulation
import fri.uniza.semestralka2.simulation.core.Service
import fri.uniza.semestralka2.simulation.objects.customer.Customer
import fri.uniza.semestralka2.simulation.objects.customer.CustomerState
import fri.uniza.semestralka2.simulation.objects.customer.CustomerType
import fri.uniza.semestralka2.simulation.objects.order.OrderSize
import fri.uniza.semestralka2.simulation.objects.order.OrderType

/**
 * @author David Zimen
 */
class ServingDesk(
    name: String,
    val allowed: Array<CustomerType>,
    override val core: CompanyEventSimulation
) : Service<Customer>(name, core) {

    override fun onServingStart(agent: Customer) {
        with(agent) {
            if (!canBeServed(agent)) {
                throw IllegalStateException("Customer type ${agent.type} can not be served at desk $name")
            }
            canBeServed(this)
            state = CustomerState.BEING_SERVED
            orderType = OrderType.retrieveOrderType(core.orderTypeGenerator.sample())
            orderSize = OrderSize.retrieveOrderSize(core.orderSizeGenerator.sample())
            serviceDesk = this@ServingDesk
        }
    }

    @Throws(IllegalStateException::class)
    fun canBeServed(customer: Customer): Boolean {
        return !(!allowed.contains(customer.type) || isOccupied)
    }
}