package scenarios

import configurations.Feeders.jsonFeeder
import configurations.{Load, MaximumResponseTimes, Possibility}
import io.gatling.core.Predef._
import io.gatling.core.structure.PopulationBuilder
import registries.Common._
import registries.Doctor._
import registries.Frontdesk.getPatientImages
import scenarios.BaseScenario.setupScenario

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.language.postfixOps

object Consultation {
  private val possibilities = List(
    Possibility(doctor_Consultation_Average_Patient, 100)
  )

  def scenario(loadSharePercentage: Int): PopulationBuilder =
    setupScenario("Consultation", Load.getTrafficShareConfiguration(loadSharePercentage), possibilities)

  def doctor_Consultation_Average_Patient(expectedResTimes: MaximumResponseTimes) ={
    exec(login)
      .exec(goToHomePage)
      .pause(5 seconds , 10 seconds)
      .exec(goToClinicalApp)
      .exec(goToClinicalSearch)
      .exec(getPatientImages)
      .pause(5 seconds , 10 seconds)
      .exec(goToDashboard("#{opdPatientId}"))
      .exec(setSession())
      .exec(setVisit("#{opdPatientId}"))
      .pause(5 seconds , 10 seconds)
      .exec(goToObservations("#{opdPatientId}"))
      .exec(goToMedications("#{opdPatientId}"))
      .exec(addDrug("Reglan Tablet"))
      .exec(addDrug("Loperamide Plus"))
      .exec(addDrug("Promethazine"))
      .feed(jsonFeeder)
      .pause(20 seconds , 30 seconds)
      .exec(saveEncounter)
      .pause(2 seconds , 4 seconds)
      .exec(goToDashboard("#{opdPatientId}"))
      .pause(2 seconds , 4 seconds)
      .exec(closeVisit())
      .pause(4 seconds , 6 seconds)
  }
}
