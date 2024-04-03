package fri.uniza.semestralka2.general_utils

import org.apache.commons.math3.util.Precision
import java.math.RoundingMode

private const val EPSILON = 0.0000001
val DOUBLE_REGEX = Regex("(0|([1-9][0-9]*))(\\\\.[0-9]+)?\$")
val INTEGER_REGEX = Regex("^[1-9]\\d*|0$")

fun isAGreaterOrEqualsToB(a: Double, b: Double): Boolean {
    return Precision.compareTo(a, b, EPSILON) != -1
}

fun isALessOrEqualsToB(a: Double, b: Double): Boolean {
    return Precision.compareTo(a, b, EPSILON) != 1
}

fun isAGreaterThanB(a: Double, b: Double): Boolean {
    return Precision.compareTo(a, b, EPSILON) == 1
}

fun isALessThanB(a: Double, b: Double): Boolean {
    return Precision.compareTo(a, b, EPSILON) == -1
}

fun isALessThanBComparator(a: Double, b: Double): Int {
    return Precision.compareTo(a, b, EPSILON)
}

fun isAEqualsToB(a: Double, b: Double): Boolean {
    return Precision.equals(a, b, EPSILON)
}

fun isABetweenBandC(a: Double, b: Double, c: Double): Boolean {
    var newB = b
    var newC = c
    if (isAGreaterThanB(a, c)) {
        newB = c
        newC = b
    }
    return isAGreaterOrEqualsToB(a, newB) && isALessOrEqualsToB(a, newC)
}

fun Double.round(decimalPlaces: Int, mode: RoundingMode = RoundingMode.HALF_UP): Double {
    return this.toBigDecimal().setScale(decimalPlaces, mode).toDouble()
}
