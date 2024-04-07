package fri.uniza.semestralka2.simulation

import fri.uniza.novinovy_stanok.generator.ContinuousUniformGenerator
import fri.uniza.semestralka2.core.EventSimulationCore
import fri.uniza.semestralka2.core.EventSimulationState
import fri.uniza.semestralka2.general_utils.minutesToSeconds
import fri.uniza.semestralka2.general_utils.round
import fri.uniza.semestralka2.general_utils.toSeconds
import fri.uniza.semestralka2.generator.*
import fri.uniza.semestralka2.simulation.components.*
import fri.uniza.semestralka2.simulation.event.customer.CustomerArrivalEvent
import fri.uniza.semestralka2.simulation.objects.customer.*
import fri.uniza.semestralka2.simulation.objects.dto.*
import fri.uniza.semestralka2.simulation.objects.order.*
import fri.uniza.semestralka2.simulation.statistics.*
import fri.uniza.semestralka2.statistics.DiscreteStatistic
import java.math.RoundingMode
import java.time.LocalTime

/**
 * Implementation of Event driven simulation based on assignment of 'Semestralna praca 2'.
 * @author David Zimen
 */
class CompanyEventSimulation : EventSimulationCore() {

    // SERVICES COUNT
    /**
     * Number of [ServingDesk] in simulation.
     */
    var serviceDeskCount = 13
        set(value) {
            simulationRunningCheck()
            field = value
        }

    /**
     * Number of [CashDesk] in simulation.
     */
    var cashDeskCount = 4
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
     * Time when the ticket machine will no longer print new tickets,
     */
    var ticketMachineClosingTime = LocalTime.of(17, 0).toSeconds()
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
    lateinit var serviceDesks: List<ServingDesk>
        private set

