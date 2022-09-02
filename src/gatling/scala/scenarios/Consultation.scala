package scenarios

import configurations.Feeders.jsonFeeder
import configurations.{Load, MaximumResponseTimes, Possibility}
import io.gatling.core.Predef._
import io.gatling.core.structure.PopulationBuilder
import registries.Common._
import registries.Doctor._
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
      .pause(5 seconds , 10 seconds)
      .exec(goToDashboard("#{opdPatientId}"))
      .exec(setSession())
      .exec(setVisit("#{opdPatientId}"))
      .pause(5 seconds , 10 seconds)
      .exec(goToObservations("#{opdPatientId}"))
      .pause(5 seconds , 10 seconds)
      .feed(jsonFeeder)
      .exec(saveEncounter)
      .pause(5 seconds , 10 seconds)
      .exec(setOrders)
      .feed(jsonFeeder)
      .exec(saveEncounter)
      .pause(5 seconds , 10 seconds)
      .exec(goToMedications("#{opdPatientId}"))
      .exec(addDrug("Dolo"))
      .exec(setMedication)
      .feed(jsonFeeder)
      .exec(saveEncounter)
      .pause(5 seconds , 10 seconds)


  }
}
