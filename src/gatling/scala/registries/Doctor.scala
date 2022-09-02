package registries

import api.Constants._
import api.DoctorHttpRequests.{getConfigDrugOrder, _}
import api.HttpRequests._
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import configurations.Feeders._
import scala.util.Random

object Doctor {

  val goToClinicalApp: ChainBuilder = exec(
    getPatientConfigFromServer
      .resources(
        getUser(LOGIN_USER).check(
          jsonPath("$..results[0].uuid").find.saveAs("runTimeUuid")
        ),
        getSession,
        getProviderForUser("#{runTimeUuid}"),
        getRegistrationConcepts,
        getConcept("Consultation Note", "custom:(uuid,name,answers)"),
        getConcept("Lab Order Notes", "custom:(uuid,name,answers)"),
        getConcept("Impression", "custom:(uuid,name,answers)"),
        getConcept("All_Tests_and_Panels", "custom:(uuid,name,answers)")
          .queryParam("v", "custom:(uuid,name:(uuid,name),setMembers:(uuid,name:(uuid,name)))"),
        getConcept("Dosage Frequency", "custom:(uuid,name,answers)"),
        getConcept("Dosage Instructions", "custom:(uuid,name,answers)"),
        getConcept("Stopped Order Reason", "custom:(uuid,name,answers)"),
        getGlobalProperty("mrs.genders"),
        getGlobalProperty("bahmni.relationshipTypeMap"),
        getGlobalProperty("bahmni.encounterType.default"),
        getOrderTypes,
        getGlobalProperty("bahmni.enableAuditLog"),
        getLoginLocations,
        getIdentifierTypes,
        postAuditLog
      )
  )

  val goToClinicalSearch: ChainBuilder = exec(
    getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.activePatients")
      .check(
        jsonPath("$..uuid").findAll.transform(Random.shuffle(_).head).optional.saveAs("opdPatientId"),
        jsonPath("$..[?(@.uuid==" + "\"#{opdPatientId}\")].activeVisitUuid").find.saveAs("opdVisitId")
      )
      .resources(
        getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.activePatientsByProvider"),
        getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.activePatientsByLocation"),
        getPatientImage("#{opdPatientId}")
      )
  )

  def goToDashboard(patientUuid: String): ChainBuilder = exec(
    getRelationship(patientUuid).resources(
      getConceptByName("Follow-up Condition"),
      getOrderTypes,
      getPatientFull(patientUuid),
      getEntityMapping("location_encountertype"),
      getEncounterTypeConsultation.check(
        jsonPath("$..uuid").find.saveAs("encounterTypeUuid")
      ),
      findEncounter(patientUuid),
      postUserInfo("#{runTimeUuid}"),
      getDiagnoses(patientUuid),
      getConditionalHistory(patientUuid),
      getDiseaseTemplates("#{runTimeUuid}"),
      postAuditLog,
      getPatientImage("#{runTimeUuid}"),
      getEncoutnerByEncounterTypeUuid("#{runTimeUuid}"),
      getPatientContext(patientUuid, "ABHA Address", "phoneNumber"),
      getPatientsInfoWithSqlInpatientInfoTabOfClinic(patientUuid, "bahmni.sqlGet.upComingAppointments"),
      getPatientsInfoWithSqlInpatientInfoTabOfClinic(patientUuid, "bahmni.sqlGet.pastAppointments"),
      getRelationship("#{runTimeUuid}"),
      getDrugOrderConfig,
      getDrugOrdersForPatient(patientUuid),
      getLabOrderResults("#{runTimeUuid}"),
      getObservation(
        Map(
          "concept" -> "History and Examination",
          "patientUuid" -> "#{runTimeUuid}",
          "scope" -> "latest",
          "numberOfVisits" -> "1"
        )
      ),
      getObservation(
        Map("concept" -> "Vitals", "patientUuid" -> "#{runTimeUuid}", "scope" -> "latest", "numberOfVisits" -> "1")
      ),
      getObservation(
        Map(
          "concept" -> "Second Vitals",
          "patientUuid" -> "#{runTimeUuid}",
          "scope" -> "latest",
          "numberOfVisits" -> "1"
        )
      ),
      getObservation(
        Seq("Height", "Weight", "BMI Data", "BMI Status Data"),
        Map(
          "patientUuid" -> "#{runTimeUuid}",
          "scope" -> "latest",
          "numberOfVisits" -> "1"
        )
      ),
      getObservation(
        Seq("Prescription", "Discharge Summary", "Radiology Report", "Referral Documents"),
        Map(
          "patientUuid" -> "#{runTimeUuid}",
          "scope" -> "latest",
          "numberOfVisits" -> "1"
        )
      ),
      getVisits(patientUuid),
      getAllObservationTemplates,
      getObs(patientUuid, "visitFormDetails"),
      getPatientFormTypes(patientUuid),
      getLatestPublishedForms,
      getGlobalProperty("drugOrder.drugOther")
    )
  )
  def setSession(): ChainBuilder = exec(
    getSession
      .check(
        jsonPath("$.currentProvider.uuid").find.saveAs("currentProviderUuid")
      )
      .resources(getProviderForUser("#{currentProviderUuid}"))
  )
  def setVisit(patientUuid: String): ChainBuilder = exec(
    getVisits(patientUuid)
      .check(
        jsonPath("$.results[0].uuid").find.saveAs("visitTypeUuid"),
        jsonPath("$..location.uuid").find.saveAs("locationUuid")
      )
      .resources(
        getVisitLocation("#{locationUuid}"),
        getSummaryByVisitUuid("#{visitTypeUuid}"),
        getVisit("#{visitTypeUuid}")
      )
  )
  def goToObservations(pUuid: String): ChainBuilder = exec(
    getConcept(
      "All Observation Templates",
      "custom:(uuid,name:(name,display),names:(uuid,conceptNameType,name),setMembers:(uuid,name:(name,display),names:(uuid,conceptNameType,name),setMembers:(uuid,name:(name,display),names:(uuid,conceptNameType,name))))"
    ).resources(
      postAuditLog(pUuid),
      getLatestPublishedForms
        .check(
          jsonPath("$[?(@.name==\"Vitals\")].uuid").find.saveAs("form_uuid"),
          jsonPath("$[?(@.name==\"Vitals\")].name").find.saveAs("form_name")
        )
    )
  ).exec { session =>
    patientUuid = session("opdPatientId").as[String]
    encounterTypeUuid = session("encounterTypeUuid").as[String]
    session
  }

