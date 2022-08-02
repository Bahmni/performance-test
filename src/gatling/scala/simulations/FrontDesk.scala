package simulations

import infrastructure.Protocols
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

class FrontDesk extends Simulation {

  val frontDesk: ScenarioBuilder = scenario("FrontDesk")

  setUp(
    frontDesk
      .inject(
        nothingFor(10 seconds),
        rampUsers(1).during(45 seconds)
      )
      .protocols(Protocols.default)
  )
}
