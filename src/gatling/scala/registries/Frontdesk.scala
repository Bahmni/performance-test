package registries

import api.Constants.{LOGIN_LOCATION_UUID, LOGIN_USER, VISIT_TYPE_ID}
import api.FrontdeskHttpRequests._
import api.HttpRequests._
import configurations.MaximumResponseTimes
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

object Frontdesk {
  val goToHomePage: ChainBuilder = exec(
    getUser(LOGIN_USER)
      .check(
        jsonPath("$..results[0].uuid").find.saveAs("runTimeUuid")
      )
      .resources(
        getProviderForUser("${runTimeUuid}"),
        getSession,
        postUserInfo("${runTimeUuid}"),
        getGlobalProperty("bahmni.enableAuditLog"),
        postAuditLog
      )
  )

  val goToRegistrationSearchPage: ChainBuilder = exec(
    getVisitLocation(LOGIN_LOCATION_UUID)
      .resources(
        getUser(LOGIN_USER).check(
          jsonPath("$..results[0].uuid").find.saveAs("runTimeUuid")
        ),
        getSession,
        getProviderForUser("${runTimeUuid}"),
        getGlobalProperty("mrs.genders"),
        getGlobalProperty("bahmni.relationshipTypeMap"),
        getAddressHierarchyLevel,
        getIdentifierTypes,
        getRelationshipTypes,
        getEntityMapping,
        getPersonAttributeTypes,
        getRegistrationConcepts,
        getByVisitLocation(LOGIN_LOCATION_UUID),
        getGlobalProperty("bahmni.enableAuditLog"),
        postAuditLog
      )
  )

  def performNameSearch(patientName: String): ChainBuilder = {
    exec(
      searchPatientUsingName(LOGIN_LOCATION_UUID, patientName)
        .check(status.is(200), bodyString.saveAs("body"))
    )
    exec(session => {
      val response = session("body").as[String]
      println(s"Response body: \n$response")
      session
    })
  }

  def performIdSearch(patientIdentifier: String): ChainBuilder = {
    exec(
      searchPatientUsingIdentifier(LOGIN_LOCATION_UUID, patientIdentifier)
        .check(status.in(200 to 201))
    )
  }

  val startVisit: ChainBuilder = {
    exec( startVisitRequest("${patient_uuid}", VISIT_TYPE_ID, LOGIN_LOCATION_UUID).check(
      status.is(201))
    )
  }
}
