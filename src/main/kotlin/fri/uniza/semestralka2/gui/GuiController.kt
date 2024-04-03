package fri.uniza.semestralka2.gui

import fri.uniza.semestralka2.api.CompanySimulationApi
import fri.uniza.semestralka2.general_utils.format
import fri.uniza.semestralka2.general_utils.minutesToLocalTime
import fri.uniza.semestralka2.general_utils.round
import fri.uniza.semestralka2.simulation.CompanyEventSimulation
import fri.uniza.semestralka2.simulation.core.EventSimulationMode
import fri.uniza.semestralka2.simulation.core.SimulationCore
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
import java.time.LocalTime
import java.util.*

@OptIn(DelicateCoroutinesApi::class)
class GuiController : Initializable {

    private val simulationApi = CompanySimulationApi.instance

    // INPUTS
    @FXML
    private lateinit var replications: TextField
    @FXML
    private lateinit var serviceDesksCount: TextField
    @FXML
    private lateinit var cashDesksCount: TextField
    @FXML
    private lateinit var waitingAreaCount: TextField
    @FXML
    private lateinit var simulationTime: Label
    @FXML
    private lateinit var modeR: RadioButton

    // SLIDERS
    @FXML
    private lateinit var openTime: Slider
    @FXML
    private lateinit var openTimeLabel: Label
    @FXML
    private lateinit var lastTicketTime: Slider
    @FXML
    private lateinit var lastTicketLabel: Label
    @FXML
    private lateinit var closeTime: Slider
    @FXML
    private lateinit var closeLabel: Label
    @FXML
    private lateinit var speed: Slider
    @FXML
    private lateinit var speedLabel: Label

    // BUTTONS
    @FXML
    private lateinit var startButton: Button
    @FXML
    private lateinit var stopButton: Button
    @FXML
    private lateinit var resumeButton: Button
    @FXML
    private lateinit var pauseButton: Button

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
        stateDisabling(SimulationState.STOPPED)
        changeMode()

        initTables()
        initInputs()
        initSliders()
    }

    @FXML
    fun onStart() {
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
        stateDisabling(SimulationState.STOPPED)
    }

    @FXML
    fun onResume() {
        GlobalScope.launch {
            simulationApi.resumeSimulation()
        }
    }

    @FXML
    fun onPause() = simulationApi.pauseSimulation()

    @FXML
    fun changeMode() {
        simulationApi.changeMode(if (modeR.isSelected) EventSimulationMode.REPLICATIONS else EventSimulationMode.SINGLE)
    }

    private fun createSimulationObserver() {
        simulationApi.observeSimulation("observer") { state ->
            Platform.runLater {
                val simState = state as CompanyEventSimulation.CompanySimulationState
                simulationTime.text = "Time: ${simState.time.format()}"

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
        speedLabel.text = "Speed: ${this.round(2, RoundingMode.HALF_UP)}x"
    }

    private fun stateDisabling(state: SimulationState) {
        when (state) {
            SimulationState.RUNNING -> {
                startButton.disable()
                stopButton.disable(false)
                resumeButton.disable()
                pauseButton.disable(false)
            }
            SimulationState.STOPPED -> {
                startButton.disable(false)
                stopButton.disable()
                resumeButton.disable()
                pauseButton.disable()
            }
            SimulationState.PAUSED -> {
                startButton.disable()
                stopButton.disable()
                resumeButton.disable(false)
                pauseButton.disable()
            }
        }
    }

    private fun initTables() {
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
    }

    private fun initInputs() {
        replications.allowOnlyInt()
        serviceDesksCount.allowOnlyInt()
        cashDesksCount.allowOnlyInt()
        waitingAreaCount.allowOnlyInt()
    }

    private fun initSliders() {
        speed.valueProperty().addListener { _, _, new ->
            new.toDouble().toSpeedLabel()
            simulationApi.setSpeed(new.toDouble())
        }
        closeTime.valueProperty().addListener { _, _, new ->
            val time = new.toDouble().minutesToLocalTime()
            closeLabel.text = "Close time: $time"
            simulationApi.setCloseTime(time)
        }
        lastTicketTime.valueProperty().addListener { _, _, new ->
            val time = new.toDouble().minutesToLocalTime()
            lastTicketLabel.text = "Last ticket time: $time"
            simulationApi.setLastTicketTime(time)
        }
        openTime.valueProperty().addListener { _, _, new ->
            val time = new.toDouble().minutesToLocalTime()
            openTimeLabel.text = "Open time: $time"
            simulationApi.setOpenTime(time)
        }
        closeTime.init(LocalTime.of(17, 30))
        openTime.init(LocalTime.of(9, 0))
        lastTicketTime.init(LocalTime.of(17, 0))

        speed.value = 1.0
        speed.min = SimulationCore.MIN_SLOW_DOWN
        speed.max = SimulationCore.MAX_SPEED_UP
    }
}