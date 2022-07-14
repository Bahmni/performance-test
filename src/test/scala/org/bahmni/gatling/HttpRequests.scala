package org.bahmni.gatling

import io.gatling.core.Predef._
import io.gatling.core.body.{Body, CompositeByteArrayBody, StringBody}
import io.gatling.core.session.Expression
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import org.bahmni.gatling.Configuration.Constants._
import scala.util.Random
import scala.io.Source

object HttpRequests {

  def getUser(username: String): HttpRequestBuilder = {
    http("get user")
      .get("/openmrs/ws/rest/v1/user?v=custom:(username,uuid,person:(uuid,),privileges:(name,retired),userProperties)")
      .queryParam("username", username)
  }

  def getSession:HttpRequestBuilder = {
    http("get session")
      .get("/openmrs/ws/rest/v1/session?v=custom:(uuid)")
  }

  def postUserInfo(loggedInUserUuid: String):HttpRequestBuilder = {
    http("post user info")
      .post("/openmrs/ws/rest/v1/user/"+loggedInUserUuid)
      .body(StringBody(
        s"""{"uuid":"$loggedInUserUuid","userProperties":{"defaultLocale":"en","favouriteObsTemplates":"",
           "recentlyViewedPatients":"","loginAttempts":"0","favouriteWards":"General Ward###Labour Ward"}}"""))
      .asJson
  }

  def postAuditLog:HttpRequestBuilder = {
    http("post audit log")
      .post("/openmrs/ws/rest/v1/auditlog")
      .body(StringBody("{\"eventType\":\"DUMMY_PERF_MESSAGE\",\"message\":\"DUMMY_PERF_TEST_MESSAGE\",\"module\":\"MODULE_PERF_TEST\"}"))
      .asJson
  }

  def getGlobalProperty(property: String): HttpRequestBuilder = {
    http("get " + property + " global property")
      .get("/openmrs/ws/rest/v1/bahmnicore/sql/globalproperty")
      .queryParam("property", property)
  }

  def getProviderForUser(userUuid: String): HttpRequestBuilder = {
    http("get provider")
      .get("/openmrs/ws/rest/v1/provider")
      .queryParam("user", userUuid)
  }

  def getByVisitLocation(visitLocationUuid: String): HttpRequestBuilder = {
    http("get visit location")
    .get("/openmrs/ws/rest/v1/location/"+visitLocationUuid)
  }

  def getLoginLocations: HttpRequestBuilder = {
    http("get login locations")
      .get("/openmrs/ws/rest/v1/location?operator=ALL&s=byTags&tags=Login+Location&v=default")
  }

  def getAddressHierarchyLevel: HttpRequestBuilder = {
    http("get address hierarchy level")
      .get("/openmrs/module/addresshierarchy/ajax/getOrderedAddressHierarchyLevels.form")
  }

  def getVisitLocation(visitLocationUuid: String): HttpRequestBuilder = {
    http("get visit location")
      .get("/openmrs/ws/rest/v1/bahmnicore/visitLocation/" + visitLocationUuid)
  }

  def getIdentifierTypes: HttpRequestBuilder = {
    http("get identifier types")
      .get("/openmrs/ws/rest/v1/idgen/identifiertype")
  }

  def getRelationshipTypes: HttpRequestBuilder = {
    http("get relationship types")
      .get("/openmrs/ws/rest/v1/relationshiptype?v=custom:(aIsToB,bIsToA,uuid)")
  }

  def getOrderTypes: HttpRequestBuilder = {
    http("get order types")
      .get("/openmrs/ws/rest/v1/ordertype?v=custom:(uuid,display,conceptClasses:(uuid,display,name))")
  }

  def getEntityMapping: HttpRequestBuilder = {
    http("get LoginLocation to visit type mapping")
      .get("/openmrs/ws/rest/v1/entitymapping?mappingType=loginlocation_visittype&s=byEntityAndMappingType")
  }

  def getEntityMappingByUserId(entityUuid : String): HttpRequestBuilder = {
    http("get LoginLocation to visit type mapping by user id")
      .get("/openmrs/ws/rest/v1/entitymapping?mappingType=loginlocation_visittype&s=byEntityAndMappingType")
      .queryParam("entityUuid",entityUuid)
  }

