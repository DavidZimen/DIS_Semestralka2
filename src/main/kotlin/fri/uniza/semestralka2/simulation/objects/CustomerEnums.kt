package fri.uniza.semestralka2.simulation.objects

/**
 * Types of the [Customer] visiting company.
 * @author David Zimen
 */
enum class CustomerType {
    /**
     * Common type with no benefits.
     */
    COMMON,
    /**
     * Type with preferential treatment to [COMMON] customer.
     */
    CONTRACTED,
    /**
     * Customer with own service place.
     */
    ONLINE;
}

/**
 * State of the [Customer] in the system.
 */
enum class CustomerState {
    /**
     * Customer is waiting in ticket machine queue.
     */
    WAITING_FOR_TICKET,
    /**
     * Customer is at ticket machine, choosing his type.
     */
    PRINTING_TICKET,
    /**
     * Ticket was printed, now in queue waiting to place his order.
     */
    WAITING_FOR_SERVING,
    /**
     * Customer is requesting and waiting for his order at serving desk.
     */
    BEING_SERVED,
    /**
     * Customer was served and is now in queue to cash desk.
     */
    WAITING_FOR_PAY,
    /**
     * Currently at the cash desk paying for his order.
     */
    PAYING,
    /**
     * When customer had big order and had to return to serving desk to pick up his order.
     */
    PICKING_UP,
    /**
     * Customer left the company.
     */
    EXITED;
}