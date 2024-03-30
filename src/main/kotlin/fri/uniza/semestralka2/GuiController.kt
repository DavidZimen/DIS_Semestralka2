package fri.uniza.semestralka2

import javafx.fxml.FXML
import javafx.scene.control.Label

class GuiController {
    @FXML
    private lateinit var welcomeText: Label

    @FXML
    private fun onHelloButtonClick() {
        welcomeText.text = "Welcome to JavaFX Application!"
    }
}