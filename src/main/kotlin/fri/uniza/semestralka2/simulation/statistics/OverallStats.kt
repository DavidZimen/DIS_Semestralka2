package fri.uniza.semestralka2.simulation.statistics

import fri.uniza.semestralka2.general_utils.round
import fri.uniza.semestralka2.general_utils.secondsToLocalTime
import fri.uniza.semestralka2.general_utils.secondsToMinutes
import fri.uniza.semestralka2.statistics.DiscreteStatistic
import java.math.RoundingMode

class OverallStats {
    var systemTime = DiscreteStatistic()
        private set

    var ticketQueueTime = DiscreteStatistic()
        private set

    var lastCustomerExit = DiscreteStatistic()
        private set

    var customersServed = DiscreteStatistic()
        private set

    fun reset() {
        systemTime = DiscreteStatistic()
        ticketQueueTime = DiscreteStatistic()
        lastCustomerExit = DiscreteStatistic()
        customersServed = DiscreteStatistic()
    }

    override fun toString(): String {
        return "Average customers served: ${customersServed.mean.round(3, RoundingMode.HALF_UP)}" +
                "\nAverage time in company: ${systemTime.mean.secondsToMinutes().round(3, RoundingMode.HALF_UP)}" +
                "\nAverage time in ticket machine queue: ${ticketQueueTime.mean.secondsToMinutes().round(3, RoundingMode.HALF_UP)}" +
                "\nAverage exit of the last customer: ${lastCustomerExit.mean.secondsToLocalTime()}"
    }
}