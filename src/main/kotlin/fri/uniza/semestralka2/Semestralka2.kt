package fri.uniza.semestralka2

import fri.uniza.semestralka2.gui.GuiController
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage

class Semestralka2 : Application() {
    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(Semestralka2::class.java.getResource("gui.fxml"))
        val scene = Scene(fxmlLoader.load(), 1920.0, 1000.0)
        val controller = fxmlLoader.getController<GuiController>()
        controller.stage = stage
        stage.icons.add(Image(Semestralka2::class.java.getResourceAsStream("icon.png")))
        stage.title = "Event simulation"
        stage.scene = scene
        stage.show()
    }
}

fun main() {
    Application.launch(Semestralka2::class.java)
//    with(CompanyEventSimulation()) {
////        mode = EventSimulationCore.Mode.SINGLE
////        simulationStateObservable.subscribe("Random") { state ->
////            println("\r${(state as CompanyEventSimulation.CompanySimulationState).customers.size}")
////        }
////        speedUpSimulation()
//        replicationsCount = 25000
//        setClosingTime(LocalTime.of(17, 30))
//        cashDeskCount = 4
//        serviceDeskCount = 13
//        runSimulation()
//    }
}