  def getSummaryByVisitUuid (visitUuid : String): HttpRequestBuilder = {
    http("get summary by visit UUID")
      .get("/openmrs/ws/rest/v1/bahmnicore/visit/summary")
      .queryParam("visitUuid", visitUuid)
  }

  def getEncounterTypeConsultation: HttpRequestBuilder = {
    http("get encounter type consultation")
      .get("/openmrs/ws/rest/v1/encountertype/Consultation")
  }

  def getPersonAttributeTypes: HttpRequestBuilder = {
    http("get person attribute types")
      .get("/openmrs/ws/rest/v1/personattributetype?v=custom:(uuid,name,sortWeight,description,format,concept)")
  }

  def getRegistrationConcepts: HttpRequestBuilder = {
    http("get registration concepts")
      .get("/openmrs/ws/rest/v1/bahmnicore/config/bahmniencounter?callerContext=REGISTRATION_CONCEPTS")
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

  def getConceptByNameAndMemberDisplay(name: String): HttpRequestBuilder = {
    http("get " + name + " concept")
      .get("/openmrs/ws/rest/v1/concept")
      .queryParam("s", "byFullySpecifiedName")
      .queryParam("v", "custom:(setMembers:(display))")
      .queryParam("name", name)
  }

  def getConceptByName(name: String): HttpRequestBuilder = {
    http("get " + name + " concept")
      .get("/openmrs/ws/rest/v1/concept")
      .queryParam("s", "byFullySpecifiedName")
      .queryParam("v", "custom:(uuid,name:(name))")
      .queryParam("name", name)
  }

  def getConceptByNameCustomFieldAndNameType(nameType: String, name: String, customField: String): HttpRequestBuilder = {
    http("get " + name + " concept")
      .get("/openmrs/ws/rest/v1/concept")
      .queryParam("s", nameType)
      .queryParam("name", name)
      .queryParam("v", customField)
      .queryParam("locale", "en")
  }

  def getReasonForDeath: HttpRequestBuilder = {
    http("Get reason for death")
      .get("/openmrs/ws/rest/v1/concept?s=byFullySpecifiedName&name=Reason+For+Death&v=custom:(uuid,name,set,names,setMembers:(uuid,display,name:(uuid,name),names,retired))")
  }

  def getPatientsInSearchTab(locationUuid: String, providerUuid: String, sqlName: String): HttpRequestBuilder = {
    http(sqlName)
      .get("/openmrs/ws/rest/v1/bahmnicore/sql")
      .queryParam("location_uuid", locationUuid)
      .queryParam("provider_uuid", providerUuid)
      .queryParam("q", sqlName)
      .queryParam("v", "full")
  }

  def getPatientsInfoWithSqlInpatientInfoTabOfClinic(patientUuid: String, sqlName : String): HttpRequestBuilder = {
    http("get patient upcoming appointment")
      .get("/openmrs/ws/rest/v1/bahmnicore/sql")
      .queryParam("v", "full")
      .queryParam("patientUuid", patientUuid)
      .queryParam("q", sqlName)
  }

  def getPatientDisposition(patientUuid: String): HttpRequestBuilder = {
    http("get patient disposition")
      .get("/openmrs/ws/rest/v1/bahmnicore/disposition/patientWithLocale?locale=en&numberOfVisits=1")
      .queryParam("patientUuid", patientUuid)
  }

  def getWardListDetails(locationName: String): HttpRequestBuilder = {
    http("get ward list " + locationName)
      .get("/openmrs/ws/rest/v1/bahmnicore/sql?q=emrapi.sqlGet.wardsListDetails&v=full")
      .queryParam("location_name", locationName)
  }

  def getRelationship(personUuid: String): HttpRequestBuilder = {
    http("get relationship")
      .get("/openmrs/ws/rest/v1/relationship")
      .queryParam("person", personUuid)
  }

  def getPatient(patientUuid: String): HttpRequestBuilder = {
    http("get patient")
      .get("/openmrs/ws/rest/v1/patient/" + patientUuid)
  }

  def getPatientProfileAfterRegistration(patientUuid: String): HttpRequestBuilder = {
    http("get patient")
      .get("/openmrs/ws/rest/v1/patientprofile/" + patientUuid)
      .queryParam("v","full")
  }

  def getDiagnoses(patientUuid: String): HttpRequestBuilder = {
    http("get diagnoses for patient")
      .get("/openmrs/ws/rest/v1/bahmnicore/diagnosis/search")
      .queryParam("patientUuid", patientUuid)
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

  def getOrdersForPatient(patientUuid: String, orderTypeUuid: String, concepts: List[String]): HttpRequestBuilder = {
    http("get drug orders")
      .get("/openmrs/ws/rest/v1/bahmnicore/orders")
      .queryParam("includeObs", "true")
      .queryParam("numberOfVisits", 1)
      .queryParam("patientUuid", patientUuid)
      .queryParam("orderTypeUuid", orderTypeUuid)
      .queryParam("concepts", concepts)
  }

  def getProgramEnrollment(patientUuid: String): HttpRequestBuilder = {
    http("get program enrollment")
      .get("/openmrs/ws/rest/v1/bahmniprogramenrollment?v=full")
      .queryParam("patient", patientUuid)
  }

  def getVisits(patientUuid: String): HttpRequestBuilder = {
    http("getVisits")
      .get("/openmrs/ws/rest/v1/visit?includeInactive=true")
      .queryParam("v", "custom:(uuid,visitType,startDatetime,stopDatetime,location,encounters:(uuid))")
      .queryParam("patient", patientUuid)
  }

  def getPatientVisitInfo(patientUuid: String): HttpRequestBuilder = {
    http("getVisits")
      .get("/openmrs/ws/rest/v1/visit?includeInactive=false")
      .queryParam("v", "custom:(uuid,location:(uuid))")
      .queryParam("patient", patientUuid)
  }

  def getObsForDisplayControl(patientUuid: String): HttpRequestBuilder = {
    http("get obs")
      .get("/openmrs/ws/rest/v1/obs?conceptNames=Patient+Vitals&conceptNames=Acute+OPD+visit" +
        "&conceptNames=Acute+Abdominal+Complaint&conceptNames=Acute+HEENT+Complaint&conceptNames=Acute+MSK+Complaint" +
        "&conceptNames=Acute+Skin+Complaint&conceptNames=Acute+GU+Complaint&conceptNames=ANC" +
        "&conceptNames=Asthma,+Progress&conceptNames=Childhood+Illness+(Children+aged+below+2+months)" +
        "&conceptNames=Childhood+Illness(+Children+aged+2+months+to+5+years)&conceptNames=CHP+Antenatal+Care" +
        "&conceptNames=CHP+Chronic+Disease+Counseling&conceptNames=CHP+Registration" +
        "&conceptNames=Chronic+Kidney+Disease,+Progress&conceptNames=COPD,+Progress" +
        "&conceptNames=Congestive+Heart+Failure,+Progress&conceptNames=Death+Note" +
        "&conceptNames=Delivery+note&conceptNames=Dental&conceptNames=Dental+Note&conceptNames=Depression+Note" +
        "&conceptNames=Diabetes,+Progress&conceptNames=Discharge+Note&conceptNames=DRTuberculosis,+Intake" +
        "&conceptNames=ECG+Notes&conceptNames=ER+General+Notes&conceptNames=Family+Planning+Template" +
        "&conceptNames=HIV+Testing+and+Counseling+Intake+Template&conceptNames=HIV+Treatment+and+Care+Intake+Template" +
        "&conceptNames=HIV+Treatment+and+Care+Progress+Template&conceptNames=Hypertension,+Progress" +
        "&conceptNames=IMAM+Program&conceptNames=Kalaazar,+Template&conceptNames=Leprosy,+Template" +
        "&conceptNames=Mental+Health&conceptNames=Mental+Health+Screener&conceptNames=Malaria" +
        "&conceptNames=Nutrition&conceptNames=Operative+Notes&conceptNames=Opioid+Substitution+Therapy+Intake+Template" +
        "&conceptNames=Opioid+Substitution+Therapy+Intake+Template&conceptNames=Opportunistic+Infection+Template" +
        "&conceptNames=Orthopaedic+Examination&conceptNames=PNC+Note&conceptNames=Post+Delivery+Family+Planning+Checklist" +
        "&conceptNames=Prevention+of+Mother-to-Child+Transmission+Intake+Template&conceptNames=Procedure+Notes" +
        "&conceptNames=Psychiatrist%27s+Recommendation&conceptNames=Psychosis+Followup+Note" +
        "&conceptNames=Psychosis+Intake+Note&conceptNames=Rheumatoid+Arthritis,+Progress" +
        "&conceptNames=Rheumatic+Heart+Disease,+Progress&conceptNames=Safe+Abortion" +
        "&conceptNames=Seizure+Disorder,+Progress&conceptNames=Sextually+Transmitted+Infections+Intake+Template" +
        "&conceptNames=Stroke+OPD&conceptNames=Trauma+Notes&conceptNames=Tuberculosis,+Intake&conceptNames=USG+Notes" +
        "&numberOfVisits=1&s=byPatientUuid&v=visitFormDetails")
      .queryParam("patient", patientUuid)
  }

  def getLabOrderResults(patientUuid: String): HttpRequestBuilder = {
    http("get lab order results")
      .get("/openmrs/ws/rest/v1/bahmnicore/labOrderResults?numberOfVisits=1")
      .queryParam("patientUuid", patientUuid)
  }

  def getVitals(patientUuid: String): HttpRequestBuilder = {
    http("get vitals")
      .get("/openmrs/ws/rest/v1/bahmnicore/observations?numberOfVisits=2")
      .queryParam("concept", "Vitals")
      .queryParam("patientUuid", patientUuid)
  }

  def getDisposition(patientUuid: String): HttpRequestBuilder = {
    http("get dispostion")
      .get("/openmrs/ws/rest/v1/bahmnicore/disposition/patient?numberOfVisits=1")
      .queryParam("patientUuid", patientUuid)
  }

  def getVisit(visitUuid: String): HttpRequestBuilder = {
    http("get visit")
      .get("/openmrs/ws/rest/v1/visit/" + visitUuid + "?v=custom:(attributes:(value,attributeType:(display,name)))")
  }

  def getPatientImage(patientUuid: String): HttpRequestBuilder = {
    http("get patient image")
      .get("/openmrs/ws/rest/v1/patientImage")
      .queryParam("patientUuid", patientUuid)
  }

  def getProgramAttributeTypes: HttpRequestBuilder = {
    http("get program attribute types")
      .get("/openmrs/ws/rest/v1/programattributetype?v=custom:(uuid,name,description,datatypeClassname,datatypeConfig,concept)")
  }

  def getEncounter(patientUuid: String, locationUuid: String, providerUuid: String): HttpRequestBuilder = {
    http("find encounter")
      .post("/openmrs/ws/rest/v1/bahmnicore/bahmniencounter/find")
      .body(StringBody(s"""{"patientUuid":"$patientUuid","providerUuids":["$providerUuid"],"includeAll":false,"encounterDateTimeFrom":null,"encounterDateTimeTo":null,"encounterTypeUuids":["$ENCOUNTER_TYPE_UUID"],"locationUuid":"$locationUuid"}"""))
      .asJson
  }

  def postFindEncounter(patientUuid: String, locationUuid: String, providerUuid: String, encounterTypeUuid: String):
  HttpRequestBuilder = {
    http("post encounter find")
      .post("/openmrs/ws/rest/v1/bahmnicore/bahmniencounter/find")
      .body(StringBody(s"""{"patientUuid":"$patientUuid","providerUuids":["$providerUuid"],"includeAll":false,"encounterDateTimeFrom":null,"encounterDateTimeTo":null,"encounterTypeUuids":["$encounterTypeUuid"],"locationUuid":"$locationUuid"}"""))
      .asJson
  }

  def getEncoutnerByEncounterTypeUuid(patientUuid: String) : HttpRequestBuilder = {
    http("get encounter by uuid")
      .get("/openmrs/ws/rest/v1/encounter")
      .queryParam("v", "custom:(uuid,provider,visit:(uuid,startDatetime,stopDatetime),obs:(uuid,concept:(uuid,name),groupMembers:(id,uuid,obsDatetime,value,comment)))")
      .queryParam("patient", patientUuid)
      .queryParam("encounterType", ENCOUNTER_TYPE_UUID)
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

  def getPatientContext(patientUuid : String) : HttpRequestBuilder = {
    http("get patient context")
      .get("/openmrs/ws/rest/v1/bahmnicore/patientcontext")
      .queryParam("patientIdentifiers", "National+ID")
      .queryParam("personAttributes", "class")
      .queryParam("personAttributes", "caste")
      .queryParam("programAttributes", "Id_Number")
      .queryParam("programAttributes", "Doctor")
      .queryParam("programAttributes", "Stage")
      .queryParam("patientUuid", patientUuid)
  }

  def getPatientFormTypes(patientUuid : String) : HttpRequestBuilder = {
    http("get patient info, form type, visits")
      .get("/openmrs/ws/rest/v1/bahmnicore/patient/"+patientUuid+"/forms?formType=v2&numberOfVisits=1")
  }

  def getLatestPublishedForms: HttpRequestBuilder = {
    http("get latest published forms")
      .get("/openmrs/ws/rest/v1/bahmniie/form/latestPublishedForms")
  }

  def getAdmissionLocations: HttpRequestBuilder = {
    http("get admission locations")
      .get("/openmrs/ws/rest/v1/admissionLocation/")
  }

  def getPatienFeedRecentPage: HttpRequestBuilder = {
    http("patient recent")
      .get("/openmrs/ws/atomfeed/patient/recent")
  }

  def getEncounterFeedRecentPage: HttpRequestBuilder = {
    http("encounter recent")
      .get("/openmrs/ws/atomfeed/encounter/recent")
  }

  def getEncounterFeedcontent(encounterUuid: String): HttpRequestBuilder = {
    http("encounter feed content")
      .get("/openmrs/ws/rest/v1/bahmnicore/bahmniencounter/" + encounterUuid + "?includeAll=true")
  }

  def getPatientFeedcontent(patientUuid: String): HttpRequestBuilder = {
    http("patient feed content")
      .get("/openmrs/ws/rest/v1/patient/" + patientUuid )
  }

  def getLabFeedcontent(referenceDataUuid: String): HttpRequestBuilder = {
    http("lab feed content")
      .get("/openmrs/ws/rest/v1/reference-data/all-tests-and-panels/"+referenceDataUuid )
  }

  def getLabFeedRecentPage: HttpRequestBuilder = {
    http("lab recent")
      .get("/openmrs/ws/atomfeed/lab/recent")
  }

  def getPatientImage: HttpRequestBuilder = {
    http("get patient image")
      .get("/openmrs/ws/rest/v1/patientImage?patientUuid=2e1a2899-409c-40c4-b8fd-3476bb11dce3&q=2016-12-06T12:19:15.166Z")
  }

  def createPatientRequest(fileBody : CompositeByteArrayBody): HttpRequestBuilder = {
      http("create patient")
        .post("/openmrs/ws/rest/v1/bahmnicore/patientprofile")
        .body(fileBody).asJson
  }

  def ccreatePatientRequest(body: Body with (Expression[String])): HttpRequestBuilder = {
    http("create patient")
      .post("/openmrs/ws/rest/v1/bahmnicore/patientprofile")
      .body(body).asJson
  }

  def startVisitRequest(patient_uuid : String, opd_visit_type_id : String, login_location_id : String): HttpRequestBuilder = {
    http("start visit")
      .post("/openmrs/ws/rest/v1/visit")
      .body(StringBody(s"""{"patient":"$patient_uuid","visitType":"$opd_visit_type_id","location":"$login_location_id"}"""))
      .asJson
  }

  def getFlowSheet(patientUuid : String): HttpRequestBuilder = {
    http("get flow sheet")
      .get("/openmrs/ws/rest/v1/bahmnicore/observations/flowSheet")
      .queryParam("groupByConcept", "")
      .queryParam("numberOfVisits", "1")
      .queryParam("patientUuid",patientUuid)
  }

  def closePatientVisit(patientUuid: String, visitUuid: String): HttpRequestBuilder = {
    http("close patient visit")
      .post("/openmrs/ws/rest/v1/bahmnicore/visit/endVisit")
      .queryParam("visitUuid", visitUuid)
      .body(StringBody("{\"withCredentials\":true}"))
  }


  def postHistoryAndExaminationEncounter(patientUuid: String, encounterTypeUuid: String, locationUuid: String,
                                         currentProviderUuid: String, imageUrl: String): HttpRequestBuilder = {

      val jsonString: String = Source.fromFile(("src/test/resources/bodies/" +
        "encounter_history_and_examination_body.json")).mkString

      val json = dijon.parse(jsonString)
      json.patientUuid = patientUuid;
      json.encounterTypeUuid = encounterTypeUuid;
      json.locationUuid = locationUuid;
      json.providers(0).uuid = currentProviderUuid;
      json.observations(0).groupMembers(5).groupMembers(0).value = imageUrl;
      json.observations(0).groupMembers(5).groupMembers(0).autocompleteValue = imageUrl;
      json.observations(0).groupMembers(5).groupMembers(0)._value = imageUrl;

      http("post history and examination for a patient")
        .post("/openmrs/ws/rest/v1/bahmnicore/bahmniencounter")
        .body(StringBody(json.toString()))
        .asJson
    }

  def postVitalsEncounter(patientUuid: String, encounterTypeUuid: String, locationUuid: String,
                                         currentProviderUuid: String): HttpRequestBuilder = {

    val jsonString: String = Source.fromFile(("src/test/resources/bodies/" +
      "vitals_body.json")).mkString

    val json = dijon.parse(jsonString)
    json.patientUuid = patientUuid;
    json.encounterTypeUuid = encounterTypeUuid;
    json.locationUuid = locationUuid;
    json.providers(0).uuid = currentProviderUuid;

    http("post vitals for a patient")
      .post("/openmrs/ws/rest/v1/bahmnicore/bahmniencounter")
      .body(StringBody(json.toString()))
      .asJson
  }

  def postOrderEncounter(patientUuid: String, encounterTypeUuid: String, locationUuid: String,
                          currentProviderUuid: String): HttpRequestBuilder = {

    val jsonString: String = Source.fromFile(("src/test/resources/bodies/" +
      "orders_body.json")).mkString

    val json = dijon.parse(jsonString)
    json.patientUuid = patientUuid;
    json.encounterTypeUuid = encounterTypeUuid;
    json.locationUuid = locationUuid;
    json.providers(0).uuid = currentProviderUuid;

    http("post orders for a patient")
      .post("/openmrs/ws/rest/v1/bahmnicore/bahmniencounter")
      .body(StringBody(json.toString()))
      .asJson
  }

  def postDrugOrderEncounter(patientUuid: String, encounterTypeUuid: String, locationUuid: String,
                                         currentProviderUuid: String): HttpRequestBuilder = {

    val jsonString: String = Source.fromFile(("src/test/resources/bodies/" +
      "drug_order_body.json")).mkString

    val randomDrugQuantity: Int = Random.nextInt(500)

    val json = dijon.parse(jsonString)
    json.patientUuid = patientUuid;
    json.encounterTypeUuid = encounterTypeUuid;
    json.locationUuid = locationUuid;
    json.providers(0).uuid = currentProviderUuid;
    json.drugOrders(0).drugNonCoded = "TestDrug-" + randomDrugQuantity + "mg";
    http("post drug info for a patient")
      .post("/openmrs/ws/rest/v1/bahmnicore/bahmniencounter")
      .body(StringBody(json.toString()))
      .asJson
  }

  def postUploadDocument(patientUuid: String): HttpRequestBuilder = {

    val jsonString = Source.fromFile("src/test/resources/bodies/post_consultation_imageFile_body.json").mkString
    val json = dijon.parse(jsonString)
    json.patientUuid = patientUuid;

    http("post patient consultation image")
      .post("/openmrs/ws/rest/v1/bahmnicore/visitDocument/uploadDocument")
      .body(StringBody(json.toString()))
      .asJson
  }
}
