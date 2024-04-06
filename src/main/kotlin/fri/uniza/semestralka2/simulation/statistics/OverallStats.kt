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

    lateinit var ticketMachineWorkload: DiscreteStatistic
        private set

    lateinit var serviceQueueLength: DiscreteStatistic
        private set

    lateinit var cashDeskQueueTime: DiscreteStatistic
        private set

    lateinit var lastCustomerExit: DiscreteStatistic
        private set

    lateinit var customersServed: DiscreteStatistic
        private set

    lateinit var cashDesksStats: MutableList<Pair<DiscreteStatistic, DiscreteStatistic>>
        private set

    lateinit var serviceDesksWorkload: MutableList<DiscreteStatistic>
        private set

    fun reset() {
        systemTime = DiscreteStatistic()
        ticketQueueTime = DiscreteStatistic()
        ticketQueueLength = DiscreteStatistic()
        ticketMachineWorkload = DiscreteStatistic()
        serviceQueueLength = DiscreteStatistic()
        cashDeskQueueTime = DiscreteStatistic()
        lastCustomerExit = DiscreteStatistic()
        customersServed = DiscreteStatistic()
        cashDesksStats = mutableListOf()
        serviceDesksWorkload = mutableListOf()
    }

    override fun toString(): String {
        var str = "\nAverage customers served: ${customersServed.mean.round(3)}" +
                "\nAverage time in company: ${systemTime.mean.round(3)} sec / ${systemTime.mean.secondsToMinutes().round(3)} min" +
                "\nAverage time in ticket machine queue: ${ticketQueueTime.mean.round(3)} sec / ${ticketQueueTime.mean.secondsToMinutes().round(3)} min" +
                "\nAverage ticket machine queue: ${ticketQueueLength.mean.round(3)}" +
                "\nAverage ticket machine workload: ${(ticketMachineWorkload.mean * 100).round(2)}%" +
                "\nAverage service desks queue: ${serviceQueueLength.mean.round(3)}"
        serviceDesksWorkload.forEachIndexed { i, it ->
            str += "\n\tService desk $i: ${(it.mean * 100).round(2)}%"
        }
        str += "\nAverage cash desk queue time: ${cashDeskQueueTime.mean.round(3)} sec / ${cashDeskQueueTime.mean.round(3).secondsToMinutes()} min"
        str.plus("\nAverage cash desk workload | queue length:")
        cashDesksStats.forEachIndexed { i, it ->
            str += "\n\tCash desk $i: ${(it.first.mean * 100).round(2)}% | ${it.second.mean.round(3)}"
        }
        str += "\nAverage exit of the last customer: ${lastCustomerExit.mean.secondsToLocalTime()}"
        return str
    }
}