    /**
     * Places where [Customer]s pay for their order.
     */
    lateinit var cashDesks: List<CashDesk>
        private set

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
     * [Customer]s, that were not served because [simulationTime] is passed [ticketMachineClosingTime].
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
        simulationState = CompanySimulationState()
        initOverallStats()
        initGenerators()
    }

    override fun beforeReplication() {
        simulationTime = openTime
        replicationStats.reset()
        CustomerSequence.reset()
        initServices()
        scheduleEvent(CustomerArrivalEvent(simulationTime + arrivalGenerator.sample().minutesToSeconds(), this))
    }

    override fun afterSimulation() {
        println(overallStats)
        updateSimulationState()
        simulationStateObservable.next(simulationState)
    }

    override fun afterReplication() {
        addToOverallStats()
    }

    // PUBLIC FUNCTIONS
    /**
     * Sets the new value of [openTime] to [time] transformed to seconds.
     */
    @Throws(IllegalStateException::class)
    fun setOpenTime(time: LocalTime) {
        simulationRunningCheck()
        val secTime = time.toSeconds()
        if (secTime > simulationEndTime || secTime > ticketMachineClosingTime) {
            throw IllegalStateException("Open time cannot be after closing time.")
        }
        openTime = secTime
    }

    /**
     * Sets the new value of [simulationEndTime] to [time] transformed to seconds.
     */
    @Throws(IllegalStateException::class)
    fun setClosingTime(time: LocalTime) {
        simulationRunningCheck()
        val secTime = time.toSeconds()
        if (secTime < openTime || secTime < ticketMachineClosingTime) {
            throw IllegalStateException("Open time cannot be after closing time.")
        }
        simulationEndTime = secTime
    }

    /**
     * Sets the new value of [ticketMachineClosingTime] to [time] transformed to seconds.
     */
    @Throws(IllegalStateException::class)
    fun setTicketMachineClosingTime(time: LocalTime) {
        simulationRunningCheck()
        val secTime = time.toSeconds()
        if (secTime < openTime || secTime > simulationEndTime) {
            throw IllegalStateException("Must be between open time and closing time.")
        }
        ticketMachineClosingTime = time.toSeconds()
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
    override fun updateSimulationState() {
        super.updateSimulationState()
        with(simulationState as CompanySimulationState) {
            customers = source.map { it.toDto() }
            employees = serviceDesks.map { it.toDto() } + cashDesks.map { it.toDto() } + ticketMachine.toDto()
            overallStats = this@CompanyEventSimulation.overallStats.toDto()
        }
    }

    /**
     * Adds all elements from [removedList] to [ticketMachineSink]
     * when [simulationTime] is after [ticketMachineClosingTime].
     */
    fun moveToTicketMachineSink(removedList: List<Customer>) = ticketMachineSink.addAll(removedList)

    /**
     * Add [customer] to [source] list of the simulation.
     */
    fun moveToSource(customer: Customer) = source.add(customer)

    /**
     * Add [customer] to [sink] when he reached [CustomerState.EXITED].
     */
    fun moveToSink(customer: Customer) = sink.add(customer)

    /**
     * Initializes [OverallStats] before simulation starts.
     */
    private fun initOverallStats(){
        overallStats.reset()
    }

    // PRIVATE FUNCTIONS
    /**
     * Initialized generator based on assignment values.
     */
    private fun initGenerators() {
        arrivalGenerator = ExponentialGenerator(1 / 2.0)
        ticketMachineGenerator = ContinuousUniformGenerator(30.0, 120.0)
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
        var onlineCount = (serviceDeskCount / 3.0).round(0, RoundingMode.FLOOR).toInt()
        var otherCount = serviceDeskCount - onlineCount
        if (onlineCount == 0) {
            onlineCount = 1
            otherCount--
        }
        val online = (1..onlineCount).toList().map {
            ServingDesk(
                "Service desk online $it",
                openTime,
                simulationEndTime,
                arrayOf(CustomerType.ONLINE),
                this
            )
        }
        val other = (1..otherCount).toList().map {
            ServingDesk(
                "Service desk $it",
                openTime,
                simulationEndTime,
                arrayOf(CustomerType.COMMON, CustomerType.CONTRACTED),
                this
            )
        }
        serviceDesks = online + other
        if (overallStats.serviceDesksWorkload.isEmpty()) {
            serviceDesks.forEach {
                overallStats.serviceDesksWorkload[it.name] = DiscreteStatistic()
            }
        }

        // service desks queue
        serviceDeskQueue = ServingDeskQueue(serviceDeskQueueMaxLength, openTime, this)

        // cash desks
        cashDesks = (1..cashDeskCount).toList().map {
            CashDesk(
                "Cash desk $it",
                openTime,
                simulationEndTime,
                this
            )
        }
        if (overallStats.cashDesksStats.isEmpty()) {
            cashDesks.forEach {
                overallStats.cashDesksStats[it.name] = DiscreteStatistic() to DiscreteStatistic()
            }
        }

        // ticket machine
        ticketMachine = TicketMachine("Ticket machine", openTime, ticketMachineClosingTime, this)

        // source and sinks
        source.clear()
        sink.clear()
        ticketMachineSink.clear()
    }

    /**
     * Adds single replication statistic into [overallStats].
     */
    private fun addToOverallStats() = with(overallStats) {
        systemTime.addEntry(replicationStats.systemTime.mean)
        ticketQueueTime.addEntry(replicationStats.ticketQueueTime.mean)
        ticketQueueLength.addEntry(ticketMachine.queueStats.mean)
        ticketMachineWorkload.addEntry(ticketMachine.workload.averageWorkload)
        serviceQueueLength.addEntry(serviceDeskQueue.stats.mean)
        cashDeskQueueTime.addEntry(replicationStats.cashDeskQueueTime.mean)
        lastCustomerExit.addEntry(replicationStats.lastCustomerExit)
        customersServed.addEntry(replicationStats.customersServed)
        cashDesks.forEach {
            cashDesksStats[it.name]?.first?.addEntry(it.workload.averageWorkload)
            cashDesksStats[it.name]?.second?.addEntry(it.queueStats.mean)
        }
        serviceDesks.forEach {
            serviceDesksWorkload[it.name]?.addEntry(it.workload.averageWorkload)
        }
    }

    class CompanySimulationState : EventSimulationState() {
        lateinit var customers: List<CustomerDto>
        lateinit var employees: List<ServiceDto>
        lateinit var overallStats: OverallStatsDto
    }
}
