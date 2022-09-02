package scenarios

import configurations.Feeders.jsonFeeder
import configurations.{Load, MaximumResponseTimes, Possibility, TrafficConfiguration}
import io.gatling.core.Predef._
import io.gatling.core.structure.PopulationBuilder
import registries.Common._
import registries.Frontdesk._
import scenarios.BaseScenario.setupScenario

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object Registration {
  private val possibilities = List(
    Possibility(existingPatient_IdSearch_StartVisit, 30),
    Possibility(existingPatient_NameSearch_StartVisit, 20),
    Possibility(createPatient_StartVisit, 40),
    Possibility(patient_Document_Upload, 10)
  )

  def scenario(loadSharePercentage: Int): PopulationBuilder =
    setupScenario("Registration", Load.getTrafficShareConfiguration(loadSharePercentage), possibilities)

  private def existingPatient_IdSearch_StartVisit(expectedResTimes: MaximumResponseTimes) = {
    exec(login)
      .feed(csv("registrations.csv").circular)
      .exec(goToHomePage)
      .pause(10 seconds, 20 seconds)
      .exec(goToRegistrationSearchPage)
      .pause(10 seconds, 20 seconds)
      .exec(performIdSearch("#{Registration Number}"))
      .pause(10 seconds, 20 seconds)
      .exec(startVisitForID)
      .pause(20 seconds)
  }

  private def existingPatient_NameSearch_StartVisit(expectedResTimes: MaximumResponseTimes) = {
    exec(login)
      .feed(csv("registrations.csv").circular)
      .exec(goToHomePage)
      .pause(10 seconds, 20 seconds)
      .exec(goToRegistrationSearchPage)
      .pause(10 seconds, 20 seconds)
      .exec(performNameSearch("#{First Name}" + " " + "#{Last Name}"))
      .pause(10 seconds, 20 seconds)
      .exec(startVisitForName)
      .pause(20 seconds)
  }

  private def createPatient_StartVisit(expectedResTimes: MaximumResponseTimes) = {
    exec(gotoCreatePatientPage)
      .pause(5 seconds)
      .feed(jsonFeeder)
      .exec(createPatient)
      .pause(3 seconds, 6 seconds)
      .exec(startVisitForCreatePatient)
      .pause(5 seconds)
  }

  private def patient_Document_Upload(expectedResTimes: MaximumResponseTimes) = {
    exec(existingPatient_NameSearch_StartVisit(null))
      .exec(getActivePatients)
      .exec(getPatientImages)
      .pause(5 seconds, 10 seconds)
      .exec(goToPatientDocumentUpload)
  }

}
