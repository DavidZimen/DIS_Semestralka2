package fri.uniza.semestralka2.gui

import fri.uniza.semestralka2.api.CompanySimulationApi
import fri.uniza.semestralka2.simulation.CompanyEventSimulation
import fri.uniza.semestralka2.simulation.core.EventSimulationCore
import fri.uniza.semestralka2.simulation.objects.dto.CustomerDto
import fri.uniza.semestralka2.simulation.objects.dto.ServiceDto
import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Label
import javafx.scene.control.SelectionMode
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URL
import java.util.*

class GuiController : Initializable {

    private val simulationApi = CompanySimulationApi.instance

    @FXML
    private lateinit var speed: Label
    @FXML
    private lateinit var simulationTime: Label

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
    }

    @FXML
    fun onStart() {
        simulationApi.changeMode(EventSimulationCore.Mode.SINGLE)
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
        simulationApi.changeMode(EventSimulationCore.Mode.SINGLE)
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
            }
        }
    }

    private fun Double.toSpeedLabel() {
        speed.text = "Speed: ${this}x"
    }
}