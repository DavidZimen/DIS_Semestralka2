package fri.uniza.semestralka2.gui

import fri.uniza.semestralka2.general_utils.DOUBLE_REGEX
import fri.uniza.semestralka2.general_utils.INTEGER_REGEX
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.TextField


fun TextField.allowOnlyDouble() {
    textProperty().addListener { _, _, newValue ->
        if (!newValue.matches(DOUBLE_REGEX)) {
            text = newValue.replace(Regex("[^0-9.]"), "")
        }
    }
}

fun TextField.allowOnlyInt() {
    textProperty().addListener { _, _, newValue ->
        if (!newValue.matches(INTEGER_REGEX)) {
            text = newValue.replace(Regex("[^0-9]"), "")
        }
    }
}

fun ProgressIndicator.showSpinner(show: Boolean, label: Label? = null) {
    isVisible = show
    label?.isVisible = show
}

fun Button.disable(disable: Boolean = true) {
    isDisable = disable
}

fun TextField.disable(disable: Boolean = true) {
    isDisable = disable
}
