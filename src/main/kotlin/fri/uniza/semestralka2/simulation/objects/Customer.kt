package fri.uniza.semestralka2.simulation.objects

/**
 * Class representing customer of the company that holds necessary data.
 * @author David Zimen
 */
class Customer(probability: Double) {
    /**
     * Unique identifier of the customer.
     */
    val id = CustomerSequence.next

    /**
     * Type of the customer.
     */
    val type: CustomerType

    /**
     * Order type of the customer.
     */
    var orderType: OrderType? = null

    /**
     * Order size of the customer.
     */
    var orderSize: OrderSize? = null

    /**
     * Type of the customer payment.
     */
    var paymentType: PaymentType? = null

    /**
     * Cash desk where customer was waiting in queue.
     */
    var cashDeskQueue = ""

    /**
     * Cash desk where customer paid for his order.
     */
    var cashDeskPaid = ""

    // STATISTICS ATTRIBUTES
    /**
     * Time of the customers arrival to company.
     */
    var systemStartTime = 0.0

    /**
     * Time when customer entered queue to ticket generation.
     */
    var automatStartTime = 0.0

    /**
     * Time when customer entered area for waiting until being served.
     */
    var servicePlaceStartTime = 0.0

    /**
     * Time when customer entered queue to cash desk.
     */
    var cashDeskStartTime = 0.0

    init {
        type = CustomerType.retrieveType(probability)
    }
}
