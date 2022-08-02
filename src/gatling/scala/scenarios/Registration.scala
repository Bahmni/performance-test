package scenarios

import configurations.{Feeders, Load, MaximumResponseTimes, Possibility}
import io.gatling.core.Predef._
import io.gatling.core.structure.PopulationBuilder
import registries.Common.login
import registries.Registration.goToHomePage
import scenarios.BaseScenario.setupScenario

object Registration {
  private val possibilities = List(
    Possibility(newPatient_Manual, 100)
  )

  private def newPatient_Manual(expectedResTimes: MaximumResponseTimes) = {
    exec(login(expectedResTimes))
      .feed(Feeders.patientName)
      .exec(goToHomePage(expectedResTimes))
      .pause(20) //human factor
  }

  val scenario: PopulationBuilder = setupScenario("Registration", Load.Standard, possibilities)
}
