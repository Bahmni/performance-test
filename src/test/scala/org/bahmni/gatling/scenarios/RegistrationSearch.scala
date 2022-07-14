package org.bahmni.gatling.scenarios

import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import org.bahmni.gatling.Configuration
import org.bahmni.gatling.HttpRequests._
import org.bahmni.gatling.Configuration.Constants._

object RegistrationSearch {

  val login: ChainBuilder = exec(
    getLoginLocations
      .resources(
        getGlobalProperty("locale.allowed.list")
      )
  )

  var goToHomePage: ChainBuilder = exec(
    getUser(LOGIN_USER)
      .resources(
        getProviderForUser(LOGIN_USER_UUID),
        getLoginLocations
      )
  )

  val goToRegistrationSearchPage: ChainBuilder = exec(
    getVisitLocation(LOGIN_LOCATION_UUID)
      .resources(
        getProviderForUser(LOGIN_USER_UUID),
        getGlobalProperty("mrs.genders"),
        getGlobalProperty("bahmni.relationshipTypeMap"),
        getAddressHierarchyLevel,
        getIdentifierTypes,
        getRelationshipTypes,
        getEntityMapping,
        getPersonAttributeTypes,
        getRegistrationConcepts
      )

  )

  def performSearch(patientIdentifier: String): ChainBuilder = {
    exec(
      searchPatientUsingName(LOGIN_LOCATION_UUID, patientIdentifier)
    )
  }

  val scn: ScenarioBuilder = scenario("registerPatient")
    .during(Configuration.Load.DURATION) {
      exec(login)
        .exec(goToHomePage)
        .exec(goToRegistrationSearchPage)
        .exec(performSearch(PATIENT_IDENTIFIER))
        .pause(20)
        .exec(goToRegistrationSearchPage)
        .exec(performSearch(PATIENT_IDENTIFIER1))
        .pause(20)
        .exec(goToRegistrationSearchPage)
        .exec(performSearch(PATIENT_IDENTIFIER2))

    }
}