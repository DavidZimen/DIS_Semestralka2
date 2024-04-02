package fri.uniza.semestralka2.simulation

import fri.uniza.novinovy_stanok.generator.ContinuousUniformGenerator
import fri.uniza.semestralka2.general_utils.minutesToSeconds
import fri.uniza.semestralka2.general_utils.round
import fri.uniza.semestralka2.general_utils.toSeconds
import fri.uniza.semestralka2.generator.*
import fri.uniza.semestralka2.simulation.components.CashDesk
import fri.uniza.semestralka2.simulation.components.ServingDesk
import fri.uniza.semestralka2.simulation.components.ServingDeskQueue
import fri.uniza.semestralka2.simulation.components.TicketMachine
import fri.uniza.semestralka2.simulation.core.EventSimulationCore
import fri.uniza.semestralka2.simulation.core.EventSimulationState
import fri.uniza.semestralka2.simulation.event.customer.CustomerArrivalEvent
import fri.uniza.semestralka2.simulation.objects.*
import fri.uniza.semestralka2.simulation.objects.customer.Customer
import fri.uniza.semestralka2.simulation.objects.customer.CustomerDto
import fri.uniza.semestralka2.simulation.objects.customer.CustomerType
import fri.uniza.semestralka2.simulation.objects.order.OrderType
import fri.uniza.semestralka2.simulation.objects.order.PaymentType
import fri.uniza.semestralka2.simulation.statistics.OverallStats
import fri.uniza.semestralka2.simulation.statistics.ReplicationStats
import java.math.RoundingMode
import java.time.LocalTime

/**
 * Implementation of Event driven simulation based on assignment of 'Semestralna praca 2'.
 * @author David Zimen
 */
class CompanyEventSimulation : EventSimulationCore() {

    // SERVICES COUNT
    /**
     * Number of [ServiceDesk] in simulation.
     */
    var serviceDeskCount = 15
        set(value) {
            simulationRunningCheck()
            field = value
        }

    /**
     * Number of [CashDesk] in simulation.
     */
    var cashDeskCount = 6
        set(value) {
            simulationRunningCheck()
            field = value
        }

    /**
     * Maximum length for [serviceDeskQueue]
     */
    var serviceDeskQueueMaxLength = 9
        set(value) {
            simulationRunningCheck()
            field = value
        }

    // TIME ATTRIBUTES
    /**
     * Start of the simulation as defined by the user.
     */
    var openTime = LocalTime.of(9, 0).toSeconds()
        private set

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
    lateinit var ticketMachineGenerator: Generator
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
     */
    lateinit var cashDeskChooseGenerator: Generator
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

    // SERVICES
    /**
     * Queue for [Customer]s for waiting until being served at one of [serviceDesks].
     */
    lateinit var serviceDeskQueue: ServingDeskQueue
        private set

    /**
     * Machine to print order tickets for [Customer]s.
     */
    lateinit var ticketMachine: TicketMachine
        private set

    /**
     * Places where [Customer] dictate and receives his order.
     */
    val serviceDesks = mutableListOf<ServingDesk>()

    /**
     * Places where [Customer]s pay for their order.
     */
    val cashDesks = mutableListOf<CashDesk>()

    // SOURCE and SINKS
    /**
     * [Customer]s that arrived in the company.
     */
    private var source = mutableListOf<Customer>()

    /**
     * Served and left [Customer]s.
     */
    private var sink = mutableListOf<Customer>()

    /**
     * [Customer]s, that were not served because [simulationTime] is passed [automatClosingTime].
     */
    private var ticketMachineSink = mutableListOf<Customer>()

    //STATISTICS
    /**
     * Statistics for single replications.
     */
    val replicationStats = ReplicationStats()

    /**
     * Cumulative statistics for all replications.
     */
    private val overallStats = OverallStats()

    // OVERRIDE FUNCTIONS
    override fun beforeSimulation() {
        overallStats.reset()
        simulationState = CompanySimulationState()
        initGenerators()
    }

    override fun beforeReplication() {
        simulationTime = openTime
        replicationStats.reset()
        initServices()
        scheduleEvent(CustomerArrivalEvent(simulationTime + arrivalGenerator.sample().minutesToSeconds(), this))
    }

    override fun afterSimulation() {
        println("Replications: $replicationsExecuted")
        println(overallStats)
    }

    override fun afterReplication() {
        addToOverallStats()
        println(replicationsExecuted)
    }

    // PUBLIC FUNCTIONS
    /**
     * Sets the new value of [openTime] to [time] transformed to seconds.
     */
    fun setOpenTime(time: LocalTime) {
        simulationRunningCheck()
        openTime = time.toSeconds()
    }

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

    /**
     * Updates [simulationState] with new values.
     */
    fun updateState() {
        with(simulationState as CompanySimulationState) {
            customers = source.map { it.toDto() }
        }
    }

    /**
     * Adds all elements from [removedList] to [ticketMachineSink]
     */
    fun moveToTicketMachineSink(removedList: List<Customer>) = ticketMachineSink.addAll(removedList)

    fun moveToSource(customer: Customer) = source.add(customer)

    fun moveToSink(customer: Customer) = sink.add(customer)

    // PRIVATE FUNCTIONS
    /**
     * Initialized generator based on assignment values.
     */
    private fun initGenerators() {
        arrivalGenerator = ExponentialGenerator(1 / 2.0)
        ticketMachineGenerator = ContinuousUniformGenerator(30.0, 180.0)
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
        cashDeskChooseGenerator = ContinuousUniformGenerator()
    }

    /**
     * Initializes services based on provided [serviceDeskCount], [cashDeskCount] and [serviceDeskQueueMaxLength].
     */
    private fun initServices() {
        // service desks
        serviceDesks.clear()
        val online = (serviceDeskCount / 3.0).round(0, RoundingMode.FLOOR).toInt()
        val other = serviceDeskCount - online
        for (i in 0 until online) {
            serviceDesks.add(ServingDesk("Service desk online ${i + 1}", arrayOf(CustomerType.ONLINE), this))
        }
        for (i in 0 until other) {
            serviceDesks.add(ServingDesk("Service desk ${i + 1}", arrayOf(CustomerType.COMMON, CustomerType.CONTRACTED), this))
        }

        // cash desks
        cashDesks.clear()
        for (i in 0 until cashDeskCount) {
            cashDesks.add(CashDesk("Cash desk ${i + 1}", this))
        }

        // service desks queue
        serviceDeskQueue = ServingDeskQueue(serviceDeskQueueMaxLength, this)

        // ticket machine
        ticketMachine = TicketMachine("Ticket machine", this)

        // source and sinks
        source.clear()
        sink.clear()
        ticketMachineSink.clear()
    }

    /**
     * Adds single replication statistic into [overallStats].
     */
    private fun addToOverallStats() {
        overallStats.systemTime.addEntry(replicationStats.systemTime.mean)
        overallStats.ticketQueueTime.addEntry(replicationStats.ticketQueueTime.mean)
        overallStats.lastCustomerExit.addEntry(replicationStats.lastCustomerExit)
        overallStats.customersServed.addEntry(replicationStats.customersServed)
    }

    inner class CompanySimulationState : EventSimulationState() {
        var customers: List<CustomerDto> = emptyList()
    }
}
