package api

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder

object FrontdeskHttpRequests {
  def getAddressHierarchyLevel: HttpRequestBuilder = {
    http("get address hierarchy level")
      .get("/openmrs/module/addresshierarchy/ajax/getOrderedAddressHierarchyLevels.form")
  }

  def getRelationshipTypes: HttpRequestBuilder = {
    http("get relationship types")
      .get("/openmrs/ws/rest/v1/relationshiptype?v=custom:(aIsToB,bIsToA,uuid)")
  }

  def getEntityMapping: HttpRequestBuilder = {
    http("get LoginLocation to visit type mapping")
      .get("/openmrs/ws/rest/v1/entitymapping?mappingType=loginlocation_visittype&s=byEntityAndMappingType")
  }

  def getPersonAttributeTypes: HttpRequestBuilder = {
    http("get person attribute types")
      .get("/openmrs/ws/rest/v1/personattributetype?v=custom:(uuid,name,sortWeight,description,format,concept)")
  }

  def getByVisitLocation(visitLocationUuid: String): HttpRequestBuilder = {
    http("get visit location")
      .get("/openmrs/ws/rest/v1/location/"+visitLocationUuid)
  }

  def searchPatientUsingName(loginLocationUuid: String, identifier: String): HttpRequestBuilder = {
    http("Search Patient by Name")
      .get("/openmrs/ws/rest/v1/bahmnicore/search/patient")
      .queryParam("addressFieldName", "address2")
      .queryParam("addressFieldValue","")
      .queryParam("addressSearchResultsConfig", "address2")
      .queryParam("customAttribute","")
      .queryParam("loginLocationUuid", loginLocationUuid)
      .queryParam("patientAttributes", "givenNameLocal")
      .queryParam("patientAttributes", "middleNameLocal")
      .queryParam("patientAttributes", "familyNameLocal")
      .queryParam("patientSearchResultsConfig", "givenNameLocal")
      .queryParam("patientSearchResultsConfig", "middleNameLocal")
      .queryParam("patientSearchResultsConfig", "familyNameLocal")
      .queryParam("programAttributeFieldValue", "")
      .queryParam("q", identifier)
      .queryParam("s", "byIdOrNameOrVillage")
      .queryParam("startIndex", "0")
  }

  def searchPatientUsingIdentifier(loginLocationUuid: String, identifier: String): HttpRequestBuilder = {
    http("Search Patient by Identifier")
      .get("/openmrs/ws/rest/v1/bahmnicore/search/patient")
      .queryParam("loginLocationUuid", loginLocationUuid)
      .queryParam("identifier", identifier)
      .queryParam("addressFieldName", "city_village")
      .queryParam("addressSearchResultsConfig", "city_village")
      .queryParam("addressSearchResultsConfig", "address1")
      .queryParam("filterOnAllIdentifiers", "true")
      .queryParam("s", "byIdOrNameOrVillage")
      .queryParam("startIndex", "0")
  }

  def startVisitRequest(patient_uuid : String, opd_visit_type_id : String, login_location_id : String): HttpRequestBuilder = {
    http("start visit")
      .post("/openmrs/ws/rest/v1/visit")
      .body(StringBody(s"""{"patient":"$patient_uuid","visitType":"$opd_visit_type_id","location":"$login_location_id"}"""))
      .asJson
  }
}
