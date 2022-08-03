package scenarios

import configurations.{Possibilities, Possibility, Protocols, TrafficShareConfiguration}
import io.gatling.core.Predef._
import io.gatling.core.structure.PopulationBuilder

import scala.concurrent.duration.DurationInt
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
//        rampUsers(300) during 30.seconds,
//        constantUsersPerSec(10) during 1.minutes
        //Closed System
        rampConcurrentUsers(0).to(load.activeUsers).during(load.initialRampUpDuration),
        constantConcurrentUsers(load.activeUsers).during(load.totalDuration)
      )
      .protocols(Protocols.default)
  }
}
