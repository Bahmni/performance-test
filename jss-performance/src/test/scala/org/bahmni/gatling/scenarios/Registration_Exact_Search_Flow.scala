package org.bahmni.gatling.scenarios

import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import org.bahmni.gatling.Configuration
import org.bahmni.gatling.HttpRequests._
import org.bahmni.gatling.Configuration.Constants._

import scala.concurrent.duration.DurationInt

object Registration_Exact_Search_Flow {

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

  def performSearch(patientIdentifier: String): ChainBuilder = {
    exec(
      searchPatientUsingIdentifier(LOGIN_LOCATION_UUID, patientIdentifier)
    )
  }

  val scn: ScenarioBuilder = scenario("IDSearch")
    .during(Configuration.Load.DURATION) {
      exec(login)
        .feed(csv("patient.csv").circular)
        .exec(goToHomePage)
        .exec(goToRegistrationSearchPage)
        .exec(performSearch("${PATIENT_IDENTIFIER}"))
        .pause(110 seconds)
    }

}
