package scenarios

import configurations.Feeders.jsonFeeder
import configurations.{Load, MaximumResponseTimes, Possibility}
import io.gatling.core.Predef._
import io.gatling.core.structure.PopulationBuilder
import registries.Common._
import registries.Doctor._
import scenarios.BaseScenario.setupScenario
import simulations.BahmniClinic

import scala.concurrent.duration.{DurationLong, FiniteDuration}
import scala.language.postfixOps
import scala.language.postfixOps
import api.Constants._

object Consultation {
  private val possibilities = List(
    Possibility(doctor_Consultation_Average_Patient, 100)
  )

  val setRate = {
    val trafficConfig = Load.getLoadParameters
    var actualPace: FiniteDuration = (trafficConfig.duration * ((CONSULTATION_LOAD_SHARE/100) * trafficConfig.activeUsers)) / (0.27*trafficConfig.patients).round
    if (actualPace.gteq(5 minutes)) actualPace = 5 minutes
    else if (actualPace.lteq(2 minutes)) actualPace = 2 minutes

    actualPace
  }

  def scenario(loadSharePercentage: Int): PopulationBuilder =
    setupScenario("Consultation", setRate, Load.getTrafficShareConfiguration(loadSharePercentage), possibilities)

  def doctor_Consultation_Average_Patient(expectedResTimes: MaximumResponseTimes) = {
    exec(setStartTime())
      .exec(login)
      .exec(goToHomePage)
      .pause(5 seconds)
      .exec(goToClinicalApp)
      .exec(refreshInMemoryOpdPatientQueue)
      .exec(selectNextPatientFromOpdQueue)
      // .exec(pauseRemainingTime(240))
      .pause(10 seconds)
      .exec(goToDashboard("#{opdPatientId}"))
      .exec(setSession())
      .exec(setVisit("#{opdPatientId}"))
      //  .exec(pauseRemainingTime(420))
      .pause(5 seconds)
      .exec(goToObservations("#{opdPatientId}"))
      .exec(goToMedications("#{opdPatientId}"))
      .exec(addDrug("Reglan Tablet"))
      .exec(addDrug("Loperamide Plus"))
      .exec(addDrug("Promethazine"))
      .feed(jsonFeeder)
      //.exec(pauseRemainingTime(660))
      .pause(40 seconds)
      .exec(saveEncounter)
      // .exec(pauseRemainingTime(720))
      .exec(goToDashboard("#{opdPatientId}"))
      //.exec(pauseRemainingTime(800))
      .exec(closeVisit())
    //.exec(pauseRemainingTime(900))

  }
}
