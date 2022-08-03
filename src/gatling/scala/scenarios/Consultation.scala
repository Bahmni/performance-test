package scenarios

import configurations.{Load, MaximumResponseTimes, Possibility}
import io.gatling.core.Predef._
import io.gatling.core.structure.PopulationBuilder
import registries.Doctor.{home, search}
import scenarios.BaseScenario.setupScenario

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object Consultation {
  private val possibilities = List(
    Possibility(searchBahmni, 80),
    Possibility(visitGoogle, 20)
  )

  def scenario(loadSharePercentage: Int): PopulationBuilder =
    setupScenario("Consultation", Load.getTrafficShareConfiguration(loadSharePercentage), possibilities)

  private def searchBahmni(expectedResTimes: MaximumResponseTimes) = {
    exec(home(expectedResTimes))
      .pause(1 second)
      .exec(search(expectedResTimes))
  }

  private def visitGoogle(expectedResTimes: MaximumResponseTimes) = {
    exec(home(expectedResTimes))
  }
}
