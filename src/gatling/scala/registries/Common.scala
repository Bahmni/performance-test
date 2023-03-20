package registries

import api.HttpRequests.{getGlobalProperty, _}
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import api.Constants._
import api.DoctorHttpRequests._
import api.FrontdeskHttpRequests._
import registries.Doctor.remainingTime
import scala.concurrent.duration.{DurationLong, FiniteDuration}
import scala.language.postfixOps
import scala.util.Random

object Common {
  val rnd = new Random()

  def login: ChainBuilder = exec(
    getLoginLocations
      .resources(
        getGlobalProperty("locale.allowed.list")
      )
  )

  def goToHomePage: ChainBuilder = exec(
    getUser(LOGIN_USER)
      .check(
        jsonPath("$..results[0].uuid").find.saveAs("runTimeUuid")
      )
      .resources(
        deleteSession,
      )
    ).exec(getSession)
    .exec(postUserInfo("#{runTimeUuid}"))
    .exec(getProviderForUser("#{runTimeUuid}"))
    .exec(getGlobalProperty("bahmni.enableAuditLog"))
    .exec(postAuditLog)

  def returnToHomePage: ChainBuilder = {
    exec(
      getUser(LOGIN_USER)
        .check(
          jsonPath("$..results[0].uuid").find.saveAs("runTimeUuid")
        )
    )
      .exec(getProviderForUser("#{runTimeUuid}"))
      .exec(getLoginLocations)
  }
def closeVisit():ChainBuilder= {
  exec(getVisitLocation(LOGIN_LOCATION_UUID))
  .exec(getSession)
  .exec(getUser("superman"))
  .exec(getProviderForUser("#{runTimeUuid}"))
  .exec(getRegistrationConcepts.resources(
    getPersonaAttributeType,
    getIdentifierTypes,
    getGlobalProperty("mrs.genders"),
    getRelationshipTypes,
    getGlobalProperty("bahmni.relationshipTypeMap"),
    getEntityMapping("loginlocation_visittype")
  ))
    .exec(findEncounter("#{opdPatientId}",PROVIDER_UUID,CLOSE_VISIT_ENCOUNTER_TYPE_UUID)
      .resources(
        getPatientProfileAfterRegistration("#{opdPatientId}"),
        getVisitByAttributes("false","#{opdPatientId}","custom:(uuid,location:(uuid))")
    ))
    .exec(postAuditLog("#{opdPatientId}"))
    .exec(getLatestPublishedForms)
    .exec(
  closePatientVisit("#{opdPatientId}","#{opdVisitId}")
)
}

def otherCloseVisit(patientUuid:String):ChainBuilder={
   exec(getactiveVisit(patientUuid).check(
  jmesPath("results[0].uuid").ofType[Any].not(None).saveAs("opdVisitId")))
  .exec(closePatientVisit(patientUuid,"#{opdVisitId}"))
}


  def waitBeforeNextWorkLoad(expectedCompletionTime: FiniteDuration): ChainBuilder = {
    exec { session =>
      val timeElapsed = (System.currentTimeMillis() - session("startTime").as[Long])
      if (timeElapsed < expectedCompletionTime.toMillis) {
        remainingTime = (expectedCompletionTime.toMillis - timeElapsed) millis
      } else {
        remainingTime = 0 millis
      }
      session.set("rt", remainingTime)
    }.exec(pause("#{rt}"))
  }

  def waitBeforeNextStep(min:Int,max:Int): ChainBuilder = {
    exec { session =>
      session.set("pt", rnd.between(min,max) seconds)
    }.exec(pause("#{pt}"))
  }

}
