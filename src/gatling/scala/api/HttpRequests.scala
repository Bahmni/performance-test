package api

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder

object HttpRequests {
  def getLoginLocations: HttpRequestBuilder = {
    http("get login locations")
      .get("/openmrs/ws/rest/v1/location?operator=ALL&s=byTags&tags=Login+Location&v=default")
  }

  def getGlobalProperty(property: String): HttpRequestBuilder = {
    http("get " + property + " global property")
      .get("/openmrs/ws/rest/v1/bahmnicore/sql/globalproperty")
      .queryParam("property", property)
  }

  def getUser(username: String): HttpRequestBuilder = {
    http("get user")
      .get("/openmrs/ws/rest/v1/user?v=custom:(username,uuid,person:(uuid,),privileges:(name,retired),userProperties)")
      .queryParam("username", username)
  }

  def getSession: HttpRequestBuilder = {
    http("get session")
      .get("/openmrs/ws/rest/v1/session?v=custom:(uuid)")
  }
  def deleteSession: HttpRequestBuilder = {
    http("get session")
      .delete("/openmrs/ws/rest/v1/session?v=custom:(uuid)")
  }

  def postUserInfo(loggedInUserUuid: String): HttpRequestBuilder = {
    http("post user info")
      .post("/openmrs/ws/rest/v1/user/" + loggedInUserUuid)
      .body(
        StringBody(s"""{"uuid":"$loggedInUserUuid","userProperties":{"defaultLocale":"en","favouriteObsTemplates":"",
           "recentlyViewedPatients":"","loginAttempts":"0","favouriteWards":""}}""")
      )
      .asJson
  }

  def postAuditLog: HttpRequestBuilder = {
    http("post audit log")
      .post("/openmrs/ws/rest/v1/auditlog")
      .body(
        StringBody(
          "{\"eventType\":\"DUMMY_PERF_MESSAGE\",\"message\":\"DUMMY_PERF_TEST_MESSAGE\",\"module\":\"MODULE_PERF_TEST\"}"
        )
      )
      .asJson
  }
  def postAuditLog(loggedInUserUuid: String): HttpRequestBuilder = {
    http("post audit log")
      .post("/openmrs/ws/rest/v1/auditlog")
      .body(
        StringBody(
          s"""{\"eventType\":\"DUMMY_PERF_MESSAGE\",\"message\":\"DUMMY_PERF_TEST_MESSAGE\",\"module\":\"MODULE_PERF_TEST\",\"patientUuid\":\"$loggedInUserUuid\"}"""
        )
      )
      .asJson
  }

  def getProviderForUser(userUuid: String): HttpRequestBuilder = {
    http("get provider")
      .get("/openmrs/ws/rest/v1/provider")
      .queryParam("user", userUuid)
  }

  def getVisitLocation(visitLocationUuid: String): HttpRequestBuilder = {
    http("get visit location")
      .get("/openmrs/ws/rest/v1/bahmnicore/visitLocation/" + visitLocationUuid)
  }

  def getIdentifierTypes: HttpRequestBuilder = {
    http("get identifier types")
      .get("/openmrs/ws/rest/v1/idgen/identifiertype")
  }

  def getRegistrationConcepts: HttpRequestBuilder = {
    http("get registration concepts")
      .get("/openmrs/ws/rest/v1/bahmnicore/config/bahmniencounter?callerContext=REGISTRATION_CONCEPTS")
  }

  def getEntityMapping(mappingType: String): HttpRequestBuilder = {
    http("get LoginLocation to visit type mapping")
      .get("/openmrs/ws/rest/v1/entitymapping?mappingType=" + mappingType + "&s=byEntityAndMappingType")
  }

  def getPersonaAttributeType: HttpRequestBuilder = {
    http("get persona Attribute type")
      .get("/openmrs/ws/rest/v1/personattributetype")
      .queryParam("v", "custom:(uuid,name,sortWeight,description,format,concept)")
  }

  def activateVisit(patientid: String): HttpRequestBuilder = {
    http("activate visit")
      .get("/openmrs/ws/rest/v1/visit")
      .queryParam("includeInactive", "false")
      .queryParam("patient", patientid)
      .queryParam("v", "custom:(uuid,location:(uuid)")
  }

  def findEncounter(patientUuid: String): HttpRequestBuilder = {
    http("find encounter")
      .post("/openmrs/ws/rest/v1/bahmnicore/bahmniencounter/find")
      .body(
        StringBody(
          s"""{"patientUuid": "$patientUuid", "providerUuids": [ "ffa806af-18a1-11ed-bd3f-02cf26abc856" ], "includeAll": false, "locationUuid": "833d0c66-e29a-4d31-ac13-ca9050d1bfa9", "encounterTypeUuids": [ "ffa806af-18a1-11ed-bd3f-02cf26abc856" ] }""".stripMargin
        )
      )
      .asJson
  }

  def getObservation(concepts: Map[String, String]): HttpRequestBuilder = {
    http("get observation")
      .get("/openmrs/ws/rest/v1/bahmnicore/observations")
      .queryParamMap(concepts)
  }

  def getObservation(concept: Seq[String], params: Map[String, String]): HttpRequestBuilder = {
    http("get observation")
      .get("/openmrs/ws/rest/v1/bahmnicore/observations")
      .multivaluedQueryParam("concept", concept)
      .queryParamMap(params)
  }
}
