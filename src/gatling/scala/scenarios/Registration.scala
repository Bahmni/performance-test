package scenarios

import configurations.Feeders.{docUploadFeeder, jsonFeeder}
import io.gatling.core.Predef._
import io.gatling.core.structure.PopulationBuilder
import registries.Common._
import registries.FrontDesk._
import scenarios.BaseScenario.setupScenario
import configurations.{Scenario, ScenarioWorkLoad, UserFlow}
import scenarios.BaseScenario.handleWorkLoad
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
      exec(waitBeforeNextStep(0, 30))
      .exec(login)
      .feed(csv("registrations.csv").random)
      .exec(goToHomePage)
        .exec(waitBeforeNextStep(0, 20))
      .exec(goToRegistrationSearchPage)
        .exec(waitBeforeNextStep(0, 20))
      .exec(performIdSearch("#{Registration Number}"))
        .exec(waitBeforeNextStep(0, 20))
      .exec(startVisitForID)
        .exec(waitBeforeNextStep(0, 30))
        .exec(otherCloseVisit("#{p_uuID}")),workLoad)
  }

  private def existingPatient_NameSearch_StartVisit(workLoad: ScenarioWorkLoad) = {
    handleWorkLoad(
      exec(waitBeforeNextStep(0, 30))
        .exec(login)
      .feed(csv("registrations.csv").random)
      .exec(goToHomePage)
         .exec(waitBeforeNextStep(0, 20))
      .exec(goToRegistrationSearchPage)
         .exec(waitBeforeNextStep(0, 20))
      .exec(performNameSearch("#{First Name}" + " " + "#{Last Name}"))
         .exec(waitBeforeNextStep(0, 20))
      .exec(startVisitForName)
        .exec(waitBeforeNextStep(0, 30))
        .exec(otherCloseVisit("#{pt_uuID}")),workLoad)

  }

  private def createPatient_StartVisit(workLoad: ScenarioWorkLoad) = {
    handleWorkLoad(
      exec(waitBeforeNextStep(0, 30))
    .exec(login)
      .exec(goToHomePage)
        .exec(waitBeforeNextStep(0, 30))
        .exec(gotoCreatePatientPage)
         .exec(waitBeforeNextStep(0, 30))
      .feed(jsonFeeder)
      .exec(createPatient)
         .exec(waitBeforeNextStep(0, 30))
      .exec(startVisitForCreatePatient),workLoad)
  }

  private def patient_Document_Upload(workLoad: ScenarioWorkLoad) = {
    handleWorkLoad(
      exec(waitBeforeNextStep(0, 30))
        .exec(login)
        .feed(csv("registrations.csv").random)
        .exec(goToHomePage)
        .exec(waitBeforeNextStep(0, 10))
        .exec(goToRegistrationSearchPage)
        .exec(waitBeforeNextStep(0, 10))
        .exec(performNameSearch("#{First Name}" + " " + "#{Last Name}"))
        .exec(waitBeforeNextStep(0, 10))
        .exec(startVisitForName)
        .exec(waitBeforeNextStep(0, 5))
        .exec(returnToHomePage)
        .exec(getActivePatients)
        .exec(waitBeforeNextStep(0, 5))
        .exec(waitBeforeNextStep(0, 20))
        .exec(goToPatientDocumentUpload)
        .feed(docUploadFeeder)
        .exec(waitBeforeNextStep(0, 10))
        .exec(uploadPatientDocument)
        .exec(waitBeforeNextStep(0, 10))
        .exec(verifyPatientDocument)
        .exec(waitBeforeNextStep(0, 10))
        .exec(otherCloseVisit("#{pt_uuID}")), workLoad)
  }
}
