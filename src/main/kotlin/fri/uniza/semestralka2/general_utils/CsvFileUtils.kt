package fri.uniza.semestralka2.general_utils

import java.io.BufferedWriter
import java.io.FileWriter
import java.io.IOException

/**
 * Simple writer of [Double] values to CSV file.
 */
fun writeToCsv(fileName: String, data: List<Double>, replaceDot: Boolean = false) {
    if (data.isEmpty()) return

    try {
        BufferedWriter(FileWriter(fileName)).use { writer ->
            data.forEach { value ->
                val formattedValue = if (replaceDot) value.toString().replace(".", ",") else value.toString()
                writer.write(formattedValue)
                writer.newLine()
            }
            println("Data has been written to $fileName")
        }
    } catch (e: IOException) {
        println("Error writing to file: ${e.message}")
    }
}