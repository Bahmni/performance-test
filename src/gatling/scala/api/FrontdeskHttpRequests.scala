package api

import io.gatling.core.Predef._
import io.gatling.core.body.Body
import io.gatling.core.session.Expression
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

  def getPersonAttributeTypes: HttpRequestBuilder = {
    http("get person attribute types")
      .get("/openmrs/ws/rest/v1/personattributetype?v=custom:(uuid,name,sortWeight,description,format,concept)")
  }

  def getByVisitLocation(visitLocationUuid: String): HttpRequestBuilder = {
    http("get visit location")
      .get("/openmrs/ws/rest/v1/location/"+visitLocationUuid)
  }

  def getPatientProfileAfterRegistration(patientUuid: String): HttpRequestBuilder = {
    http("get patient")
      .get("/openmrs/ws/rest/v1/patientprofile/" + patientUuid)
      .queryParam("v","full")
  }

  def searchPatientUsingName(loginLocationUuid: String, identifier: String): HttpRequestBuilder = {
    http("Search Patient by Name")
      .get("/openmrs/ws/rest/v1/bahmnicore/search/patient")
      .queryParam("addressFieldValue", "")
      .queryParam("addressSearchResultsConfig","{}")
      .queryParam("customAttribute", "")
      .queryParam("loginLocationUuid",loginLocationUuid)
      .queryParam("patientAttributes", "phoneNumber")
      .queryParam("patientAttributes", "alternatePhoneNumber")
      .queryParam("patientSearchResultsConfig", "phoneNumber")
      .queryParam("patientSearchResultsConfig", "alternatePhoneNumber")
      .queryParam("programAttributeFieldValue", "")
      .queryParam("q", identifier)
      .queryParam("s", "byIdOrNameOrVillage")
      .queryParam("startIndex", "0")
  }

  def searchPatientUsingIdentifier(loginLocationUuid: String, identifier: String): HttpRequestBuilder = {
    http("Search Patient by Identifier")
      .get("/openmrs/ws/rest/v1/bahmnicore/search/patient")
      .queryParam("addressSearchResultsConfig", "{}")
      .queryParam("filterOnAllIdentifiers", "true")
      .queryParam("identifier", identifier)
      .queryParam("loginLocationUuid", loginLocationUuid)
      .queryParam("patientAttributes", "phoneNumber")
      .queryParam("patientAttributes", "alternatePhoneNumber")
      .queryParam("patientSearchResultsConfig", "phoneNumber")
      .queryParam("patientSearchResultsConfig", "alternatePhoneNumber")
      .queryParam("programAttributeFieldValue", "")
      .queryParam("s", "byIdOrNameOrVillage")
      .queryParam("startIndex", "0")
  }

  def startVisitRequest(patient_uuid : String, opd_visit_type_id : String, login_location_id : String): HttpRequestBuilder = {
    http("start visit")
      .post("/openmrs/ws/rest/v1/visit")
      .body(StringBody(s"""{"patient":"$patient_uuid","visitType":"$opd_visit_type_id","location":"$login_location_id"}"""))
      .asJson
  }

  def getReasonForDeath: HttpRequestBuilder = {
    http("Get reason for death")
      .get("/openmrs/ws/rest/v1/concept?s=byFullySpecifiedName&name=Reason+For+Death&v=custom:(uuid,name,set,names,setMembers:(uuid,display,name:(uuid,name),names,retired))")
  }

  def createPatientRequest(body: Body with (Expression[String])): HttpRequestBuilder = {
    http("create patient")
      .post("/openmrs/ws/rest/v1/bahmnicore/patientprofile")
      .body(body).asJson
  }


  def getNutrition:HttpRequestBuilder={
    http("get Nutrition")
      .get("/openmrs/ws/rest/v1/concept")
      .queryParam("s","byFullySpecifiedName")
      .queryParam("locale","en")
      .queryParam("name","Nutritional+Values")
      .queryParam("v","bahmni")
  }

  def getVital:HttpRequestBuilder={
    http("get Vital")
      .get("/openmrs/ws/rest/v1/concept")
      .queryParam("s","byFullySpecifiedName")
      .queryParam("locale","en")
      .queryParam("name","Vitals")
      .queryParam("v","bahmni")
  }
  def getFeeInformation:HttpRequestBuilder={
    http("get fee information")
      .get("/openmrs/ws/rest/v1/concept")
      .queryParam("s","byFullySpecifiedName")
      .queryParam("locale","en")
      .queryParam("name","Fee+Information")
      .queryParam("v","bahmni")
  }
}
