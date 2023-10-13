package scenarios

import configurations.Feeders._
import configurations.{Scenario, ScenarioWorkLoad, UserFlow}
import io.gatling.core.Predef._
import io.gatling.core.structure.PopulationBuilder
import registries.Common._
import registries.Doctor._
import scenarios.BaseScenario.setupScenario
import scala.language.postfixOps
import scenarios.BaseScenario.{handleWorkLoad}
import scala.language.postfixOps

object Consultation {
  private val scenarios = List(
    Scenario(
      doctor_Consultation_Average_Patient,
      UserFlow.Consultation.averagePatient
    )
  )

  def scenario(trafficSharePercentage: Int): List[PopulationBuilder] = {
    scenarios.map(scn => setupScenario(scn, trafficSharePercentage))
  }
  def doctor_Consultation_Average_Patient(workLoad: ScenarioWorkLoad) = {
    handleWorkLoad(
      exec(waitBeforeNextStep(0, 60))
        .exec(login)
        .exec(goToHomePage)
        .exec(waitBeforeNextStep(0, 30))
        .exec(goToClinicalApp)
        .exec(waitBeforeNextStep(0, 30))
        .exec(refreshInMemoryOpdPatientQueue)
        .exec(selectNextPatientFromOpdQueue)
        .exec(goToDashboard("#{opdPatientId}"))
        .exec(setSession())
        .exec(setVisit("#{opdPatientId}"))
        .exec(waitBeforeNextStep(0, 30))
        .exec(goToObservations("#{opdPatientId}"))
        .exec(waitBeforeNextStep(0, 30))
        .feed(jsonFeeder)
        .feed(observationsFeeder)
        .exec(waitBeforeNextStep(0, 60))
        .exec(saveEncounter)
        .exec(goToDashboard("#{opdPatientId}"))
          .exec(openTheForm())
        .exec(waitBeforeNextStep(0, 30))
        .exec(closeVisit()),
      workLoad
    )
  }
}
