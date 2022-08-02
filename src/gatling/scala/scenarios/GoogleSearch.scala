package scenarios

import configurations.{Load, MaximumResponseTimes, Possibility}
import io.gatling.core.Predef._
import io.gatling.core.structure.PopulationBuilder
import registries.GoogleSearch.{home, search}
import scenarios.BaseScenario.setupScenario

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object GoogleSearch {
  private val possibilities = List(
    Possibility(searchBahmni, 80),
    Possibility(visitGoogle, 20)
  )

  private def searchBahmni(expectedResTimes: MaximumResponseTimes) = {
    exec(home(expectedResTimes))
      .pause(1 second)
      .exec(search(expectedResTimes))
  }

  private def visitGoogle(expectedResTimes: MaximumResponseTimes) = {
    exec(home(expectedResTimes))
  }

  val scenario: PopulationBuilder = setupScenario("Google Search", Load.Standard, possibilities)
}
