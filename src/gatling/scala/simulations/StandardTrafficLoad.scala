package simulations

import infrastructure.{BaseSimulation, Load}
import registries.Explore

class StandardTrafficLoad extends BaseSimulation {
  setUp("Explore Standard Traffic Load", Load.Standard, Explore.possibleCalls)
}
