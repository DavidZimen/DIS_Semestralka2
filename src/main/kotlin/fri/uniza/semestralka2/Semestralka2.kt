package fri.uniza.semestralka2

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage
import java.math.RoundingMode

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
    println((6 / 3.0).toBigDecimal().setScale(0, RoundingMode.FLOOR).toInt())
}