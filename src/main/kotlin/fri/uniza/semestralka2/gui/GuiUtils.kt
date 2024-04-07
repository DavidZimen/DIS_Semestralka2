package fri.uniza.semestralka2.gui

import fri.uniza.semestralka2.general_utils.*
import fri.uniza.semestralka2.simulation.objects.dto.StatisticDto
import javafx.scene.control.*
import java.time.LocalTime


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

fun Slider.init(time: LocalTime) {
    min = LocalTime.of(0, 0).toMinutes()
    max = LocalTime.of(23, 59).toMinutes()
    value = time.toMinutes()
    blockIncrement = 5.0
}


fun StatisticDto.toOwnString(confInterval: Boolean = false): String {
    val conf = if (confInterval) {
        "${confidenceInterval.first}% confidence: <${confidenceInterval.second.first} ;${confidenceInterval.second.second}>"
    } else ""
    return "Mean: $mean sec / ${mean.secondsToMinutes().round(3)} min" +
            "\tMin: $min sec / ${min.secondsToMinutes().round(3)} min" +
            "\tMax: $max sec / ${max.secondsToMinutes().round(3)} min" +
            "\n$conf"
}

fun StatisticDto.toOwnTimeString(): String {
    return "Mean: ${mean.secondsToLocalTime()}" +
            "\tMin: ${min.secondsToLocalTime()}" +
            "\tMax: ${max.secondsToLocalTime()}"
}
