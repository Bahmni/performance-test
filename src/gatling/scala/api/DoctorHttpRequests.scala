package api

import api.Constants._
import io.gatling.core.Predef.{ElFileBody, _}
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder

object DoctorHttpRequests {

  def getPatientConfigFromServer: HttpRequestBuilder = {
    http("patient config")
      .get("/openmrs/ws/rest/v1/bahmnicore/config/patient")
  }

  def getConcept(name: String, v: String): HttpRequestBuilder = {
    http("get " + name + " concept")
      .get("/openmrs/ws/rest/v1/concept")
      .queryParam("s", "byFullySpecifiedName")
      .queryParam("v", v)
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
      .get("/openmrs/ws/rest/v1/visit")
      .queryParam("includeInactive","true")
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

  def getPatientFull(patientUuid: String): HttpRequestBuilder = {
    http("get patient")
      .get("/openmrs/ws/rest/v1/patient/" + patientUuid)
      .queryParam("v", "full")
  }

  def getDiagnoses(patientUuid: String): HttpRequestBuilder = {
    http("get diagnoses for patient")
      .get("/openmrs/ws/rest/v1/bahmnicore/diagnosis/search")
      .queryParam("patientUuid", patientUuid)
  }

  def getConditionalHistory(patientUuid: String): HttpRequestBuilder = {
    http("get conditional history")
      .get("/openmrs/ws/rest/emrapi/conditionhistory")
      .queryParam("patientUuid", patientUuid)
  }

  def getDiseaseTemplates(patientUuid: String): HttpRequestBuilder = {
    http("get disease templates")
      .post("/openmrs/ws/rest/v1/bahmnicore/diseaseTemplates")
      .body(
        StringBody(
          s"""{"patientUuid":"$patientUuid","diseaseTemplateConfigList":[{"title":"Diabetes","templateName":"Diabetes Templates","type":"diseaseTemplate","displayOrder":18,"dashboardConfig":{"showOnly":[]},"expandedViewConfig":{"showDetailsButton":true,"pivotTable":{"numberOfVisits":"15","groupBy":"encounters","obsConcepts":["Weight","Height","Systolic","Diastolic","Diabetes, Foot Exam","Diabetes, Eye Exam"],"drugConcepts":["Ipratropium Pressurised","Garbhpal Rasa"],"labConcepts":["RBS","FBS","PP2BS","Hb1AC","Creatinine","Albumin","Polymorph"]}}}],"startDate":null,"endDate":null}"""
        )
      )
      .asJson
  }

  def getEncoutnerByEncounterTypeUuid(patientUuid: String): HttpRequestBuilder = {
    http("get encounter by uuid")
      .get("/openmrs/ws/rest/v1/encounter")
      .queryParam(
        "v",
        "custom:(uuid,provider,visit:(uuid,startDatetime,stopDatetime),obs:(uuid,concept:(uuid,name),groupMembers:(id,uuid,obsDatetime,value,comment)))"
      )
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

  def getSummaryByVisitUuid(visitUuid: String): HttpRequestBuilder = {
    http("get summary by visit UUID")
      .get("/openmrs/ws/rest/v1/bahmnicore/visit/summary")
      .queryParam("visitUuid", visitUuid)
  }

  def getPatientContext(
      patientUuid: String,
      patientIdentitfiers: String,
      personAttributes: String
  ): HttpRequestBuilder = {
    http("get patient context")
      .get("/openmrs/ws/rest/v1/bahmnicore/patientcontext")
      .queryParam("patientIdentifiers", patientIdentitfiers)
      .queryParam("personAttributes", personAttributes)
      .queryParam("patientUuid", patientUuid)
  }

  def getPatientsInfoWithSqlInpatientInfoTabOfClinic(patientUuid: String, sqlName: String): HttpRequestBuilder = {
    http("get patient upcoming appointment")
      .get("/openmrs/ws/rest/v1/bahmnicore/sql")
      .queryParam("v", "full")
      .queryParam("patientUuid", patientUuid)
      .queryParam("q", sqlName)
  }

  def getLabOrderResults(patientUuid: String): HttpRequestBuilder = {
    http("get lab order results")
      .get("/openmrs/ws/rest/v1/bahmnicore/labOrderResults?numberOfVisits=1")
      .queryParam("patientUuid", patientUuid)
  }

  def getAllObservationTemplates: HttpRequestBuilder = {
    http("Get all observation templates")
      .get(
        "/openmrs/ws/rest/v1/concept?s=byFullySpecifiedName&name=All+Observation+Templates&v=custom:(setMembers:(display))"
      )
  }

  def getObs(patientUuid: String, v: String): HttpRequestBuilder = {
    http("Get obs")
      .get("/openmrs/ws/rest/v1/obs")
      .queryParam("numberOfVisits", "1")
      .queryParam("patient", patientUuid)
      .queryParam("s", "byPatientUuid")
      .queryParam("v", v)
  }

  def getPatientFormTypes(patientUuid: String): HttpRequestBuilder = {
    http("get patient info, form type, visits")
      .get("/openmrs/ws/rest/v1/bahmnicore/patient/" + patientUuid + "/forms?formType=v2&numberOfVisits=1")
  }

  def getLatestPublishedForms: HttpRequestBuilder = {
    http("get latest published forms")
      .get("/openmrs/ws/rest/v1/bahmniie/form/latestPublishedForms")
  }

  def getVisit(visitUuid: String): HttpRequestBuilder = {
    http("get visit")
      .get("/openmrs/ws/rest/v1/visit/" + visitUuid + "?v=custom:(attributes:(value,attributeType:(display,name)))")
  }

  def getForm(formUuid: String): HttpRequestBuilder = {
    http("get form")
      .get("/openmrs/ws/rest/v1/form/" + formUuid)
      .queryParam("v", "custom:(resources:(value))")
  }
  def getFormTranslations(formName: String, formUuid: String): HttpRequestBuilder = {
    http("get form translation")
      .get("/openmrs/ws/rest/v1/bahmniie/form/translations")
      .queryParam("formName", formName)
      .queryParam("formUuid", formUuid)
      .queryParam("formVersion", "1")
      .queryParam("locale", "en")

  }

  def getEntityMappingByLocationEncounter(entityUuid: String): HttpRequestBuilder = {
    http("get entity mapping by location Encounter")
      .get("/openmrs/ws/rest/v1/entitymapping")
      .queryParam("entityUuid", entityUuid)
      .queryParam("mappingType", "location_encountertype")
      .queryParam("s", "byEntityAndMappingType")
  }
  def postEncounter(path: String): HttpRequestBuilder = {
    http("post encounter")
      .post("/openmrs/ws/rest/v1/bahmnicore/bahmniencounter")
      .body(ElFileBody(path))
  }

  def getactiveDrugOrder(Uuid: String): HttpRequestBuilder = {
    http("get active drugOrders")
      .get("/openmrs/ws/rest/v1/bahmnicore/drugOrders/active")
      .queryParam("patientUuid", Uuid)
  }

  def getConfigDrugOrder(): HttpRequestBuilder = {
    http("get config drug order")
      .get("/openmrs/ws/rest/v1/bahmnicore/config/drugOrders")
  }

  def includeActiveVisit(patientUuid: String): HttpRequestBuilder = {
    http("get drug order")
      .get("/openmrs/ws/rest/v1/bahmnicore/drugOrders")
      .queryParam("includeActiveVisit", "true")
      .queryParam("numberOfVisits", "1")
      .queryParam("patientUuid", patientUuid)
  }

  def findDrug(name: String): HttpRequestBuilder = {
    http("find " + name + " drug")
      .get("/openmrs/ws/rest/v1/drug")
      .queryParam("q", name)
      .queryParam("s", "ordered")
      .queryParam("v", "custom:(uuid,strength,name,dosageForm,concept:(uuid,name,names:(name)))")
  }
}
