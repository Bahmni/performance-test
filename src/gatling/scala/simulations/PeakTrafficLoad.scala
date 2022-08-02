package simulations

import infrastructure.{BaseSimulation, Load}
import registries.Explore

class PeakTrafficLoad extends BaseSimulation {
  setUp("Explore Peak Traffic Load", Load.Peak, Explore.possibleCalls)
}
