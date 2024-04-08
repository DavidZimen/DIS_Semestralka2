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

    lateinit var cashDesksStats: MutableMap<String, Pair<DiscreteStatistic, DiscreteStatistic>>
        private set

    lateinit var serviceDesksWorkload: MutableMap<String, DiscreteStatistic>
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
        cashDesksStats = mutableMapOf()
        serviceDesksWorkload = mutableMapOf()
    }

    override fun toString(): String {
        val confInt = systemTime.getConfidenceInterval(90)
        var str = "\nAverage customers served: ${customersServed.mean.round(3)}" +
                "\nAverage system time: ${systemTime.mean.round(3)} sec / ${systemTime.mean.secondsToMinutes().round(3)} min" +
                "\n\t95% confidence interval <${confInt.first}; ${confInt.second}> sec" +
                "\nAverage ticket machine queue time: ${ticketQueueTime.mean.round(3)} sec / ${ticketQueueTime.mean.secondsToMinutes().round(3)} min" +
                "\nAverage ticket machine queue length: ${ticketQueueLength.mean.round(3)}" +
                "\nAverage ticket machine workload: ${(ticketMachineWorkload.mean * 100).round(2)}%" +
                "\nAverage service desks queue: ${serviceQueueLength.mean.round(3)}"+
                "\nAva"
        serviceDesksWorkload.forEach { (k, v) ->
            str += "\n\t$k: ${(v.mean * 100).round(2)}%"
        }
        str += "\nAverage cash desk queue time: ${cashDeskQueueTime.mean.round(3)} sec / ${cashDeskQueueTime.mean.round(3).secondsToMinutes()} min"
        str.plus("\nAverage cash desk workload | queue length:")
        cashDesksStats.forEach { (k, v) ->
            str += "\n\t$k: ${(v.first.mean * 100).round(2)}% | ${v.second.mean.round(3)}"
        }
        str += "\nAverage exit of the last customer: ${lastCustomerExit.mean.secondsToLocalTime()}"
        return str
    }
}