package scenarios

import configurations.Feeders._
import configurations.{Scenario, ScenarioWorkLoad, UserFlow}
import io.gatling.core.Predef._
import io.gatling.core.structure.PopulationBuilder
import registries.Common._
import registries.Doctor._
import scenarios.BaseScenario.setupScenario

import scala.language.postfixOps
import scenarios.BaseScenario.handleWorkLoad

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.util.Random

object Consultation {
  val rnd = new Random()
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
      exec(login)
        .feed(pauseFeeder)
        .exec(goToHomePage)
        .exec(pause("#{pausePeriod}"))
        .exec(goToClinicalApp)
        .exec(refreshInMemoryOpdPatientQueue)
        .exec(selectNextPatientFromOpdQueue)
        .exec(pause("#{pausePeriod}"))
        .exec(goToDashboard("#{opdPatientId}"))
        .exec(setSession())
        .exec(setVisit("#{opdPatientId}"))
        .exec(pause("#{pausePeriod}"))
        .exec(goToObservations("#{opdPatientId}"))
        .exec(goToMedications("#{opdPatientId}"))
        .exec(addDrug("Reglan Tablet"))
        .exec(addDrug("Loperamide Plus"))
        .exec(addDrug("Promethazine"))
        .exec(pause("#{pausePeriod}"))
        .feed(jsonFeeder)
        .feed(observationsFeeder)
        .exec(saveEncounter)
        .exec(goToDashboard("#{opdPatientId}"))
        .exec(pause("#{pausePeriod}"))
        .exec(closeVisit()), workLoad)
  }
}
