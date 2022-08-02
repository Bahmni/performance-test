package simulations

import io.gatling.core.Predef._
import scenarios.{GoogleSearch, Registration}

class FrontDesk extends Simulation {
  setUp(
    Registration.scenario,
    GoogleSearch.scenario
  )
}
