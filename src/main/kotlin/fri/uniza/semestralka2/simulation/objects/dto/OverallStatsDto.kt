package fri.uniza.semestralka2.simulation.objects.dto

import fri.uniza.semestralka2.simulation.statistics.OverallStats


/**
 * Dto for transferring [OverallStats] data to GUI.
 */
data class OverallStatsDto(
    val avgSystemTime: StatisticDto,
    val avgTicketQueueTime: StatisticDto,
    val avgTicketQueueLength: StatisticDto,
    val avgTicketMachineWorkload: StatisticDto,
    val avgCashDeskQueueTime: StatisticDto,
    val avgCashDeskWorkload: List<StatisticDto>,
    val avgCashDeskQueueLength: List<StatisticDto>,
    val avgServiceQueueLength: StatisticDto,
    val avgServiceDeskWorkload: List<StatisticDto>,
    val avgLastCustomerExit: StatisticDto,
    val avgCustomersServed: StatisticDto
)

/**
 * Maps [OverallStats] to [OverallStatsDto].
 */
fun OverallStats.toDto(confInterval: Int = 95) = OverallStatsDto(
    systemTime.toDto(null, confInterval),
    ticketQueueTime.toDto("Ticket machine", confInterval),
    ticketQueueLength.toDto("Ticket machine", confInterval),
    ticketMachineWorkload.toDto("Ticket machine", confInterval),
    cashDeskQueueTime.toDto(null, confInterval),
    cashDesksStats.mapValues { it.value.first.toDto(it.key, confInterval) }.values.toList(),
    cashDesksStats.mapValues { it.value.second.toDto(it.key, confInterval) }.values.toList(),
    serviceQueueLength.toDto(null, confInterval),
    serviceDesksWorkload.mapValues { it.value.toDto(it.key, confInterval) }.values.toList(),
    lastCustomerExit.toDto(null, confInterval),
    customersServed.toDto(null, confInterval)
)