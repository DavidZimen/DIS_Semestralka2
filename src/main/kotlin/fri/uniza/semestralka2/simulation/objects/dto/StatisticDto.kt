package fri.uniza.semestralka2.simulation.objects.dto

import fri.uniza.semestralka2.general_utils.round
import fri.uniza.semestralka2.general_utils.secondsToLocalTime
import fri.uniza.semestralka2.statistics.Statistic
import java.time.LocalTime

/**
 * Dto for transferring [Statistic] data to GUI.
 */
data class StatisticDto(
    val mean: Double,
    val min: Double,
    val max: Double,
    val confidenceInterval: Pair<Double, Double>
)

/**
 * Dto for transferring [Statistic] data to GUI.
 * These data are converted to [LocalTime].
 */
data class TimeStatisticDto(
    val mean: LocalTime,
    val min: LocalTime,
    val max: LocalTime,
    val confidenceInterval: Pair<LocalTime, LocalTime>
)

/**
 * Maps [Statistic] to [StatisticDto]
 * with provided [confPercentage] for calculating Confidence interval.
 */
fun Statistic.toDto(confPercentage: Int = 95, decimalPlaces: Int = 3) = StatisticDto(
    mean.round(decimalPlaces),
    min.round(decimalPlaces),
    max.round(decimalPlaces),
    getConfidenceInterval(confPercentage)
)

fun Statistic.toTimeDto(confPercentage: Int = 95) = TimeStatisticDto(
    mean.secondsToLocalTime(),
    min.secondsToLocalTime(),
    max.secondsToLocalTime(),
    with(getConfidenceInterval(confPercentage)) {
        first.secondsToLocalTime() to second.secondsToLocalTime()
    }
)