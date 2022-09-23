package scenarios

import configurations.Feeders.jsonFeeder
import configurations.{Load, MaximumResponseTimes, Possibility}
import io.gatling.core.Predef._
import io.gatling.core.structure.PopulationBuilder
import registries.Common._
import registries.Doctor._
import scenarios.BaseScenario.setupScenario

import scala.concurrent.duration.{Duration, DurationLong, FiniteDuration, SECONDS}
import scala.language.postfixOps
import api.Constants._

object Consultation {
  private val possibilities = List(
    Possibility(doctor_Consultation_Average_Patient, 100)
  )

  val setRate = {
    val trafficConfig = Load.getLoadParameters
    var actualPace: FiniteDuration = Duration(((trafficConfig.duration * (CONSULTATION_LOAD_SHARE/100.00 * trafficConfig.activeUsers)) / (0.272*trafficConfig.patients)).toSeconds,SECONDS)
    if (actualPace.gteq(5 minutes)) actualPace = 5 minutes
    else if (actualPace.lteq(2 minutes)) actualPace = 2 minutes

    println("Patients at consultation : "+(0.27*trafficConfig.patients))
    println("Time available for consultation : "+(trafficConfig.duration * ((CONSULTATION_LOAD_SHARE/100.00) * trafficConfig.activeUsers)).toSeconds)
    println("Consultation Pace : " + actualPace.toSeconds)
    actualPace
  }

  def scenario(loadSharePercentage: Int): PopulationBuilder =
    setupScenario("Consultation", setRate, Load.getTrafficShareConfiguration(loadSharePercentage), possibilities)

  def doctor_Consultation_Average_Patient(expectedResTimes: MaximumResponseTimes) = {
    exec(setStartTime())
      .exec(login)
      .exec(goToHomePage)
      .exec(pauseRemainingTime(10))
      .exec(goToClinicalApp)
      .exec(refreshInMemoryOpdPatientQueue)
      .exec(selectNextPatientFromOpdQueue)
      .exec(pauseRemainingTime(20))
      .exec(goToDashboard("#{opdPatientId}"))
      .exec(setSession())
      .exec(setVisit("#{opdPatientId}"))
      .exec(pauseRemainingTime(30))
      .exec(goToObservations("#{opdPatientId}"))
      .exec(goToMedications("#{opdPatientId}"))
      .exec(addDrug("Reglan Tablet"))
      .exec(addDrug("Loperamide Plus"))
      .exec(addDrug("Promethazine"))
      .feed(jsonFeeder)
      .exec(pauseRemainingTime(60))
      .exec(saveEncounter)
      .exec(pauseRemainingTime(80))
      .exec(goToDashboard("#{opdPatientId}"))
      .exec(pauseRemainingTime(100))
      .exec(closeVisit())
  }
}
