package fri.uniza.semestralka2.api

import fri.uniza.semestralka2.observer.Observer
import fri.uniza.semestralka2.simulation.CompanyEventSimulation
import fri.uniza.semestralka2.simulation.core.EventSimulationCore
import fri.uniza.semestralka2.simulation.core.EventSimulationState
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * API for bridging UI and backend parts of the application.
 */
class CompanySimulationApi private constructor() {

    /**
     * Instance of the [CompanyEventSimulation].
     */
    private val simulation = CompanyEventSimulation()

    /**
     * Sets [replicationsCount] to all simulations.
     */
    @Throws(IllegalStateException::class)
    fun setReplicationsCount(replicationsCount: Long) {
        simulation.replicationsCount = replicationsCount
    }

    /**
     * Async function to start [simulation] in different thread.
     */
    suspend fun runSimulation() = coroutineScope {
        launch {
            simulation.runSimulation()
        }
    }

    /**
     * Async function to resume [simulation] after pausing in different thread.
     */
    suspend fun resumeSimulation() = coroutineScope {
        launch {
            simulation.resumeSimulation()
        }
    }

    /**
     * Stops [simulation] without possibility to resume.
     */
    fun stopSimulation() {
        simulation.stopSimulation()
        simulation.simulationStateObservable.unsubscribe()
    }

    /**
     * Pauses [simulation] withh possibility to resume.
     */
    fun pauseSimulation() {
        simulation.pauseSimulation()
    }

    /**
     * Changes mode of the [simulation].
     */
    fun changeMode(mode: EventSimulationCore.Mode) {
        simulation.mode = mode
    }

    /**
     * @return [EventSimulationCore.mode] of the [simulation].
     */
    fun getMode() = simulation.mode

    /**
     * Increases speed of the [simulation].
     * @return New [EventSimulationCore.speed] of [simulation]
     */
    fun speedUpSimulation(): Double {
        simulation.speedUpSimulation()
        return simulation.speed
    }

    /**
     * Decreases speed of the [simulation].
     * @return New [EventSimulationCore.speed] of [simulation]
     */
    fun slowDownSimulation(): Double {
        simulation.slowDownSimulation()
        return simulation.speed
    }

    /**
     * Adds observer for [simulation] state.
     */
    fun observeSimulation(name: String, observer: Observer<EventSimulationState>) {
        simulation.simulationStateObservable.subscribe(name, observer)
    }

    /**
     * Adds observer for [simulation] state.
     */
    fun stopObservingSimulation(name: String) {
        simulation.simulationStateObservable.unsubscribe(name)
    }

    companion object {
        /**
         * Provides single instance of [CompanySimulationApi] to whole application scope.
         */
        val instance: CompanySimulationApi by lazy {
            CompanySimulationApi()
        }
    }
}