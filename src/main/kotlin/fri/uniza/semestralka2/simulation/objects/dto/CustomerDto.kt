package fri.uniza.semestralka2.simulation.objects.dto

import fri.uniza.semestralka2.general_utils.format
import fri.uniza.semestralka2.general_utils.secondsToLocalTime
import fri.uniza.semestralka2.simulation.objects.customer.Customer

/**
 * Dto for transferring [Customer] data to GUI.
 */
data class CustomerDto(
    val name: String,
    val arrivalTime: String,
    val type: String,
    val orderType: String,
    val orderSize: String,
    val serviceDesk: String,
    val cashDeskQueue: String,
    val cashDesk: String,
    val state: String
)

/**
 * Maps [Customer] to [CustomerDto].
 */
fun Customer.toDto() = CustomerDto(
    "Customer $id",
    systemStartTime.secondsToLocalTime().format(),
    type.toString(),
    orderType.toString(),
    orderSize.toString(),
    serviceDesk?.name ?: "",
    cashDeskQueue?.name ?: "",
    cashDeskPaid?.name ?: "",
    state.toString()
)