package fri.uniza.semestralka2.simulation.event

import fri.uniza.semestralka2.simulation.CompanyEventSimulation
import fri.uniza.semestralka2.simulation.objects.customer.CustomerType

class OpenOnlineServingDeskEvent(
    time: Double,
    core: CompanyEventSimulation
) : CompanyEvent(time, core) {

    override fun onExecute() {
        core.openAnotherPlace(time, arrayOf(CustomerType.ONLINE))
    }
}