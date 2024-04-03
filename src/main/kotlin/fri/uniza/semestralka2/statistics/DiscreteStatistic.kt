package fri.uniza.semestralka2.statistics

import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Statistic calculation for discrete data.
 * Provides mean, variance standard deviation, min and max value.
 * @author David Zimen
 */
class DiscreteStatistic : Statistic() {

    /**
     * Last value added to data set.
     */
    private var lastValue: Double = 0.0

    /**
     * Adds [sample] to data set and recalculates all variables.
     */
    fun addEntry(sample: Number) {
        lastValue = sample.toDouble()
        count++

        if (lastValue < min)
            min = lastValue

        if (lastValue > max)
            max = lastValue

        calculateVariance()
        calculateMean()
        calculateStandardDeviation()
    }

    private fun calculateMean() {
        mean = ((mean * (count - 1)) + lastValue) / count
    }

    private fun calculateVariance() {
        variance = ((count - 1) / count.toDouble()) * (variance + ((lastValue - mean).pow(2) / (count - 1)))
    }

    private fun calculateStandardDeviation() {
        standardDeviation = sqrt(variance)
    }

    override fun toString(): String {
        return "\nMean: $mean" +
                "\nVariance: $variance" +
                "\nStandard deviation: $standardDeviation"
    }
}