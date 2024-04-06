package fri.uniza.semestralka2.simulation.objects.dto

import fri.uniza.semestralka2.core.Service
import fri.uniza.semestralka2.general_utils.round
import java.math.RoundingMode


/**
 * Dto for transferring [Service] data to GUI.
 */
data class ServiceDto(
    val name: String,
    val queueLength: String,
    val state: String,
    val workload: String
)

/**
 * Maps [Service] to [ServiceDto].
 */
fun <T> Service<T>.toDto() = ServiceDto(
    name,
    queueStats.mean.round(3).toString(),
    state.toString(),
    workload.averageWorkload.round(3, RoundingMode.HALF_UP).toString()
)