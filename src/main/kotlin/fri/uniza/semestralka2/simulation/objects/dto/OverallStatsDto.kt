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
    val avgCashDeskStats: Map<String, Pair<StatisticDto, StatisticDto>>,
    val avgServiceQueueLength: StatisticDto,
    val avgServiceDeskWorkload: Map<String, StatisticDto>,
    val avgLastCustomerExit: TimeStatisticDto,
    val avgCustomersServed: StatisticDto
)

/**
 * Maps [OverallStats] to [OverallStatsDto].
 */
fun OverallStats.toDto(confInterval: Int = 95) = OverallStatsDto(
    systemTime.toDto(confInterval),
    ticketQueueTime.toDto(confInterval),
    ticketQueueLength.toDto(confInterval),
    ticketMachineWorkload.toDto(confInterval),
    cashDeskQueueTime.toDto(confInterval),
    cashDesksStats.mapValues { it.value.first.toDto(confInterval) to it.value.second.toDto(confInterval) },
    serviceQueueLength.toDto(confInterval),
    serviceDesksWorkload.mapValues { it.value.toDto(confInterval) },
    lastCustomerExit.toTimeDto(confInterval),
    customersServed.toDto(confInterval)
)