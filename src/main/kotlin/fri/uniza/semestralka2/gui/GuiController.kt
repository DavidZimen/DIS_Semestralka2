package fri.uniza.semestralka2.gui

import fri.uniza.semestralka2.Semestralka2
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
import javafx.scene.control.Alert.AlertType
import javafx.scene.image.Image
import javafx.stage.Stage
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jfree.chart.ChartFactory
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.axis.NumberTickUnit
import org.jfree.chart.fx.ChartViewer
import org.jfree.chart.plot.XYPlot
import org.jfree.data.Range
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import java.awt.BasicStroke
import java.awt.Color
import java.net.URL
import java.time.LocalTime
import java.util.*


@OptIn(DelicateCoroutinesApi::class)
class GuiController : Initializable {

    private val simulationApi = CompanySimulationApi.instance
    private var state = SimulationState.STOPPED
    var stage: Stage? = null
        set(value) {
            field = value
            field?.setOnCloseRequest { e ->
                if (state == SimulationState.RUNNING) {
                    e.consume()
                    onPause()
                    showAlert("Simulation is still running. Do you really want to close application ?",
                        "Running simulation",
                        AlertType.CONFIRMATION
                    )
                }
            }
        }

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

    //GRAPH PROPERTIES
    @FXML
    private lateinit var serviceDesks: TextField
    @FXML
    private lateinit var maxCashDeskCount: TextField
    @FXML
    private lateinit var minCashDeskCount: TextField
    @FXML
    private lateinit var replicationsGraph: TextField
    @FXML
    private lateinit var chart: ChartViewer
    private var series = XYSeries("series")

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        stateDisabling(SimulationState.STOPPED)
        changeMode()

        initTables()
        initInputs()
        initSliders()
        createChart()
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

    @FXML
    fun startGraphTest() {
        createChart()
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
                speed.value = simState.speed.round(2)

                this.state = state.state
            }
        }
    }

    private fun Double.toSpeedLabel() {
        speedLabel.text = "Speed: ${this.round(2)}x"
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
        replicationsGraph.allowOnlyInt()
        minCashDeskCount.allowOnlyInt()
        maxCashDeskCount.allowOnlyInt()
        serviceDesksCount.allowOnlyInt()
    }

    private fun initSliders() {
        speed.valueProperty().addListener { _, _, new ->
            simulationApi.setSpeed(new.toDouble())
            new.toDouble().toSpeedLabel()
        }
        closeTime.valueProperty().addListener { _, _, new ->
            val time = new.toDouble().minutesToLocalTime()
            try {
                simulationApi.setCloseTime(time)
            } catch (e: IllegalStateException) {
                showAlert(e.message)
            }
            closeLabel.text = "Close time: $time"
        }
        lastTicketTime.valueProperty().addListener { _, _, new ->
            val time = new.toDouble().minutesToLocalTime()
            try {
                simulationApi.setLastTicketTime(time)
            } catch (e: IllegalStateException) {
                showAlert(e.message)
            }
            lastTicketLabel.text = "Last ticket time: $time"
        }
        openTime.valueProperty().addListener { _, _, new ->
            val time = new.toDouble().minutesToLocalTime()
            try {
                simulationApi.setOpenTime(time)
            } catch (e: IllegalStateException) {
                showAlert(e.message)
            }
            openTimeLabel.text = "Open time: $time"
        }
        closeTime.init(LocalTime.of(17, 30))
        openTime.init(LocalTime.of(9, 0))
        lastTicketTime.init(LocalTime.of(17, 0))

        speed.value = 1.0
        speed.min = SimulationCore.MIN_SLOW_DOWN
        speed.max = SimulationCore.MAX_SPEED_UP
    }

    private fun createChart() {
        val dataset = XYSeriesCollection()
        val color = Color.GREEN
        series.clear()
        dataset.addSeries(series)

        val chart = ChartFactory.createXYLineChart(
            "Company event simulation",
            "Cash desks",
            "Average ticket machine queue",
            dataset
        )

        //auto range for the y-axis
        val yAxis = chart.xyPlot.rangeAxis as NumberAxis
        yAxis.autoRangeIncludesZero = false
        yAxis.isAutoRange = true

        val xAxis = chart.xyPlot.domainAxis as NumberAxis
        xAxis.range = Range(minCashDeskCount.text.toDouble() - 1, maxCashDeskCount.text.toDouble() + 1)
        xAxis.tickUnit = NumberTickUnit(1.0)
        xAxis.isAutoRange = false

        with(chart.plot as XYPlot) {
            backgroundPaint = Color.WHITE
            renderer.setSeriesStroke(0, BasicStroke(1.5f))
            renderer.setSeriesPaint(0, color)
        }
        this.chart.chart = chart
        this.chart.isVisible = true
    }

    private fun showAlert(message: String?, titleMsg: String = "Wrong time set", type: AlertType = AlertType.ERROR) {
        with(Alert(type)) {
            title = titleMsg
            headerText = title
            contentText = message
            (dialogPane.scene.window as Stage).icons.add(Image(Semestralka2::class.java.getResourceAsStream("icon.png")))
            showAndWait().ifPresent { buttonType ->
                if (type == AlertType.CONFIRMATION) {
                    if (buttonType === ButtonType.OK) {
                        Platform.exit()
                    } else {
                        onResume()
                    }
                }
            }
        }
    }
}