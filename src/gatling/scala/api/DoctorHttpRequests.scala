package api

import io.gatling.core.Predef._
import io.gatling.core.session.Expression
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import api.Constants._
import scala.util.Random
import scala.io.Source

object DoctorHttpRequests {

  def getPatientConfigFromServer: HttpRequestBuilder = {
    http("patient config")
      .get("/openmrs/ws/rest/v1/bahmnicore/config/patient")
  }

  def getConcept(name: String): HttpRequestBuilder = {
    http("get " + name + " concept")
      .get("/openmrs/ws/rest/v1/concept")
      .queryParam("s", "byFullySpecifiedName")
      .queryParam("v", "custom:(uuid,name,answers)")
      .queryParam("name", name)
  }

  def getOrderTypes: HttpRequestBuilder = {
    http("get order types")
      .get("/openmrs/ws/rest/v1/ordertype?v=custom:(uuid,display,conceptClasses:(uuid,display,name))")
  }

  def getPatientsInSearchTab(locationUuid: String, providerUuid: String, sqlName: String): HttpRequestBuilder = {
    http(sqlName)
      .get("/openmrs/ws/rest/v1/bahmnicore/sql")
      .queryParam("location_uuid", locationUuid)
      .queryParam("provider_uuid", providerUuid)
      .queryParam("q", sqlName)
      .queryParam("v", "full")
  }

  def getRelationship(personUuid: String): HttpRequestBuilder = {
    http("get relationship")
      .get("/openmrs/ws/rest/v1/relationship")
      .queryParam("person", personUuid)
  }

  def getConceptByName(name: String): HttpRequestBuilder = {
    http("get " + name + " concept")
      .get("/openmrs/ws/rest/v1/concept")
      .queryParam("s", "byFullySpecifiedName")
      .queryParam("v", "custom:(uuid,name:(name))")
      .queryParam("name", name)
  }

  def getVisits(patientUuid: String): HttpRequestBuilder = {
    http("getVisits")
      .get("/openmrs/ws/rest/v1/visit?includeInactive=true")
      .queryParam("v", "custom:(uuid,visitType,startDatetime,stopDatetime,location,encounters:(uuid))")
      .queryParam("patient", patientUuid)
  }

  def getEncounterTypeConsultation: HttpRequestBuilder = {
    http("get encounter type consultation")
      .get("/openmrs/ws/rest/v1/encountertype/Consultation")
  }

  def getPatient(patientUuid: String): HttpRequestBuilder = {
    http("get patient")
      .get("/openmrs/ws/rest/v1/patient/" + patientUuid)
  }

  def getDiagnoses(patientUuid: String): HttpRequestBuilder = {
    http("get diagnoses for patient")
      .get("/openmrs/ws/rest/v1/bahmnicore/diagnosis/search")
      .queryParam("patientUuid", patientUuid)
  }

  def getConditionalHistory(patientUuid: String) : HttpRequestBuilder = {
    http("get conditional history")
      .get("/openmrs/ws/rest/emrapi/conditionhistory")
      .queryParam("patientUuid", patientUuid)
  }

  def getDiseaseTemplates(patientUuid : String) : HttpRequestBuilder = {
    http("get disease templates")
      .post("/openmrs/ws/rest/v1/bahmnicore/diseaseTemplates")
      .body(StringBody(s"""{"patientUuid":"$patientUuid","diseaseTemplateConfigList":[{"title":"Diabetes","templateName":"Diabetes Templates","type":"diseaseTemplate","displayOrder":18,"dashboardConfig":{"showOnly":[]},"expandedViewConfig":{"showDetailsButton":true,"pivotTable":{"numberOfVisits":"15","groupBy":"encounters","obsConcepts":["Weight","Height","Systolic","Diastolic","Diabetes, Foot Exam","Diabetes, Eye Exam"],"drugConcepts":["Ipratropium Pressurised","Garbhpal Rasa"],"labConcepts":["RBS","FBS","PP2BS","Hb1AC","Creatinine","Albumin","Polymorph"]}}}],"startDate":null,"endDate":null}"""))
      .asJson
  }

  def getEncoutnerByEncounterTypeUuid(patientUuid: String) : HttpRequestBuilder = {
    http("get encounter by uuid")
      .get("/openmrs/ws/rest/v1/encounter")
      .queryParam("v", "custom:(uuid,provider,visit:(uuid,startDatetime,stopDatetime),obs:(uuid,concept:(uuid,name),groupMembers:(id,uuid,obsDatetime,value,comment)))")
      .queryParam("patient", patientUuid)
      .queryParam("encounterType", ENCOUNTER_TYPE_UUID)
  }

  def getDrugOrderConfig: HttpRequestBuilder = {
    http("get config for drug orders")
      .get("/openmrs/ws/rest/v1/bahmnicore/config/drugOrders")
  }

  def getDrugOrdersForPatient(patientUuid: String): HttpRequestBuilder = {
    http("get drug orders")
      .get("/openmrs/ws/rest/v1/bahmnicore/drugOrders/prescribedAndActive")
      .queryParam("getEffectiveOrdersOnly", "false")
      .queryParam("getOtherActive", "true")
      .queryParam("numberOfVisits", 1)
      .queryParam("patientUuid", patientUuid)
  }

  def getOrdersForPatient(patientUuid: String, orderTypeUuid: String): HttpRequestBuilder = {
    http("get drug orders")
      .get("/openmrs/ws/rest/v1/bahmnicore/orders")
      .queryParam("includeObs", "true")
      .queryParam("numberOfVisits", 4)
      .queryParam("patientUuid", patientUuid)
      .queryParam("orderTypeUuid", orderTypeUuid)
  }

  def getPatientImage(patientUuid: String): HttpRequestBuilder = {
    http("get patient image")
      .get("/openmrs/ws/rest/v1/patientImage")
      .queryParam("patientUuid", patientUuid)
  }

}
