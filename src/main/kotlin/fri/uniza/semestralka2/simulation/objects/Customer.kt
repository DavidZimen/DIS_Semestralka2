package fri.uniza.semestralka2.simulation.objects

import fri.uniza.semestralka2.general_utils.isALessThanB

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
        type = retrieveType(probability)
    }

    /**
     * Returns [CustomerType] based on generated [probability].
     */
    private fun retrieveType(probability: Double): CustomerType {
        return when {
            isALessThanB(probability,0.5) -> CustomerType.COMMON
            isALessThanB(probability,0.65) -> CustomerType.CONTRACTED
            else -> CustomerType.ONLINE
        }
    }
}
