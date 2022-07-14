package org.bahmni.gatling.scenarios

import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import org.bahmni.gatling.Configuration.Constants._
import org.bahmni.gatling.HttpRequests._

import scala.concurrent.duration.DurationInt

object Registration_Name_Search_Flow {

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
    )
  }

  val scn: ScenarioBuilder = scenario("NameSearch")
      .exec(login)
        .feed(csv("patientName.csv").circular)
        .exec(goToHomePage)
        .pause(10 seconds, 20 seconds)
        .exec(goToRegistrationSearchPage)
        .pause(10 seconds, 20 seconds)
        .exec(performSearch("${PATIENT_NAME}"))
        .pause(20 seconds)
}
