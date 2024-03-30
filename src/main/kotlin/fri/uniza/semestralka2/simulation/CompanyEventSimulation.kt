package fri.uniza.semestralka2.simulation

import fri.uniza.novinovy_stanok.generator.ContinuousUniformGenerator
import fri.uniza.semestralka2.general_utils.minutesToSeconds
import fri.uniza.semestralka2.general_utils.toSeconds
import fri.uniza.semestralka2.generator.ExponentialGenerator
import fri.uniza.semestralka2.generator.Generator
import fri.uniza.semestralka2.simulation.core.EventSimulationCore
import java.time.LocalTime

/**
 * Implementation of Event driven simulation based on assignment of 'Semestralna praca 2'.
 * @author David Zimen
 */
class CompanyEventSimulation : EventSimulationCore() {

    // TIME ATTRIBUTES
    /**
     * End of the simulation as defined by the user.
     */
    var closingTime = LocalTime.of(17, 30).toSeconds()
        private set

    /**
     * Time when the ticket machine will no longer print new tickets,
     */
    var automatClosingTime = LocalTime.of(17, 0).toSeconds()
        private set

    //
    /**
     * [Generator] for customers arrivals to company.
     */
    lateinit var arrivalGenerator: Generator
        private set

    /**
     * [Generator] for time it takes the customer
     */
    lateinit var automatTimeGenerator: Generator
        private set

    /**
     * [Generator] for values in <0; 1) interval for [CustomerType].
     */
    lateinit var customerTypeGenerator: Generator
        private set

    // OVERRIDE FUNCTIONS
    override fun beforeSimulation() {
        initGenerators()
    }

    /**
     * Sets the new value of [closingTime] to [time] transformed to seconds.
     */
    fun setClosingTime(time: LocalTime) {
        simulationRunningCheck()
        closingTime = time.toSeconds()
    }

    /**
     * Sets the new value of [automatClosingTime] to [time] transformed to seconds.
     */
    fun setAutomatClosingTime(time: LocalTime) {
        simulationRunningCheck()
        automatClosingTime = time.toSeconds()
    }

    /**
     * Initialized generator based on assignment values.
     */
    private fun initGenerators() {
        arrivalGenerator = ExponentialGenerator(1 / 2.0.minutesToSeconds())
        automatTimeGenerator = ContinuousUniformGenerator(30.0, 180.0)
        customerTypeGenerator = ContinuousUniformGenerator()
    }
}