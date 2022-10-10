package scenarios

import configurations.{Scenario, ScenarioWorkLoad, UserFlow}
import io.gatling.core.Predef._
import io.gatling.core.structure.PopulationBuilder
import registries.Common._
import registries.Doctor._
import scenarios.BaseScenario.{handleWorkLoad, setupScenario}

import scala.concurrent.duration.DurationInt
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

  private def doctor_Consultation_Average_Patient(workLoad: ScenarioWorkLoad) = {
    handleWorkLoad(
      exec(login)
        .exec(goToHomePage)
        .exec(pause(10 seconds))
        .exec(goToClinicalApp),
//        .exec(refreshInMemoryOpdPatientQueue)
//        .exec(selectNextPatientFromOpdQueue)
//        .exec(pause(10 seconds))
//        .exec(goToDashboard("#{opdPatientId}"))
//        .exec(setSession())
//        .exec(setVisit("#{opdPatientId}"))
//        .exec(pause(10 seconds))
//        .exec(goToObservations("#{opdPatientId}"))
//        .exec(goToMedications("#{opdPatientId}"))
//        .exec(addDrug("Reglan Tablet"))
//        .exec(addDrug("Loperamide Plus"))
//        .exec(addDrug("Promethazine"))
//        .feed(jsonFeeder)
//        .exec(pause(10 seconds))
//        .exec(saveEncounter)
//        .exec(pause(10 seconds))
//        .exec(goToDashboard("#{opdPatientId}"))
//        .exec(pause(10 seconds))
//        .exec(closeVisit()),
      workLoad
    )
  }
}
