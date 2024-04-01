package fri.uniza.semestralka2

import fri.uniza.semestralka2.simulation.CompanyEventSimulation
import fri.uniza.semestralka2.simulation.core.EventSimulationCore
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage

class Semestralka2 : Application() {
    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(Semestralka2::class.java.getResource("gui.fxml"))
        val scene = Scene(fxmlLoader.load(), 320.0, 240.0)
        stage.title = "Hello!"
        stage.scene = scene
        stage.show()
    }
}

fun main() {
//    Application.launch(Semestralka2::class.java)
    with(CompanyEventSimulation()) {
        mode = EventSimulationCore.Mode.SINGLE
        simulationStateObservable.subscribe("Random") { state ->
            println("\r${state!!.time}")
        }
        speedUpSimulation()
        runSimulation()
    }
}