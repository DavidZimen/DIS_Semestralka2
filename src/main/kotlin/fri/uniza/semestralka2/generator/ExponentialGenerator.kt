package fri.uniza.semestralka2.generator

import fri.uniza.semestralka2.general_utils.isALessOrEqualsToB
import java.util.Random
import kotlin.jvm.Throws
import kotlin.math.ln

class ExponentialGenerator : Generator {

    /**
     * Lambda parameter of the exponential distribution.
     */
    private val lambda: Double

    /**
     * Generator of random numbers.
     */
    private val generator = Random(nextSeed())

    /**
     * Creates a new instance of [ExponentialGenerator] with [lambda] set to 1.
     */
    constructor() : this(1.0)

    /**
     * Creates a new instance of [ExponentialGenerator] based on [lambda] parameter.
     * @throws IllegalStateException [lambda] <= 0
     */
    @Throws(IllegalStateException::class)
    constructor(lambda: Double) {
        if (isALessOrEqualsToB(lambda, 0.0)) {
            throw IllegalStateException(GeneratorMessage.LAMBDA_VALUE_MSG)
        }
        this.lambda = lambda
    }

    /**
     * Overrides [Generator.sample] method.
     * @return Pseudo-randomly generated number with Exponential distribution
     *  based on [lambda] parameter.
     */
    override fun sample(): Double {
        return -ln(1 - generator.nextDouble()) / lambda
    }
}