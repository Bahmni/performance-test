package scenarios

import configurations.Feeders.jsonFeeder
import configurations.{Load, MaximumResponseTimes, Possibility}
import io.gatling.core.Predef._
import io.gatling.core.structure.PopulationBuilder
import registries.Common._
import registries.Doctor._
import scenarios.BaseScenario.setupScenario
import scala.language.postfixOps
import scala.language.postfixOps

object Consultation {
  private val possibilities = List(
    Possibility(doctor_Consultation_Average_Patient, 100)
  )

  def scenario(loadSharePercentage: Int): PopulationBuilder =
    setupScenario("Consultation", Load.getTrafficShareConfiguration(loadSharePercentage), possibilities)

  def doctor_Consultation_Average_Patient(expectedResTimes: MaximumResponseTimes) = {
       exec(setStartTime())
      .exec(login)
      .exec(goToHomePage)
     //.exec(pauseRemainingTime(60))
      .exec(goToClinicalApp)
      .exec(refreshInMemoryOpdPatientQueue)
      .exec(selectNextPatientFromOpdQueue)
     // .exec(pauseRemainingTime(240))
      .exec(goToDashboard("#{opdPatientId}"))
      .exec(setSession())
      .exec(setVisit("#{opdPatientId}"))
    //  .exec(pauseRemainingTime(420))
      .exec(goToObservations("#{opdPatientId}"))
      .exec(goToMedications("#{opdPatientId}"))
      .exec(addDrug("Reglan Tablet"))
      .exec(addDrug("Loperamide Plus"))
      .exec(addDrug("Promethazine"))
      .feed(jsonFeeder)
      //.exec(pauseRemainingTime(660))
      .exec(saveEncounter)
     // .exec(pauseRemainingTime(720))
      .exec(goToDashboard("#{opdPatientId}"))
      //.exec(pauseRemainingTime(800))
      .exec(closeVisit())
      //.exec(pauseRemainingTime(900))

  }
}
