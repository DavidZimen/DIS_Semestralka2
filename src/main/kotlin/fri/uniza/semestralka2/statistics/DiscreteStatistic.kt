package fri.uniza.semestralka2.statistics

import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Statistic calculation for discrete data.
 * Provides mean, variance standard deviation, min and max value.
 * @author David Zimen
 */
class DiscreteStatistic {

    /**
     * Average value of all values added into data set.
     */
    var mean = 0.0
        private set

    /**
     * Variance of the data set.
     */
    var variance = 0.0
        private set

    /**
     * Standard deviation in data set/
     */
    var standardDeviation = 0.0
        private set

    /**
     * Maximum value from data set.
     */
    var max = Double.MIN_VALUE
        private set

    /**
     * Minimum value from data set.
     */
    var min = Double.MAX_VALUE
        private set

    /**
     * Last value added to data set.
     */
    private var lastValue: Number = 0

    /**
     * Number of elements in data set.
     */
    private var count: Long = 1

    /**
     * Adds [sample] to data set and recalculates all variables.
     */
    fun addEntry(sample: Number) {
        val sampleDouble = sample.toDouble()
        lastValue = sample
        count++

        if (sampleDouble < min)
            min = sampleDouble

        if (sampleDouble > max)
            max = sampleDouble

        calculateVariance()
        calculateMean()
        calculateStandardDeviation()
    }

    private fun calculateMean() {
        mean = ((mean * (count - 1)) + lastValue.toDouble()) / count
    }

    private fun calculateVariance() {
        variance = ((count - 1) / count.toDouble()) * (variance + ((lastValue.toDouble() - mean).pow(2) / (count - 1)))
    }

    private fun calculateStandardDeviation() {
        standardDeviation = sqrt(variance)
    }
}