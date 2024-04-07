package fri.uniza.semestralka2.simulation.objects.dto

import fri.uniza.semestralka2.general_utils.round
import fri.uniza.semestralka2.statistics.Statistic

/**
 * Dto for transferring [Statistic] data to GUI.
 */
data class StatisticDto(
    val mean: Double,
    val min: Double,
    val max: Double,
    val confidenceInterval: Pair<Int, Pair<Double, Double>>,
    val name: String? = null
)

/**
 * Maps [Statistic] to [StatisticDto]
 * with provided [confPercentage] for calculating Confidence interval.
 */
fun Statistic.toDto(name: String? = null, confPercentage: Int = 95, decimalPlaces: Int = 3) = StatisticDto(
    mean.round(decimalPlaces),
    min.round(decimalPlaces),
    max.round(decimalPlaces),
    confPercentage to getConfidenceInterval(confPercentage, decimalPlaces),
    name
)