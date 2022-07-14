package org.bahmni.gatling.scenarios

import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import org.bahmni.gatling.Configuration
import org.bahmni.gatling.Configuration.Constants._
import org.bahmni.gatling.HttpRequests._

import scala.util.Random

object Registration_Name_Search_OPD_Visit_Flow {

  val login: ChainBuilder = exec(
    getLoginLocations
      .resources(
        getGlobalProperty("locale.allowed.list")
      )
  )

  var goToHomePage: ChainBuilder = exec(
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

  def performSearch(patientName: String): ChainBuilder = {
    exec(
      searchPatientUsingName(LOGIN_LOCATION_UUID, patientName)
        .check(
          status.is(200),
          jsonPath("$..uuid").findAll.transform(Random.shuffle(_).head).optional.saveAs("p_uuID")
        )
        .resources(

          getPatientProfileAfterRegistration("${p_uuID}"),
          startVisitRequest("${p_uuID}", VISIT_TYPE_ID, LOGIN_LOCATION_UUID).check(status.is(201)))
    )
  }

  val scn: ScenarioBuilder = scenario("NameSearch")
    .during(Configuration.Load.DURATION) {
      exec(login)
        .feed(csv("patientName.csv").circular)
        .exec(goToHomePage)
        .exec(goToRegistrationSearchPage)
        .exec(performSearch("${PATIENT_NAME}"))
        .pause(20)
    }

}
