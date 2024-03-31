package fri.uniza.semestralka2.simulation

import fri.uniza.novinovy_stanok.generator.ContinuousUniformGenerator
import fri.uniza.semestralka2.general_utils.minutesToSeconds
import fri.uniza.semestralka2.general_utils.toSeconds
import fri.uniza.semestralka2.generator.*
import fri.uniza.semestralka2.simulation.core.EventSimulationCore
import fri.uniza.semestralka2.simulation.objects.*
import fri.uniza.semestralka2.simulation.objects.order.OrderType
import fri.uniza.semestralka2.simulation.objects.order.PaymentType
import java.time.LocalTime

/**
 * Implementation of Event driven simulation based on assignment of 'Semestralna praca 2'.
 * @author David Zimen
 */
class CompanyEventSimulation : EventSimulationCore() {

    // TIME ATTRIBUTES
    /**
     * End of the simulation as defined by the user.
     */
    var closingTime = LocalTime.of(17, 30).toSeconds()
        private set

    /**
     * Time when the ticket machine will no longer print new tickets,
     */
    var automatClosingTime = LocalTime.of(17, 0).toSeconds()
        private set

    // GENERATORS
    /**
     * [Generator] for customers arrivals to company.
     */
    lateinit var arrivalGenerator: Generator
        private set

    /**
     * [Generator] for time it takes the customer
     * Instance of [ExponentialGenerator] with lambda set to 1 / 2 minutes.
     * @return Random value in seconds.
     */
    lateinit var automatTimeGenerator: Generator
        private set

    /**
     * [Generator] for values in <0; 1) interval.
     * Call [CustomerType.retrieveType] with generated value.
     */
    lateinit var customerTypeGenerator: Generator
        private set

    /**
     * [Generator] for time it takes customer to dictate order at service desk.
     * Instance of [ContinuousUniformGenerator] on interval 480 +- 420 seconds.
     * @return Random value in seconds.
     */
    lateinit var orderDictationGenerator: Generator
        private set

    /**
     * [Generator] for values in <0; 1) interval.
     * Call [OrderSize.retrieveOrderSize] with generated value.
     */
    lateinit var orderSizeGenerator: Generator
        private set

    /**
     * [Generator] for time it takes [Customer] to retrieve his [OrderSize.BIG] order
     * after paying it at the cash desk.
     */
    lateinit var orderRetrievalTimeGenerator: Generator
        private set

    /**
     * [Generator] for values in <0; 1) interval.
     * Call [OrderType.retrieveOrderType] with generated value.
     */
    lateinit var orderTypeGenerator: Generator
        private set

    /**
     * Map of [Generator]s for each [OrderType].
     * Generated value represent how long will order creation take the employee.
     */
    private val orderCreationTimeGenerators = mutableMapOf<OrderType, Generator>()

    /**
     * [Generator] for time it takes for the employee to handover [CustomerType.ONLINE] order.
     */
    lateinit var onlineHandoverTimeGenerator: Generator
        private set

    /**
     * [Generator] for values in <0; 1) interval.
     * Call [PaymentType.retrievePaymentType] with generated value.
     */
    lateinit var paymentTypeGenerator: Generator
        private set

    /**
     * Map of [Generator]s, where [PaymentType] is the key.
     */
    private val paymentTimeGenerators = mutableMapOf<PaymentType, Generator>()

    // OVERRIDE FUNCTIONS
    override fun beforeSimulation() {
        initGenerators()
    }

    // PUBLIC FUNCTIONS
    /**
     * Sets the new value of [closingTime] to [time] transformed to seconds.
     */
    fun setClosingTime(time: LocalTime) {
        simulationRunningCheck()
        closingTime = time.toSeconds()
    }

    /**
     * Sets the new value of [automatClosingTime] to [time] transformed to seconds.
     */
    fun setAutomatClosingTime(time: LocalTime) {
        simulationRunningCheck()
        automatClosingTime = time.toSeconds()
    }

    /**
     * @return [Generator] from [orderCreationTimeGenerators] based on [type] parameter.
     */
    fun getOrderCreationGenerator(type: OrderType) = orderCreationTimeGenerators[type]!!

    /**
     * @return [Generator] from [paymentTimeGenerators] based on [type] parameter.
     */
    fun getPaymentTimeGenerator(type: PaymentType) = paymentTimeGenerators[type]!!

    // PRIVATE FUNCTIONS
    /**
     * Initialized generator based on assignment values.
     */
    private fun initGenerators() {
        arrivalGenerator = ExponentialGenerator(1 / 2.0.minutesToSeconds())
        automatTimeGenerator = ContinuousUniformGenerator(30.0, 180.0)
        orderDictationGenerator = ContinuousUniformGenerator(60.0, 900.0)
        orderRetrievalTimeGenerator = ContinuousUniformGenerator(30.0, 70.0)
        onlineHandoverTimeGenerator = TriangularDistribution(60.0, 480.0, 120.0)

        // generators in maps
        paymentTimeGenerators[PaymentType.CASH] = DiscreteUniformGenerator(180, 480)
        paymentTimeGenerators[PaymentType.CARD] = DiscreteUniformGenerator(180, 360)
        orderCreationTimeGenerators[OrderType.SIMPLE] = ContinuousEmpiricalGenerator(
            IntervalProbability(2.0, 5.0, 0.6),
            IntervalProbability(5.0, 9.0, 0.4)
        )
        orderCreationTimeGenerators[OrderType.SLIGHTLY_COMPLEX] = ContinuousUniformGenerator(9.0, 11.0)
        orderCreationTimeGenerators[OrderType.COMPLEX] = ContinuousEmpiricalGenerator(
            IntervalProbability(11.0, 12.0, 0.1),
            IntervalProbability(12.0, 20.0, 0.6),
            IntervalProbability(20.0, 25.0, 0.3)
        )

        // <0; 1) generators
        customerTypeGenerator = ContinuousUniformGenerator()
        orderTypeGenerator = ContinuousUniformGenerator()
        orderSizeGenerator = ContinuousUniformGenerator()
        paymentTypeGenerator = ContinuousUniformGenerator()
    }
}