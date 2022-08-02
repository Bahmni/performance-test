package simulations

import infrastructure.{BaseSimulation, Load}
import registries.Google

class HighTrafficLoad extends BaseSimulation {
  setUp("Google High Traffic", Load.High, Google.possibleCalls)
}
