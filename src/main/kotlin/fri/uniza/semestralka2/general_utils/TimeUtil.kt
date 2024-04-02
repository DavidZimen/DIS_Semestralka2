package fri.uniza.semestralka2.general_utils

import java.time.LocalTime

/**
 * Hours per day
 */
private const val HOURS_PER_DAY = 24

/**
 * Minutes per hour.
 */
private const val MINUTES_PER_HOUR = 60

/**
 * Seconds per minute.
 */
private const val SECONDS_PER_MINUTE = 60

/**
 * Seconds per hour.
 */
private const val SECONDS_PER_HOUR = SECONDS_PER_MINUTE * MINUTES_PER_HOUR

/**
 * Seconds per day.
 */
private const val SECONDS_PER_DAY = SECONDS_PER_HOUR * HOURS_PER_DAY

/**
 * Milliseconds per second.
 */
private const val MILLIS_PER_SECOND = 1000L

fun LocalTime.toHours(): Double {
    var result = 0.0
    result += hour
    result += minute / MINUTES_PER_HOUR
    result += second / SECONDS_PER_HOUR
    return result
}

fun LocalTime.toMinutes(): Double {
    var result = 0.0
    result += hour * MINUTES_PER_HOUR
    result += minute
    result += second / SECONDS_PER_MINUTE
    return result
}

fun LocalTime.toSeconds(): Double {
    var result = 0.0
    result += hour * SECONDS_PER_HOUR
    result += minute * SECONDS_PER_MINUTE
    result += second
    return result
}

fun Double.secondsToLocalTime(): LocalTime {
    val hours = (this / SECONDS_PER_HOUR).toInt()
    val minutes = ((this % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE).toInt()
    val seconds = (this % SECONDS_PER_MINUTE).toInt()

    return LocalTime.of(hours, minutes, seconds)
}

fun Double.minutesToLocalTime(): LocalTime {
    val minutes = this.toInt()
    val hours = minutes / 60
    val remainingMinutes = minutes % 60
    return LocalTime.of(hours, remainingMinutes)
}

fun Double.hoursToMinutes() = this * MINUTES_PER_HOUR

fun Double.hoursToSeconds() = this.hoursToMinutes().minutesToSeconds()

fun Double.hoursToMillis() = this.hoursToSeconds().secondsToMillis()

fun Double.minutesToSeconds()  = this * SECONDS_PER_MINUTE

fun Double.minutesToMillis() = this.minutesToSeconds().secondsToMillis()

fun Double.minutesToHours() = this / MINUTES_PER_HOUR

fun Double.secondsToMillis() = this * MILLIS_PER_SECOND

fun Double.secondsToMinutes() = this / SECONDS_PER_MINUTE

fun Double.secondsToHours() = this.secondsToMinutes().millisToHours()

fun Double.millisToSeconds() = this / MILLIS_PER_SECOND

fun Double.millisToMinutes() = this.millisToSeconds().secondsToMinutes()

fun Double.millisToHours() = this.millisToMinutes().minutesToHours()