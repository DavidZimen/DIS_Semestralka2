package fri.uniza.semestralka2.simulation.objects.dto

import fri.uniza.semestralka2.general_utils.round
import fri.uniza.semestralka2.general_utils.secondsToLocalTime
import fri.uniza.semestralka2.simulation.statistics.OverallStats
import java.time.LocalTime


/**
 * Dto for transferring [OverallStats] data to GUI.
 */
data class OverallStatsDto(
    val avgSystemTime: Double,
    val avgTicketQueueTime: Double,
    val avgTicketQueueLength: Double,
    val avgTicketMachineWorkload: Double,
    val avgCashDeskQueueTime: Double,
    val avgCashDeskStats: List<Pair<Double, Double>>,
    val avgServiceQueueLength: Double,
    val avgServiceDeskWorkload: List<Double>,
    val avgLastCustomerExit: LocalTime,
    val avgCustomersServed: Double
)

/**
 * Maps [OverallStats] to [OverallStatsDto].
 */
fun OverallStats.toDto(decimalPlaces: Int = 3) = OverallStatsDto(
    systemTime.mean.round(decimalPlaces),
    ticketQueueTime.mean.round(decimalPlaces),
    ticketQueueLength.mean.round(decimalPlaces),
    ticketMachineWorkload.mean.round(decimalPlaces),
    cashDeskQueueTime.mean.round(decimalPlaces),
    cashDesksStats.map { it.first.mean.round(decimalPlaces) to it.second.mean.round(decimalPlaces) },
    serviceQueueLength.mean.round(decimalPlaces),
    serviceDesksWorkload.map { it.mean.round(decimalPlaces) },
    lastCustomerExit.mean.secondsToLocalTime(),
    customersServed.mean.round(decimalPlaces)
)