package fri.uniza.semestralka2.api

import fri.uniza.semestralka2.core.EventSimulationMode
import fri.uniza.semestralka2.core.EventSimulationState
import fri.uniza.semestralka2.observer.Observer
import fri.uniza.semestralka2.simulation.CompanyEventSimulation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.LocalTime

/**
 * API for bridging UI and backend parts of the application.
 */
class CompanySimulationApi private constructor() {

    /**
     * Instance of the [CompanyEventSimulation].
     */
    private val simulation = CompanyEventSimulation()

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
    fun changeMode(mode: EventSimulationMode) {
        simulation.mode = mode
    }

    /**
     * Sets new speed of the [simulation].
     */
    fun setSpeed(speed: Double) {
        simulation.speed = speed
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

    fun setEntryParameters(replicationsCount: Int, serviceDeskCount: Int, cashDeskCount: Int) {
        simulation.replicationsCount = replicationsCount.toLong()
        simulation.serviceDeskCount = serviceDeskCount
        simulation.cashDeskCount = cashDeskCount
    }

    fun setOpenTime(time: LocalTime) = simulation.setOpenTime(time)

    fun setCloseTime(time: LocalTime) = simulation.setClosingTime(time)

    fun setLastTicketTime(time: LocalTime) = simulation.setTicketMachineClosingTime(time)

    companion object {
        /**
         * Provides single instance of [CompanySimulationApi] to whole application scope.
         */
        val instance: CompanySimulationApi by lazy {
            CompanySimulationApi()
        }
    }
}