package scenarios

import api.Constants.REGISTRATION_LOAD_SHARE
import configurations.Feeders.{docUploadFeeder, jsonFeeder}
import configurations.{Load, MaximumResponseTimes, Possibility}
import io.gatling.core.Predef._
import io.gatling.core.structure.PopulationBuilder
import registries.Common._
import registries.Doctor.{pauseRemainingTime, setStartTime}
import registries.Frontdesk._
import scenarios.BaseScenario.setupScenario

import scala.concurrent.duration.{Duration, DurationInt, FiniteDuration, SECONDS}
import scala.language.postfixOps

object Registration {
  private val possibilities = List(
    Possibility(existingPatient_IdSearch_StartVisit, 30),
    Possibility(existingPatient_NameSearch_StartVisit, 20),
    Possibility(createPatient_StartVisit, 40),
    Possibility(patient_Document_Upload, 10)
  )
  val setRate = {
    val trafficConfig = Load.getLoadParameters
    var actualPace: FiniteDuration = Duration(((trafficConfig.duration * (REGISTRATION_LOAD_SHARE/100.00 * trafficConfig.activeUsers)) / Load.getPatientCount("frontdesk")).toSeconds,SECONDS)
    if (actualPace.gteq(2 minutes)) actualPace = 2 minutes
    else if (actualPace.lteq(1 minutes)) actualPace = 1 minutes

    println("Patients at Registration : "+Load.getPatientCount("frontdesk"))
    println("Time available for Registration : "+ (trafficConfig.duration * ((REGISTRATION_LOAD_SHARE/100.00) * trafficConfig.activeUsers)).toSeconds)
    println("Registration Pace : " + actualPace.toSeconds)
    actualPace
  }
  def scenario(loadSharePercentage: Int): PopulationBuilder =
    setupScenario("Registration",setRate, Load.getTrafficShareConfiguration(loadSharePercentage), possibilities)

  private def existingPatient_IdSearch_StartVisit(expectedResTimes: MaximumResponseTimes) = {
    exec(setStartTime())
      .exec(login)
      .feed(csv("registrations.csv").circular)
      .exec(goToHomePage)
      .exec(pauseRemainingTime(10))
      .exec(goToRegistrationSearchPage)
      .exec(pauseRemainingTime(20))
      .exec(performIdSearch("#{Registration Number}"))
      .exec(pauseRemainingTime(30))
      .exec(startVisitForID)
  }

  private def existingPatient_NameSearch_StartVisit(expectedResTimes: MaximumResponseTimes) = {
    exec(setStartTime())
      .exec(login)
      .feed(csv("registrations.csv").circular)
      .exec(goToHomePage)
      .exec(pauseRemainingTime(20))
      .exec(goToRegistrationSearchPage)
      .exec(pauseRemainingTime(40))
      .exec(performNameSearch("#{First Name}" + " " + "#{Last Name}"))
      .exec(pauseRemainingTime(60))
      .exec(startVisitForName)

  }

  private def createPatient_StartVisit(expectedResTimes: MaximumResponseTimes) = {
    exec(setStartTime())
      .exec(login)
      .exec(goToHomePage)
      .exec(gotoCreatePatientPage)
      .exec(pauseRemainingTime(10))
      .feed(jsonFeeder)
      .exec(createPatient)
      .exec(pauseRemainingTime(40))
      .exec(startVisitForCreatePatient)
  }

  private def patient_Document_Upload(expectedResTimes: MaximumResponseTimes) = {
       exec(existingPatient_NameSearch_StartVisit(null))
      .exec(returnToHomePage)
      .exec(getActivePatients)
      .exec(getPatientAvatars)
      .exec(pauseRemainingTime(70))
      .exec(goToPatientDocumentUpload)
      .feed(docUploadFeeder)
      .exec(pauseRemainingTime(75))
      .exec(uploadPatientDocument)
      .exec(pauseRemainingTime(90))
      .exec(verifyPatientDocument)
  }

}
