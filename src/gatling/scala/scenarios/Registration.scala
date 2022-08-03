package scenarios

import configurations.{Feeders, Load, MaximumResponseTimes, Possibility}
import io.gatling.core.Predef._
import io.gatling.core.structure.PopulationBuilder
import registries.Common.login
import registries.Frontdesk.goToHomePage
import scenarios.BaseScenario.setupScenario

object Registration {
  private val possibilities = List(
    Possibility(existingPatient_NameSearch_StartVisit, 100)
  )

  def scenario(loadSharePercentage: Int): PopulationBuilder =
    setupScenario("Registration", Load.getTrafficShareConfiguration(loadSharePercentage), possibilities)

  private def existingPatient_NameSearch_StartVisit(expectedResTimes: MaximumResponseTimes) = {
    exec(login(expectedResTimes))
      .feed(Feeders.patientName)
      .exec(goToHomePage(expectedResTimes))
      .pause(20) //human factor
  }

}
