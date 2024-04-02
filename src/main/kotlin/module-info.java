module fri.uniza.semestralka2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;
    requires commons.math3;
    requires kotlinx.coroutines.core;


    opens fri.uniza.semestralka2 to javafx.fxml;
    exports fri.uniza.semestralka2;
}