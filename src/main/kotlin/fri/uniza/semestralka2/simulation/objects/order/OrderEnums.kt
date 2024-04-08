package fri.uniza.semestralka2.simulation.objects.order

import fri.uniza.semestralka2.general_utils.isALessThanB
import fri.uniza.semestralka2.simulation.objects.customer.Customer

/**
 * Type of the order in company.
 */
enum class OrderType {
    /**
     * Generated with probability of 0.3
     */
    SIMPLE,
    /**
     * Generated with probability of 0.4
     */
    SLIGHTLY_COMPLEX,
    /**
     * Generated with probability of 0.3
     */
    COMPLEX;

    companion object {
        /**
         * @return [OrderType] based on generated [probability].
         */
        fun retrieveOrderType(probability: Double): OrderType {
            return when {
                isALessThanB(probability, 0.3) -> SIMPLE
                isALessThanB(probability, 0.7) -> SLIGHTLY_COMPLEX
                else -> COMPLEX
            }
        }
    }
}

/**
 * Size of the order created by [Customer].
 */
enum class OrderSize {
    /**
     * [Customer] with this order size can go to cash desk with his order.
     */
    NORMAL,
    /**
     * [Customer] has to left his order at service desk and return for order after paying.
     */
    BIG;

    companion object {
        /**
         * @return [OrderSize] based on generated [probability].
         */
        fun retrieveOrderSize(probability: Double): OrderSize {
            return when {
                isALessThanB(probability, 0.6) -> BIG
                else -> NORMAL
            }
        }
    }
}

/**
 * Type of the payment.
 */
enum class PaymentType {
    CARD,
    CASH;

    companion object {
        /**
         * @return [PaymentType] based on generated [probability].
         */
        fun retrievePaymentType(probability: Double): PaymentType {
            return when {
                isALessThanB(probability, 0.4) -> CASH
                else -> CARD
            }
        }
    }
}