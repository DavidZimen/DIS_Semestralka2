package fri.uniza.semestralka2.simulation.statistics

import fri.uniza.semestralka2.general_utils.round
import fri.uniza.semestralka2.general_utils.secondsToLocalTime
import fri.uniza.semestralka2.general_utils.secondsToMinutes
import fri.uniza.semestralka2.statistics.DiscreteStatistic

class OverallStats {

    lateinit var systemTime: DiscreteStatistic
        private set

    lateinit var ticketQueueTime: DiscreteStatistic
        private set

    lateinit var ticketQueueLength: DiscreteStatistic
        private set

    lateinit var serviceQueueLength: DiscreteStatistic
        private set

    lateinit var cashDeskQueueTime: DiscreteStatistic
        private set

    lateinit var lastCustomerExit: DiscreteStatistic
        private set

    lateinit var customersServed: DiscreteStatistic
        private set

    fun reset() {
        systemTime = DiscreteStatistic()
        ticketQueueTime = DiscreteStatistic()
        ticketQueueLength = DiscreteStatistic()
        serviceQueueLength = DiscreteStatistic()
        cashDeskQueueTime = DiscreteStatistic()
        lastCustomerExit = DiscreteStatistic()
        customersServed = DiscreteStatistic()
    }

    override fun toString(): String {
        return "Average customers served: ${customersServed.mean.round(3)}" +
                "\nAverage time in company: ${systemTime.mean.secondsToMinutes().round(3)}" +
                "\nAverage time in ticket machine queue: ${ticketQueueTime.mean.secondsToMinutes().round(3)}" +
                "\nAverage ticket machine queue: ${ticketQueueLength.mean.round(3)}" +
                "\nAverage service desks queue: ${serviceQueueLength.mean.round(3)}" +
                "\nAverage exit of the last customer: ${lastCustomerExit.mean.secondsToLocalTime()}"
    }
}