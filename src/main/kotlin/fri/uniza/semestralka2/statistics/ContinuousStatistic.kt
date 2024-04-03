package fri.uniza.semestralka2.statistics

/**
 * Statistic calculation for continuous data.
 * Provides mean, min and max value.
 * @author David Zimen
 */
class ContinuousStatistic(private val startTime: Double) : Statistic() {

    /**
     * Last value added to data set.
     */
    private var lastValue = 0.0 to 0.0

    /**
     * Weighted sum of items
     */
    private var totalArea = 0.0

    /**
     * Adds [yAxis] to data set and recalculates all variables.
     */
    fun addEntry(yAxis: Number, xAxis: Number) {
        val pair = yAxis.toDouble() to xAxis.toDouble()

        if (pair.first < min)
            min = pair.first

        if (pair.first > max)
            max = pair.first

        calculateMean(pair)
        lastValue = pair
    }

    private fun calculateMean(pair: Pair<Double, Double>) {
        val xAxisDiff = pair.second - lastValue.second
        totalArea += xAxisDiff * lastValue.first
        mean = totalArea / (pair.second - startTime)
    }
}