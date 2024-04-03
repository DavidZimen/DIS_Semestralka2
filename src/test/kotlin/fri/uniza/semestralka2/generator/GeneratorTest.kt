package fri.uniza.semestralka2.generator

import fri.uniza.semestralka2.general_utils.writeToCsv
import fri.uniza.semestralka2.statistics.DiscreteStatistic
import org.junit.jupiter.api.Test

class GeneratorTest {
    @Test
    fun generateTriangularNumbers() {
        generate(TriangularDistribution(60.0, 480.0, 120.0), TriangularDistribution::class.simpleName!!)
    }

    @Test
    fun generateExponentialNumbers() {
        generate(ExponentialGenerator(1 / 2.0), ExponentialGenerator::class.simpleName!!)
    }

    private fun generate(generator: Generator, name: String) {
        // generate data
        val replications = 1_000_000
        val stats = DiscreteStatistic()
        val data = mutableListOf<Double>()
        for (i in 0 until replications) {
            val sample = generator.sample()
            data.add(sample)
            stats.addEntry(sample)
        }
        println(stats)
        // write data to file
        writeToCsv("data/${name}.csv", data)
    }
}