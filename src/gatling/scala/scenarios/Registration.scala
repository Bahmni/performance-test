package scenarios

import configurations.Feeders.{docUploadFeeder, jsonFeeder}
import io.gatling.core.Predef._
import io.gatling.core.structure.PopulationBuilder
import registries.Common._
import registries.FrontDesk._
import scenarios.BaseScenario.setupScenario
import configurations.{Scenario, ScenarioWorkLoad, UserFlow}
import scenarios.BaseScenario.{handleWorkLoad}

import scala.concurrent.duration.{DurationInt}
import scala.language.postfixOps

object Registration {
  private val scenarios = List(
    Scenario(
      existingPatient_IdSearch_StartVisit,
      UserFlow.Registration.existingPatientIdSearchOpdVisit
    ),
    Scenario(
      existingPatient_NameSearch_StartVisit,
      UserFlow.Registration.existingPatientNameSearchOpdVisit
    ),
    Scenario(
      createPatient_StartVisit,
      UserFlow.Registration.newPatientOpdVisit
    ),
    Scenario(
      patient_Document_Upload,
      UserFlow.Registration.patientDocumentUpload
    )
  )

  def scenario(trafficSharePercentage: Int): List[PopulationBuilder] = {
    scenarios.map(scn => setupScenario(scn, trafficSharePercentage))
  }
  private def existingPatient_IdSearch_StartVisit(workLoad: ScenarioWorkLoad) = {
    handleWorkLoad(
      exec(login)
      .feed(csv("registrations.csv").circular)
      .exec(goToHomePage)
      //.exec(pause(10 seconds))
      .exec(goToRegistrationSearchPage)
      //.exec(pause(20 seconds))
      .exec(performIdSearch("#{Registration Number}"))
      //.exec(pause(30 seconds))
      .exec(startVisitForID)
        .exec(otherCloseVisit("#{p_uuID}")),workLoad)
  }

  private def existingPatient_NameSearch_StartVisit(workLoad: ScenarioWorkLoad) = {
    handleWorkLoad(
       exec(login)
      .feed(csv("registrations.csv").circular)
      .exec(goToHomePage)
      //.exec(pause(20 seconds))
      .exec(goToRegistrationSearchPage)
     // .exec(pause(20 seconds))
      .exec(performNameSearch("#{First Name}" + " " + "#{Last Name}"))
      //.exec(pause(20 seconds))
      .exec(startVisitForName)
      .exec(otherCloseVisit("#{pt_uuID}")),workLoad)

  }

  private def createPatient_StartVisit(workLoad: ScenarioWorkLoad) = {
    handleWorkLoad(
       exec(login)
      .exec(goToHomePage)
      .exec(gotoCreatePatientPage)
     // .exec(pause(10 seconds))
      .feed(jsonFeeder)
      .exec(createPatient)
      //.exec(pause(30 seconds))
      .exec(startVisitForCreatePatient),workLoad)
  }

  private def patient_Document_Upload(workLoad: ScenarioWorkLoad) = {
    handleWorkLoad(
      exec(login)
        .feed(csv("registrations.csv").circular)
        .exec(goToHomePage)
       // .exec(pause(20 seconds))
        .exec(goToRegistrationSearchPage)
       // .exec(pause(20 seconds))
        .exec(performNameSearch("#{First Name}" + " " + "#{Last Name}"))
        //.exec(pause(20 seconds))
        .exec(startVisitForName)
        .exec(returnToHomePage)
        .exec(getActivePatients)
        .exec(getPatientAvatars)
        //.exec(pause(20 seconds))
        .exec(goToPatientDocumentUpload)
        .feed(docUploadFeeder)
        //.exec(pause(20 seconds))
        .exec(uploadPatientDocument)
       // .exec(pause(20 seconds))
        .exec(verifyPatientDocument)
        .exec(otherCloseVisit("#{pt_uuID}")), workLoad)
  }
}
