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
     * Adds [sample] to data set and recalculates all variables.
     */
    fun addEntry(sample: Number) {
        val sampleDouble = sample.toDouble()
        sum += sampleDouble
        squareSum += sampleDouble.pow(2)
        count++

        calculateMinMax(sampleDouble)
        calculateVariance()
        calculateMean()
        calculateStandardDeviation()
    }

    private fun calculateMean() {
        mean = sum / count
    }

    private fun calculateVariance() {
        variance = ( (1 / count) * squareSum ) - ( (1 / count) * sum ).pow(2)
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