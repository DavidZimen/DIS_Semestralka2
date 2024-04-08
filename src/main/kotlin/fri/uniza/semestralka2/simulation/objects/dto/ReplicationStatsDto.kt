package fri.uniza.semestralka2.simulation.objects.dto

import fri.uniza.semestralka2.general_utils.format
import fri.uniza.semestralka2.general_utils.secondsToLocalTime
import fri.uniza.semestralka2.simulation.statistics.ReplicationStats

/**
 * Dto for transferring [ReplicationStats] data to GUI.
 */
data class ReplicationStatsDto(
    val avgSystemTime: StatisticDto,
    val avgTicketQueueTime: StatisticDto,
    val avgCashDeskQueueTime: StatisticDto,
    val lastCustomerExit: String,
    val customersServed: String
)

/**
 * Maps [ReplicationStats] to [ReplicationStatsDto].
 */
fun ReplicationStats.toDto(confInterval: Int = 95) = ReplicationStatsDto(
    systemTime.toDto(null, 95),
    ticketQueueTime.toDto(null, 95),
    cashDeskQueueTime.toDto(null, 95),
    lastCustomerExit.secondsToLocalTime().format(),
    customersServed.toString()
)