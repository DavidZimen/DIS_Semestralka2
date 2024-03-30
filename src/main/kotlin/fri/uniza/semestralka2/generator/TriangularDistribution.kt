package fri.uniza.semestralka2.generator

import fri.uniza.semestralka2.general_utils.isABetweenBandC
import fri.uniza.semestralka2.general_utils.isALessOrEqualsToB
import java.util.*
import kotlin.jvm.Throws
import kotlin.math.sqrt

/**
 * Random generator for Triangular distribution.
 * Possible to provide own [min], [max] and [mode] attributes.
 * Implements [Generator] interface.
 * @author David Zimen
 */
class TriangularDistribution : Generator {

    /**
     * Minimum parameter of the triangular distribution.
     */
    private val min: Double

    /**
     * Maximum parameter of the triangular distribution.
     */
    private val max: Double

    /**
     * Mode parameter of the triangular distribution.
     */
    private val mode: Double

    /**
     * Generator of random numbers.
     */
    private val generator = Random(nextSeed())

    /**
     * Creates a new instance of [TriangularDistribution] with
     *  [min] set to 0,
     *  [max] set to 1,
     *  and [mode] set to 0.8.
     */
    constructor() : this(0.0, 1.0, 0.5)

    /**
     * Creates a new instance of [TriangularDistribution] based on [min], [max] and [mode] parameters.
     * @throws IllegalStateException [max] > [min] || [mode] is not between [min] and [max].
     */
    @Throws(IllegalStateException::class)
    constructor(min: Double, max: Double, mode: Double) {
        if (isALessOrEqualsToB(max, min)) {
            throw IllegalStateException()
        }

        if (!isABetweenBandC(mode, min, max)) {
            throw IllegalStateException()
        }

        this.min = min
        this.max = max
        this.mode = mode
    }

    /**
     * Overrides [Generator.sample] method.
     * @return Pseudo-randomly generated number with Triangular distribution
     *  with [min], [max] and [mode].
     */
    override fun sample(): Double {
        val rand = generator.nextDouble()
        return if (isALessOrEqualsToB(rand, (mode - min) / (max - min))) {
            min + sqrt(rand * (max - min) * (mode - min))
        } else {
            max - sqrt((1 - rand) * (max - min) * (max - mode))
        }
    }
}