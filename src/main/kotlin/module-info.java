module fri.uniza.semestralka2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;
    requires commons.math3;
    requires kotlinx.coroutines.core;
    requires org.jfree.chart.fx;
    requires org.jfree.jfreechart;
    requires java.desktop;


    opens fri.uniza.semestralka2 to javafx.fxml;
    opens fri.uniza.semestralka2.gui to javafx.fxml;
    exports fri.uniza.semestralka2;
}