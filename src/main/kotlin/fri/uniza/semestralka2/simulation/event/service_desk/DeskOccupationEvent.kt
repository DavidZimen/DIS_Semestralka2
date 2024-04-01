package fri.uniza.semestralka2.simulation.event.service_desk

import fri.uniza.semestralka2.general_utils.round
import fri.uniza.semestralka2.simulation.CompanyEventSimulation
import fri.uniza.semestralka2.simulation.components.CashDesk
import fri.uniza.semestralka2.simulation.event.CompanyEvent
import fri.uniza.semestralka2.simulation.objects.customer.Customer
import java.math.RoundingMode

abstract class DeskOccupationEvent(
    time: Double,
    protected val customer: Customer,
    core: CompanyEventSimulation
) : CompanyEvent(time, core) {

    protected fun chooseCashDesk(): CashDesk? {
        val min = core.cashDesks.minOf { it.queueLength }
        val minimalQueues = core.cashDesks.filter { it.queueLength == min }

        // no queues choose between free desks
        if (min == 0 && minimalQueues.size == core.cashDeskCount) {
            val notOccupied = minimalQueues.filter { !it.isOccupied }

            // every desk is occupied, so choose randomly
            if (notOccupied.isEmpty()) {
                minimalQueues[cashDeskIndex(minimalQueues.size)].addToQueue(customer)
                return null
            }

            val cashDesk = if (notOccupied.size == 1) {
                notOccupied[0]
            } else {
                notOccupied[cashDeskIndex(notOccupied.size)]
            }

            if (cashDesk.isOccupied) {
                cashDesk.addToQueue(customer)
                return null
            } else {
                return cashDesk
            }
        }

        // empty return null
        if (minimalQueues.isEmpty()) {
            return null
        }

        val cashDesk = if (minimalQueues.size == 1) {
            minimalQueues[0]
        } else {
            minimalQueues[cashDeskIndex(minimalQueues.size)]
        }

        if (cashDesk.isOccupied) {
            cashDesk.addToQueue(customer)
            return null
        } else {
            return cashDesk
        }
    }

    private fun cashDeskIndex(size: Int): Int {
        return (size * core.cashDeskChooseGenerator.sample()).round(0, RoundingMode.FLOOR).toInt()
    }
}