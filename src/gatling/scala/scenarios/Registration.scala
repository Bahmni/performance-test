package scenarios

import configurations.{Scenario, ScenarioWorkLoad, UserFlow}
import io.gatling.core.Predef._
import io.gatling.core.structure.PopulationBuilder
import registries.Common._
import registries.FrontDesk._
import scenarios.BaseScenario.{handleWorkLoad, setupScenario}

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object Registration {
  private val scenarios = List(
    Scenario(
      existingPatient_IdSearch_StartVisit,
      UserFlow.Registration.existingPatientIdSearchOpdVisit
    ),
    Scenario(
      existingPatient_NameSearch_StartVisit,
      UserFlow.Registration.existingPatientNameSearchOpdVisit
    ),
    Scenario(
      createPatient_StartVisit,
      UserFlow.Registration.newPatientOpdVisit
    )
  )

  def scenario(trafficSharePercentage: Int): List[PopulationBuilder] = {
    scenarios.map(scn => setupScenario(scn, trafficSharePercentage))
  }

  private def existingPatient_IdSearch_StartVisit(workLoad: ScenarioWorkLoad) = {
    handleWorkLoad(
      exec(login)
        .feed(csv("registrations.csv").circular)
        .exec(goToHomePage)
        .exec(pause(10 seconds)),
      workLoad
    )
  }

  private def existingPatient_NameSearch_StartVisit(workLoad: ScenarioWorkLoad) = {
    handleWorkLoad(
      exec(login)
        .feed(csv("registrations.csv").circular)
        .exec(goToHomePage),
      workLoad
    )
  }

  private def createPatient_StartVisit(workLoad: ScenarioWorkLoad) = {
    handleWorkLoad(
      exec(login)
        .exec(goToHomePage)
        .exec(gotoCreatePatientPage)
        .exec(pause(10 seconds)),
//        .feed(jsonFeeder)
//        .exec(createPatient)
//        .exec(pause(10 seconds))
//        .exec(startVisitForCreatePatient),
      workLoad
    )
  }
}
