package scenarios

import configurations.{Possibilities, Possibility, Protocols, TrafficLoadConfiguration}
import io.gatling.core.Predef._
import io.gatling.core.structure.PopulationBuilder

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object BaseScenario {
  def setupScenario(
      name: String,
      load: TrafficLoadConfiguration,
      possibilities: List[Possibility]
  ): PopulationBuilder = {
    scenario(name)
      .exec(randomSwitch(Possibilities.balance(load, possibilities): _*))
      .inject(
        rampUsers(600) during 1.minutes,
        constantUsersPerSec(10) during 1.minutes
      )
      .protocols(Protocols.default)
  }
}
