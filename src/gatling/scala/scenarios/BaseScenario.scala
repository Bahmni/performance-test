package scenarios

import configurations.{Possibilities, Possibility, Protocols, TrafficShareConfiguration}
import io.gatling.core.Predef._
import io.gatling.core.structure.PopulationBuilder
import scala.language.postfixOps

object BaseScenario {
  def setupScenario(
      name: String,
      load: TrafficShareConfiguration,
      possibilities: List[Possibility]
  ): PopulationBuilder = {
    scenario(name)
      .exec(randomSwitch(Possibilities.balance(load, possibilities): _*))
      .inject(
        //Open System - this would produce more load
        // rampUsers(20) during 180.seconds
//        constantUsersPerSec(10) during 1.minutes
        //Closed System
        rampConcurrentUsers(0).to(load.activeUsers).during(load.initialRampUpDuration),
        constantConcurrentUsers(load.activeUsers).during(load.totalDuration)
        //atOnceUsers(1)

      )
      .protocols(Protocols.default)
  }
}
