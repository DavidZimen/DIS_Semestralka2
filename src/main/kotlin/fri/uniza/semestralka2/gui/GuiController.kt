package fri.uniza.semestralka2.gui

import fri.uniza.semestralka2.Semestralka2
import fri.uniza.semestralka2.api.CompanySimulationApi
import fri.uniza.semestralka2.core.EventSimulationMode
import fri.uniza.semestralka2.core.SimulationCore
import fri.uniza.semestralka2.core.SimulationState
import fri.uniza.semestralka2.general_utils.format
import fri.uniza.semestralka2.general_utils.minutesToLocalTime
import fri.uniza.semestralka2.general_utils.round
import fri.uniza.semestralka2.simulation.CompanyEventSimulation
import fri.uniza.semestralka2.simulation.objects.dto.CustomerDto
import fri.uniza.semestralka2.simulation.objects.dto.ServiceDto
import fri.uniza.semestralka2.simulation.objects.dto.StatisticDto
import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType
import javafx.scene.image.Image
import javafx.scene.layout.VBox
import javafx.stage.Stage
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jfree.chart.ChartFactory
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.axis.NumberTickUnit
import org.jfree.chart.fx.ChartViewer
import org.jfree.chart.labels.XYItemLabelGenerator
import org.jfree.chart.plot.XYPlot
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
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
        set(value) {
            field = value
            if (value == SimulationState.STOPPED) {
                simulationApi.stopObservingSimulation("observer")
            }
        }
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

    //CONTAINERS
    @FXML
    private lateinit var overallVBox: VBox
    @FXML
    private lateinit var replicationVBox: VBox
    @FXML
    private lateinit var wholeVBox: VBox

    // SPINNER
    @FXML
    private lateinit var spinnerLabel: Label
    @FXML
    private lateinit var spinner: ProgressIndicator

    // STATISTICS
    @FXML
    private lateinit var repsExecuted: Label
    @FXML
    private lateinit var ovSystemTime: Label
    @FXML
    private lateinit var ovTicketTime: Label
    @FXML
    private lateinit var ovCashDeskTime: Label
    @FXML
    private lateinit var ovLastExit: Label
    @FXML
    private lateinit var repNumber: Label
    @FXML
    private lateinit var repSystemTime: Label
    @FXML
    private lateinit var repTicketTime: Label
    @FXML
    private lateinit var repCashDeskTime: Label
    @FXML
    private lateinit var repLastExit: Label

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
    @FXML
    private lateinit var expStartButton: Button

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

    // TABLE OVERALL WORKLOADS
    private var workloads = FXCollections.observableArrayList<StatisticDto>()
    @FXML
    private lateinit var workloadsTable: TableView<StatisticDto>
    @FXML
    private lateinit var worklName: TableColumn<StatisticDto, String>
    @FXML
    private lateinit var worklMean: TableColumn<StatisticDto, String>
    @FXML
    private lateinit var worklMin: TableColumn<StatisticDto, String>
    @FXML
    private lateinit var worklMax: TableColumn<StatisticDto, String>

    // TABLE AVERAGE QUEUE LENGTHS
    private var queueLengths = FXCollections.observableArrayList<StatisticDto>()
    @FXML
    private lateinit var queueLengthsTable: TableView<StatisticDto>
    @FXML
    private lateinit var queueName: TableColumn<StatisticDto, String>
    @FXML
    private lateinit var queueMean: TableColumn<StatisticDto, String>
    @FXML
    private lateinit var queueMin: TableColumn<StatisticDto, String>
    @FXML
    private lateinit var queueMax: TableColumn<StatisticDto, String>

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
    private var series = XYSeries("Average ticket machine queue length")

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        stateDisabling(SimulationState.STOPPED)
        wholeVBox.children.remove(replicationVBox)
        wholeVBox.children.remove(overallVBox)
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
        if (modeR.isSelected) {
            simulationApi.changeMode(EventSimulationMode.REPLICATIONS)
            wholeVBox.children.remove(replicationVBox)
            wholeVBox.children.add(overallVBox)
            simulationTime.isVisible = false
        } else {
            simulationApi.changeMode(EventSimulationMode.SINGLE)
            wholeVBox.children.remove(overallVBox)
            wholeVBox.children.add(replicationVBox)
            simulationTime.isVisible = true
        }
    }

    @FXML
    fun startGraphTest() {
        createChart()
        val start = minCashDeskCount.text.toInt()
        val end = maxCashDeskCount.text.toInt()
        val serviceDesks = serviceDesks.text.toInt()

        // had to be correct interval
        if (start >= end) {
            showAlert("Cash desk from must be less than to !!!", "Wrong cash desks counts")
            return
        }
        if (serviceDesks < 1) {
            showAlert("Service desks count must be positive number.", "Wrong service desk count")
            return
        }

        simulationApi.setOpenTime(LocalTime.of(9, 0))
        simulationApi.setLastTicketTime(LocalTime.of(17, 0))
        simulationApi.setCloseTime(LocalTime.of(17, 30))
        simulationApi.changeMode(EventSimulationMode.REPLICATIONS)

        spinner.showSpinner(true, spinnerLabel)
        expStartButton.disable()
        GlobalScope.launch {
            for (i in start.rangeTo(end)) {
                val sim = async {
                    simulationApi.setEntryParameters(replicationsGraph.text.toInt(), serviceDesks, i)
                    simulationApi.runSimulation()
                }
                simulationApi.observeSimulation(i.toString()) { state ->
                    val simState = state as CompanyEventSimulation.CompanySimulationState
                    if (simState.state == SimulationState.STOPPED) {
                        Platform.runLater {
                            series.add(i, simState.overallStats.avgTicketQueueLength.mean.round(3))
                        }
                    }
                }

                sim.await()
                simulationApi.stopObservingSimulation(i.toString())
            }
            spinner.showSpinner(false, spinnerLabel)
            expStartButton.disable(false)
        }
    }

    private fun createSimulationObserver() {
        simulationApi.observeSimulation("observer") { state ->
            Platform.runLater {
                val simState = state as CompanyEventSimulation.CompanySimulationState
                if (!modeR.isSelected) {
                    simulationTime.text = "Time: ${simState.time.format()}"

                    // customers
                    customers = FXCollections.observableArrayList(simState.customers)
                    customersTable.items = customers

                    //employees
                    employees = FXCollections.observableArrayList(simState.employees)
                    empTable.items = employees

                    with(simState.replicationStats) {
                        repSystemTime.text = avgSystemTime.toOwnString()
                        repTicketTime.text = avgTicketQueueTime.toOwnString()
                        repCashDeskTime.text = avgCashDeskQueueTime.toOwnString()
                        repLastExit.text = lastCustomerExit
                    }
                } else {
                    // to overall stats
                    with(simState.overallStats) {
                        workloads = FXCollections.observableArrayList(listOf(avgTicketMachineWorkload) + avgCashDeskWorkload + avgServiceDeskWorkload)
                        workloadsTable.items = workloads

                        queueLengths = FXCollections.observableArrayList(listOf(avgTicketQueueLength) + avgCashDeskQueueLength)
                        queueLengthsTable.items = queueLengths

                        ovSystemTime.text = avgSystemTime.toOwnString(true)
                        ovTicketTime.text = avgTicketQueueTime.toOwnString()
                        ovCashDeskTime.text = avgCashDeskQueueTime.toOwnString()
                        ovLastExit.text = avgLastCustomerExit.toOwnTimeString()
                    }
                }

                stateDisabling(simState.state)
                simState.speed.toSpeedLabel()
                speed.value = simState.speed.round(2)
                repsExecuted.text = "Replications executed: ${simState.replicationsExecuted}"
                repNumber.text = "Replication number: ${simState.replicationsExecuted + 1}"

                this.state = simState.state
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

        workloadsTable.selectionModel.selectionMode = SelectionMode.SINGLE
        worklName.setCellValueFactory { cellData -> SimpleStringProperty(cellData.value.name ?: "Not filled") }
        worklMean.setCellValueFactory { cellData -> SimpleStringProperty(cellData.value.mean.toString()) }
        worklMax.setCellValueFactory { cellData -> SimpleStringProperty(cellData.value.max.toString()) }
        worklMin.setCellValueFactory { cellData -> SimpleStringProperty(cellData.value.min.toString()) }

        queueLengthsTable.selectionModel.selectionMode = SelectionMode.SINGLE
        queueName.setCellValueFactory { cellData -> SimpleStringProperty(cellData.value.name ?: "Not filled") }
        queueMean.setCellValueFactory { cellData -> SimpleStringProperty(cellData.value.mean.toString()) }
        queueMax.setCellValueFactory { cellData -> SimpleStringProperty(cellData.value.max.toString()) }
        queueMin.setCellValueFactory { cellData -> SimpleStringProperty(cellData.value.min.toString()) }
    }

    private fun initInputs() {
        replications.allowOnlyInt()
        serviceDesksCount.allowOnlyInt()
        cashDesksCount.allowOnlyInt()
        waitingAreaCount.allowOnlyInt()
        replicationsGraph.allowOnlyInt()
        minCashDeskCount.allowOnlyInt()
        maxCashDeskCount.allowOnlyInt()
        serviceDesks.allowOnlyInt()
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

        val jFreeChart = ChartFactory.createXYLineChart(
            "Company event simulation",
            "Cash desks",
            "Average ticket machine queue",
            dataset
        )

        val renderer = XYLineAndShapeRenderer(true, false)
        renderer.defaultItemLabelGenerator = XYItemLabelGenerator { set, x, y -> set.getYValue(x, y).toString() }
        renderer.defaultItemLabelsVisible = true
        renderer.setSeriesShapesVisible(0, true)
        jFreeChart.xyPlot.renderer = renderer

        //auto range for the y-axis
        val yAxis = jFreeChart.xyPlot.rangeAxis as NumberAxis
        yAxis.autoRangeIncludesZero = false
        yAxis.isAutoRange = true

        val xAxis = jFreeChart.xyPlot.domainAxis as NumberAxis
        xAxis.range = Range(minCashDeskCount.text.toDouble() - 1, maxCashDeskCount.text.toDouble() + 1)
        xAxis.tickUnit = NumberTickUnit(1.0)
        xAxis.isAutoRange = false

        with(jFreeChart.plot as XYPlot) {
            backgroundPaint = Color.WHITE
            renderer.setSeriesStroke(0, BasicStroke(1.75f))
            renderer.setSeriesPaint(0, color)
        }
        chart.chart = jFreeChart
        chart.isVisible = true
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