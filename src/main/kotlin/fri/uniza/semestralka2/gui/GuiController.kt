package fri.uniza.semestralka2.gui

import fri.uniza.semestralka2.api.CompanySimulationApi
import fri.uniza.semestralka2.general_utils.round
import fri.uniza.semestralka2.simulation.CompanyEventSimulation
import fri.uniza.semestralka2.simulation.core.EventSimulationMode
import fri.uniza.semestralka2.simulation.core.SimulationState
import fri.uniza.semestralka2.simulation.objects.dto.CustomerDto
import fri.uniza.semestralka2.simulation.objects.dto.ServiceDto
import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.net.URL
import java.util.*

@OptIn(DelicateCoroutinesApi::class)
class GuiController : Initializable {

    private val simulationApi = CompanySimulationApi.instance

    @FXML
    private lateinit var replications: TextField
    @FXML
    private lateinit var serviceDesksCount: TextField
    @FXML
    private lateinit var cashDesksCount: TextField
    @FXML
    private lateinit var speed: Label
    @FXML
    private lateinit var simulationTime: Label

    // BUTTONS
    @FXML
    private lateinit var startButton: Button
    @FXML
    private lateinit var stopButton: Button
    @FXML
    private lateinit var resumeButton: Button
    @FXML
    private lateinit var pauseButton: Button
    @FXML
    private lateinit var speedUpButton: Button
    @FXML
    private lateinit var slowDownButton: Button

    // TABLE CUSTOMERS
    private var customers = FXCollections.observableArrayList<CustomerDto>()
    @FXML
    private lateinit var customersTable: TableView<CustomerDto>
    @FXML
    private lateinit var name: TableColumn<CustomerDto, String>
    @FXML
    private lateinit var cusType: TableColumn<CustomerDto, String>
    @FXML
    private lateinit var arrivalTime: TableColumn<CustomerDto, String>
    @FXML
    private lateinit var orderType: TableColumn<CustomerDto, String>
    @FXML
    private lateinit var orderSize: TableColumn<CustomerDto, String>
    @FXML
    private lateinit var serviceDesk: TableColumn<CustomerDto, String>
    @FXML
    private lateinit var chasDeskQueue: TableColumn<CustomerDto, String>
    @FXML
    private lateinit var chasDesk: TableColumn<CustomerDto, String>
    @FXML
    private lateinit var currentState: TableColumn<CustomerDto, String>

    // TABLE EMPLOYEES
    private var employees = FXCollections.observableArrayList<ServiceDto>()
    @FXML
    private lateinit var empTable: TableView<ServiceDto>
    @FXML
    private lateinit var empName: TableColumn<ServiceDto, String>
    @FXML
    private lateinit var queueLength: TableColumn<ServiceDto, String>
    @FXML
    private lateinit var workload: TableColumn<ServiceDto, String>
    @FXML
    private lateinit var empState: TableColumn<ServiceDto, String>


    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        customersTable.selectionModel.selectionMode = SelectionMode.SINGLE
        name.setCellValueFactory { cellData -> SimpleStringProperty(cellData.value.name) }
        cusType.setCellValueFactory { cellData -> SimpleStringProperty(cellData.value.type) }
        arrivalTime.setCellValueFactory { cellData -> SimpleStringProperty(cellData.value.arrivalTime) }
        orderType.setCellValueFactory { cellData -> SimpleStringProperty(cellData.value.orderType) }
        orderSize.setCellValueFactory { cellData -> SimpleStringProperty(cellData.value.orderSize) }
        serviceDesk.setCellValueFactory { cellData -> SimpleStringProperty(cellData.value.serviceDesk) }
        chasDeskQueue.setCellValueFactory { cellData -> SimpleStringProperty(cellData.value.cashDeskQueue) }
        chasDesk.setCellValueFactory { cellData -> SimpleStringProperty(cellData.value.cashDesk) }
        currentState.setCellValueFactory { cellData -> SimpleStringProperty(cellData.value.state) }

        empTable.selectionModel.selectionMode = SelectionMode.SINGLE
        empName.setCellValueFactory { cellData -> SimpleStringProperty(cellData.value.name) }
        queueLength.setCellValueFactory { cellData -> SimpleStringProperty(cellData.value.queueLength) }
        workload.setCellValueFactory { cellData -> SimpleStringProperty(cellData.value.workload) }
        empState.setCellValueFactory { cellData -> SimpleStringProperty(cellData.value.state) }

        replications.allowOnlyInt()
        serviceDesksCount.allowOnlyInt()
        cashDesksCount.allowOnlyInt()

        stateDisabling(SimulationState.STOPPED)
    }

    @FXML
    fun onStart() {
        simulationApi.changeMode(EventSimulationMode.SINGLE)
        simulationApi.setEntryParameters(replications.text.toInt(), serviceDesksCount.text.toInt(), cashDesksCount.text.toInt())
        createSimulationObserver()
        GlobalScope.launch {
            simulationApi.runSimulation()
        }
    }

    @FXML
    fun onStop() {
        simulationApi.stopObservingSimulation("observer")
        simulationApi.stopSimulation()
    }

    @FXML
    fun onResume() {
        simulationApi.changeMode(EventSimulationMode.SINGLE)
        GlobalScope.launch {
            simulationApi.resumeSimulation()
        }
    }

    @FXML
    fun onPause() = simulationApi.pauseSimulation()

    @FXML
    fun speedUp() = simulationApi.speedUpSimulation().toSpeedLabel()

    @FXML
    fun slowDown() = simulationApi.slowDownSimulation().toSpeedLabel()

    @FXML
    fun changeMode() { }

    private fun createSimulationObserver() {
        simulationApi.observeSimulation("observer") { state ->
            Platform.runLater {
                val simState = state as CompanyEventSimulation.CompanySimulationState
                simulationTime.text = "Time: ${simState.time}"

                // customers
                customers = FXCollections.observableArrayList(simState.customers)
                customersTable.items = customers

                //employees
                employees = FXCollections.observableArrayList(simState.employees)
                empTable.items = employees

                stateDisabling(simState.state)
                simState.speed.toSpeedLabel()
            }
        }
    }

    private fun Double.toSpeedLabel() {
        speed.text = "Speed: ${this.round(2, RoundingMode.HALF_UP)}x"
    }

    private fun stateDisabling(state: SimulationState) {
        when (state) {
            SimulationState.RUNNING -> {
                startButton.disable()
                stopButton.disable(false)
                resumeButton.disable()
                pauseButton.disable(false)
                speedUpButton.disable(false)
                slowDownButton.disable(false)
            }
            SimulationState.STOPPED -> {
                startButton.disable(false)
                stopButton.disable()
                resumeButton.disable()
                pauseButton.disable()
                speedUpButton.disable()
                slowDownButton.disable()
            }
            SimulationState.PAUSED -> {
                startButton.disable()
                stopButton.disable()
                resumeButton.disable(false)
                pauseButton.disable()
                speedUpButton.disable()
                slowDownButton.disable()
            }
        }
    }
}