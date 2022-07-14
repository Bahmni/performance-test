package org.bahmni.gatling.scenarios

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.request.builder.HttpRequestBuilder
import org.bahmni.gatling.Configuration
import org.bahmni.gatling.Configuration.Constants._
import org.bahmni.gatling.HttpRequests.{getProviderForUser, _}

object AdtFlow {

  val goToADTApp: HttpRequestBuilder = {
    getUser(LOGIN_USER)
      .resources(
        getProviderForUser(LOGIN_USER_UUID),
        getProviderForUser(LOGIN_USER_UUID),
        getRegistrationConcepts,
        getPatientConfigFromServer,
        getGlobalProperty("mrs.genders"),
        getGlobalProperty("bahmni.relationshipTypeMap"),
        getIdentifierTypes,
        getAdmissionLocations,
        getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.patientsToAdmit"),
        getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.admittedPatients"),
        getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.patientsToDischarge"),
        getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.patientsAdmittedToday"),
        getWardListDetails("Location-6"),
        getWardListDetails("Location-8"),
        getWardListDetails("Location-10"),
        getWardListDetails("Location-12"),
        getWardListDetails("Location-14"),
        getWardListDetails("Location-16"),
        getWardListDetails("Location-18"),
        getWardListDetails("Location-20"),
        getWardListDetails("Location-22"),
        getWardListDetails("Location-24"),
        getWardListDetails("Location-26"),
        getWardListDetails("Location-43"),
        getWardListDetails("Location-45")

      )
  }

  val scn: ScenarioBuilder = scenario("inPatientListing")
    .during(Configuration.Load.DURATION) {
      exec(goToADTApp)
    }
}