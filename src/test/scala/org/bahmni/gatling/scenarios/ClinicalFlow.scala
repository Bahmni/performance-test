package org.bahmni.gatling.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import org.bahmni.gatling.Configuration
import org.bahmni.gatling.Configuration.Constants._
import org.bahmni.gatling.HttpRequests._
import scala.concurrent.duration.DurationInt

object ClinicalFlow {

  val goToClinicalApp: ChainBuilder = exec(
    getPatientConfigFromServer
      .resources(
        getConcept("Consultation Note"),
        getConcept("Lab Order Notes"),
        getConcept("Impression"),
        getConcept("Dosage Frequency"),
        getConcept("Dosage Instructions"),
        getConcept("Stopped Order Reason"),
        getConcept("All_Tests_and_Panels")
          .queryParam("v", "custom:(uuid,name:(uuid,name),setMembers:(uuid,name:(uuid,name)))"),
        getGlobalProperty("bahmni.relationshipTypeMap"),
        getGlobalProperty("mrs.genders"),
        getGlobalProperty("bahmni.encounterType.default"),
        getRegistrationConcepts,
        getIdentifierTypes,
        getOrderTypes,
        getGlobalProperty("bahmni.enableAuditLog"),
        getLoginLocations,
        postAuditLog
      )
  )

  val goToClinicalSearch: ChainBuilder = exec(
    getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.activePatients")
      .resources(
        getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.activePatientsByProvider"),
        getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.activePatientsByLocation")
      )
  )

  private def gotToDashboard(patientUuid: String, visitUuid: String): ChainBuilder = {
    exec(
      getRelationship(patientUuid)
        .resources(
          getSession.check(
            jsonPath("$.currentProvider.uuid").find.saveAs("currentProviderUuid")
          ),
          getConceptByName("Follow-up Condition"),
          getUser(LOGIN_USER).check(
            jsonPath("$..results[0].uuid").find.saveAs("runTimeUuid")
          ),
          getProviderForUser("${currentProviderUuid}"),
          getOrderTypes,
          getVisits(patientUuid).check(
            jsonPath("$.results[0].uuid").find.saveAs( "visitTypeUuid"),
            jsonPath("$..location.uuid").find.saveAs("locationUuid")

          ),
          getEncounterTypeConsultation.check(
            jsonPath("$..uuid").find.saveAs( "encounterTypeUuid")
          ),
          getPatient(patientUuid),
          getDiagnoses(patientUuid),
          getConditionalHistory(patientUuid),
          getDiseaseTemplates(patientUuid),
          getEncoutnerByEncounterTypeUuid(patientUuid),
          postAuditLog,
          getDrugOrderConfig,
          getDrugOrdersForPatient(patientUuid),
          getOrdersForPatient(patientUuid, RADIOLOGY_ORDER_TYPE_UUID),
          getOrdersForPatient(patientUuid, USG_ORDER_TYPE_UUID,
            List("USG Order fulfillment, Clinician", "USG Order fulfillment Notes, Findings", "USG Order fulfillment, Remarks")),
          getProgramEnrollment(patientUuid),
          getObsForDisplayControl(patientUuid),
          getConcept("All Observation Templates"),
          getLabOrderResults(patientUuid),
          getVitals(patientUuid),
          getDisposition(patientUuid),
          getVisit("${visitTypeUuid}"),
          getSummaryByVisitUuid("${visitTypeUuid}"),
          postUserInfo("${runTimeUuid}"),
          postFindEncounter(patientUuid, "${locationUuid}", "${currentProviderUuid}",
            "${encounterTypeUuid}"),
          getEntityMappingByUserId("${locationUuid}"),
          getVisitLocation("${locationUuid}"),
          getGlobalProperty("drugOrder.drugOther"),
          getProgramAttributeTypes,
          getPatientContext(patientUuid),
          getPatientsInfoWithSqlInpatientInfoTabOfClinic(patientUuid, "bahmni.sqlGet.upComingAppointments"),
          getPatientsInfoWithSqlInpatientInfoTabOfClinic(patientUuid, "bahmni.sqlGet.pastAppointments"),
          getPatientDisposition(patientUuid),
          getPatientFormTypes(patientUuid),
          getLatestPublishedForms,
          getConceptByNameAndMemberDisplay("All Observation Templates"),
          getFlowSheet(patientUuid)
        )
    )
  }

  val scn: ScenarioBuilder = scenario("clinical search")
    .during(Configuration.Load.DURATION) {
      exec(goToClinicalApp)
        .exec(goToClinicalSearch)
        .exec(gotToDashboard(PATIENT_UUID, VISIT_UUID))
        .pause(100 seconds)
        .exec(goToClinicalSearch)
        .exec(gotToDashboard(ANOTHER_PATIENT_UUID, ANOTHER_VISIT_UUID))
    }
}
