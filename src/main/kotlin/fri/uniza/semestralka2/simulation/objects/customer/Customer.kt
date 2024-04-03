package fri.uniza.semestralka2.simulation.objects.customer

import fri.uniza.semestralka2.simulation.CompanyEventSimulation
import fri.uniza.semestralka2.simulation.components.CashDesk
import fri.uniza.semestralka2.simulation.components.ServingDesk
import fri.uniza.semestralka2.simulation.objects.order.OrderSize
import fri.uniza.semestralka2.simulation.objects.order.OrderType
import fri.uniza.semestralka2.simulation.objects.order.PaymentType

/**
 * Class representing customer of the company that holds necessary data.
 * @author David Zimen
 */
class Customer(core: CompanyEventSimulation) {
    /**
     * Unique identifier of the customer.
     */
    val id = CustomerSequence.next

    /**
     * Time printed on the ticket by machine.
     */
    var ticketTime = -1.0

    /**
     * State of the customer in the simulation.
     */
    var state = CustomerState.ARRIVED

    /**
     * Type of the customer.
     */
    val type: CustomerType = CustomerType.retrieveType(core.customerTypeGenerator.sample())


    /**
     * Order type of the customer.
     */
    val orderType = OrderType.retrieveOrderType(core.orderTypeGenerator.sample())

    /**
     * Order size of the customer.
     */
    val orderSize = OrderSize.retrieveOrderSize(core.orderSizeGenerator.sample())

    /**
     * Type of the customer payment.
     */
    val paymentType = PaymentType.retrievePaymentType(core.paymentTypeGenerator.sample())

    /**
     * Cash desk where customer was waiting in queue.
     */
    var cashDeskQueue: CashDesk? = null

    /**
     * Cash desk where customer paid for his order.
     */
    var cashDeskPaid: CashDesk? = null

    /**
     * Service desk where customer receives his order.
     */
    var serviceDesk: ServingDesk? = null

    // STATISTICS ATTRIBUTES
    /**
     * Time of the customers arrival to company.
     */
    var systemStartTime = core.simulationTime

    /**
     * Time when customer entered queue to ticket generation.
     */
    var ticketMachineQueueStartTime = -1.0

    /**
     * Time when customer entered area for waiting until being served.
     */
    var servingDeskQueueStartTime = -1.0

    /**
     * Time when customer entered queue to cash desk.
     */
    var cashDeskQueueStartTime = -1.0
}
