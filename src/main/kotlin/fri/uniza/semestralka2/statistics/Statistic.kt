package fri.uniza.semestralka2.statistics

import fri.uniza.semestralka2.general_utils.round
import org.apache.commons.math3.distribution.NormalDistribution
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

abstract class Statistic {
    /**
     * Average value of all values added into data set.
     */
    var mean = 0.0
        protected set

    /**
     * Variance of the data set.
     */
    var variance = 0.0
        protected set

    /**
     * Standard deviation in data set/
     */
    var standardDeviation = 0.0
        protected set

    /**
     * Maximum value from data set.
     */
    var max = Double.MIN_VALUE
        protected set

    /**
     * Minimum value from data set.
     */
    var min = Double.MAX_VALUE
        protected set

    /**
     * Number of elements in data set.
     */
    protected var count: Long = 1

    /**
     * Sum of all elements added to [Statistic].
     */
    protected var sum = 0.0

    /**
     * Sum of all elements added to [Statistic]. Each element is squared.
     */
    protected var squareSum = 0.0

    protected fun calculateMinMax(sample: Double) {
        if (sample < min)
            min = sample

        if (sample > max)
            max = sample
    }

    /**
     * Returns value of [percentage] confidence interval.
     * [Pair.first] is lower limit, [Pair.second] is higher limit.
     * Values will be rounded to [decimalPlaces].
     */
    fun getConfidenceInterval(percentage: Int, decimalPlaces: Int = 3): Pair<Double, Double> {
        if (count < 30) {
            return 0.0 to 0.0
        }
        val sampleVariance = (squareSum - (sum.pow(2) / count)) / (count - 1)
        val sampleStdDeviation = sqrt(sampleVariance)
        val tAlpha = abs(NormalDistribution().inverseCumulativeProbability((1 - (percentage / 100.0)) / 2))
        val lowerLimit = mean - ((sampleStdDeviation * tAlpha) / sqrt(count.toDouble()))
        val higherLimit = mean + ((sampleStdDeviation * tAlpha) / sqrt(count.toDouble()))
        return lowerLimit.round(decimalPlaces) to higherLimit.round(decimalPlaces)
    }
}