  val saveEncounter: ChainBuilder = exec(
    getForm("#{form_uuid}").resources(
      getFormTranslations("#{form_name}", "#{form_uuid}"),
      getEntityMappingByLocationEncounter(LOGIN_LOCATION_UUID),
      getEncounterTypeConsultation.check(jsonPath("$..uuid").find.saveAs("encounterTypeUuid")),
      postEncounter("bodies/encounter.json")
    )
  )

  val setOrders: ChainBuilder = exec { session =>
    orders = "[{ \"concept\": {\"uuid\":\"73784885-4113-4e8e-8eab-cd07fb69974e\"}}]"
    session
  }

  def goToMedications(patientUuid: String): ChainBuilder = exec(
    getactiveDrugOrder(patientUuid).resources(
      getConfigDrugOrder(),
      getGlobalProperty("drugOrder.drugOther"),
      postAuditLog(patientUuid),
      includeActiveVisit(patientUuid)
    )
  )

  def addDrug(name: String): ChainBuilder = exec(
    findDrug(name).resources(
      getEntityMappingByLocationEncounter(LOGIN_LOCATION_UUID),
      getEncounterTypeConsultation
    )
  )

  val setMedication: ChainBuilder = exec { session =>
    drugOrders =
      "[{\"careSetting\":\"OUTPATIENT\",\"drug\": {\"name\": \"Dolo 650\",\"form\": null,\"uuid\": \"e3315b29-0bee-4d87-8fcf-0e1941a433c1\"},\"orderType\": \"Drug Order\",\"dosingInstructionType\": \"org.openmrs.module.bahmniemrapi.drugorder.dosinginstructions.FlexibleDosingInstructions\",\"dosingInstructions\": {\"dose\": 1,\"doseUnits\": \"Tablet(s)\",\n        \"route\": \"Oral\",\n        \"frequency\": \"Once a day\",\"asNeeded\": false,\"administrationInstructions\": \"{\\\"instructions\\\":\\\"As directed\\\"}\",\"quantity\": 3,\"quantityUnits\": \"Tablet(s)\",\"numberOfRefills\": 0},\"duration\": 3,\"durationUnits\": \"Day(s)\",\"scheduledDate\": null,\"autoExpireDate\": null,\"dateStopped\": null,\"orderGroup\": {\"orderSet\": {}}}]"
    session
  }
}
