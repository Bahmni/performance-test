package registries

import api.Constants._
import api.DoctorHttpRequests._
import api.HttpRequests._
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

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
        getConcept("Consultation Note"),
        getConcept("Lab Order Notes"),
        getConcept("Impression"),
        getConcept("All_Tests_and_Panels")
          .queryParam("v", "custom:(uuid,name:(uuid,name),setMembers:(uuid,name:(uuid,name)))"),
        getConcept("Dosage Frequency"),
        getConcept("Dosage Instructions"),
        getConcept("Stopped Order Reason"),
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

  val goToClinicalSearch :ChainBuilder = exec(
    getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.activePatients")
      .check(
        jsonPath(PATIENT_NAME).find.saveAs("opdPatientId"),
        jsonPath("$..activeVisitUuid").find.saveAs("opdVisitId")
      )
      .resources(
        getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.activePatientsByProvider"),
        getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.activePatientsByLocation"),
        getPatientImage("#{opdPatientId}")
      )
  )

  def goToDashboard(patientUuid: String): ChainBuilder = exec(
    getRelationship(patientUuid)
      .resources(
        getSession.check(
          jsonPath("$.currentProvider.uuid").find.saveAs("currentProviderUuid")
        ),
        getVisitLocation("${locationUuid}"),
        getConceptByName("Follow-up Condition"),
        getVisits(patientUuid).check(
          jsonPath("$.results[0].uuid").find.saveAs( "visitTypeUuid"),
          jsonPath("$..location.uuid").find.saveAs("locationUuid")
        ),
        getUser(LOGIN_USER).check(
          jsonPath("$..results[0].uuid").find.saveAs("runTimeUuid")
        ),
        getProviderForUser("${currentProviderUuid}"),
        getOrderTypes,
        getPatientFull(patientUuid),
        getEntityMapping("location_encountertype"),
        getSummaryByVisitUuid("${visitTypeUuid}"),
        getEncounterTypeConsultation.check(
          jsonPath("$..uuid").find.saveAs( "encounterTypeUuid")
        ),
        findEncounter(patientUuid),
        postUserInfo(patientUuid),
        getDiagnoses("#{runtimeUuid}"),
        getConditionalHistory("#{runtimeUuid}"),
        getDiseaseTemplates("#{runtimeUuid}"),
        postAuditLog,
        getPatientImage("#{runtimeUuid}"),
        getEncoutnerByEncounterTypeUuid("#{runtimeUuid}"),
        getPatientContext("#{runtimeUuid}", "ABHA Address", "phoneNumber"),
        getPatientsInfoWithSqlInpatientInfoTabOfClinic(patientUuid, "bahmni.sqlGet.upComingAppointments"),
        getRelationship("#{runtimeUuid}"),
        getDrugOrderConfig,
        getDrugOrdersForPatient("#{runtimeUuid}"),
        getLabOrderResults("#{runtimeUuid}"),
        getObservation("#{runtimeUuid}",Array("History and Examination")),
        getObservation("#{runtimeUuid}",Array("Vitals")),
        getObservation("#{runtimeUuid}",Array("Second Vitals")),
        getObservation("#{runtimeUuid}",Array("HEIGHT", "Weight", "BMI Data", "BMI Status Data")),
        getObservation("#{runtimeUuid}",Array("Prescription", "Discharge Summary", "Radiology Report", "Referral Documents")),
        getConditionalHistory("#{runtimeUuid}"),
        getVisits("#{runtimeUuid}"),
        getAllObservationTemplates,
        getObs("#{runtimeUuid}", "visitFormDetails"),
        getPatientFormTypes("#{runtimeUuid}"),
        getLatestPublishedForms,
        getVisit("#{visitTypeUuid}"),
        getGlobalProperty("drugOrder.drugOther")
        //          getOrdersForPatient(patientUuid, RADIOLOGY_ORDER_TYPE_UUID),
//          getOrdersForPatient(patientUuid, USG_ORDER_TYPE_UUID,
//            List("USG Order fulfillment, Clinician", "USG Order fulfillment Notes, Findings", "USG Order fulfillment, Remarks")),
//          getProgramEnrollment(patientUuid),
//          getObsForDisplayControl(patientUuid),
//          getConcept("All Observation Templates"),
//          getLabOrderResults(patientUuid),
//          getVitals(patientUuid),
//          getDisposition(patientUuid),
//          getVisit("${visitTypeUuid}"),
//          getSummaryByVisitUuid("${visitTypeUuid}"),
//          postUserInfo("${runTimeUuid}"),
//          postFindEncounter(patientUuid, "${locationUuid}", "${currentProviderUuid}",
//            "${encounterTypeUuid}"),
//          getEntityMappingByUserId("${locationUuid}"),
//          getVisitLocation("${locationUuid}"),
//          getGlobalProperty("drugOrder.drugOther"),
//          getProgramAttributeTypes,
//          getPatientContext(patientUuid),
//          getPatientsInfoWithSqlInpatientInfoTabOfClinic(patientUuid, "bahmni.sqlGet.upComingAppointments"),
//          getPatientsInfoWithSqlInpatientInfoTabOfClinic(patientUuid, "bahmni.sqlGet.pastAppointments"),
//          getPatientDisposition(patientUuid),
//          getPatientFormTypes(patientUuid),
//          getLatestPublishedForms,
//          getConceptByNameAndMemberDisplay("All Observation Templates"),
//          getFlowSheet(patientUuid)
      )
  )
}
