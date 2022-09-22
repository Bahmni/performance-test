package scenarios

import configurations.{Possibilities, Possibility, Protocols, TrafficShareConfiguration}
import io.gatling.core.Predef._
import io.gatling.core.structure.PopulationBuilder

import scala.concurrent.duration.FiniteDuration
import scala.language.postfixOps

object BaseScenario {
  def setupScenario(
                     name: String,
                     rate: FiniteDuration,
                     load: TrafficShareConfiguration,
                     possibilities: List[Possibility]
  ): PopulationBuilder = {
    scenario(name)
      .forever {
        pace(rate).exec(randomSwitch(Possibilities.balance(load, possibilities): _*))
      }
      .inject(
        rampConcurrentUsers(0).to(load.activeUsers).during(load.initialRampUpDuration),
        constantConcurrentUsers(load.activeUsers).during(load.totalDuration)
      )
      .protocols(Protocols.default)
  }

}
