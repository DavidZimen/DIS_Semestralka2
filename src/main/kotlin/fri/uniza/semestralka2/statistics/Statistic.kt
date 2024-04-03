package fri.uniza.semestralka2.statistics

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
}