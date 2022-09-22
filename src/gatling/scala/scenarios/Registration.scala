package scenarios

import configurations.Feeders.{docUploadFeeder, jsonFeeder}
import configurations.{Load, MaximumResponseTimes, Possibility, TrafficConfiguration}
import io.gatling.core.Predef._
import io.gatling.core.structure.PopulationBuilder
import registries.Common._
import registries.Doctor.{pauseRemainingTime, setStartTime}
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
    setupScenario("Registration",5 minutes, Load.getTrafficShareConfiguration(loadSharePercentage), possibilities)

  private def existingPatient_IdSearch_StartVisit(expectedResTimes: MaximumResponseTimes) = {
    exec(setStartTime())
      .exec(login)
      .feed(csv("registrations.csv").circular)
      .exec(goToHomePage)
      .exec(pauseRemainingTime(20))
      .exec(goToRegistrationSearchPage)
      .exec(pauseRemainingTime(40))
      .exec(performIdSearch("#{Registration Number}"))
      .exec(pauseRemainingTime(150))
      .exec(startVisitForID)
      .exec(pauseRemainingTime(180))
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
      .exec(pauseRemainingTime(150))
      .exec(startVisitForName)
      .exec(pauseRemainingTime(180))

  }

  private def createPatient_StartVisit(expectedResTimes: MaximumResponseTimes) = {
    exec(setStartTime())
      .exec(login)
      .exec(goToHomePage)
      .exec(gotoCreatePatientPage)
      .exec(pauseRemainingTime(20))
      .feed(jsonFeeder)
      .exec(createPatient)
      .exec(pauseRemainingTime(120))
      .exec(startVisitForCreatePatient)
      .exec(pauseRemainingTime(180))
  }

  private def patient_Document_Upload(expectedResTimes: MaximumResponseTimes) = {
    exec(setStartTime())
      .exec(existingPatient_NameSearch_StartVisit(null))
      .exec(pauseRemainingTime(20))
      .exec(returnToHomePage)
      .exec(pauseRemainingTime(40))
      .exec(getActivePatients)
      .exec(getPatientAvatars)
      .exec(pauseRemainingTime(80))
      .exec(goToPatientDocumentUpload)
      .feed(docUploadFeeder)
      .exec(pauseRemainingTime(120))
      .exec(uploadPatientDocument)
      .exec(pauseRemainingTime(160))
      .exec(verifyPatientDocument)
      .exec(pauseRemainingTime(180))
  }

}
