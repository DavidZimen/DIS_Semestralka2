package fri.uniza.semestralka2.simulation.statistics

import fri.uniza.semestralka2.statistics.DiscreteStatistic

class ReplicationStats {
    var systemTime = DiscreteStatistic()
        private set

    var ticketQueueTime = DiscreteStatistic()
        private set

    var lastCustomerExit = 0.0

    var customersServed = 0

    fun reset() {
        systemTime = DiscreteStatistic()
        ticketQueueTime = DiscreteStatistic()
        lastCustomerExit = 0.0
        customersServed = 0
    }
}