package org.bahmni.gatling.scenarios

import io.gatling.core.Predef.{jsonPath, _}
import io.gatling.http.Predef._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import org.bahmni.gatling.Configuration.Constants._
import org.bahmni.gatling.HttpRequests._

import scala.concurrent.duration.DurationInt

object Clinical_Dashboard_View_Name_Search_Flow {

  val goToClinicalApp: ChainBuilder = exec(
    getPatientConfigFromServer
      .resources(
        getConcept("Consultation Note"),
        getConcept("Lab Order Notes"),
        getConcept("Impression"),
        getConcept("Dosage Frequency"),
        getConcept("Dosage Instructions"),
        getConcept("All_Tests_and_Panels")
          .queryParam("v", "custom:(uuid,name:(uuid,name),setMembers:(uuid,name:(uuid,name)))"),
        getGlobalProperty("bahmni.relationshipTypeMap"),
        getGlobalProperty("mrs.genders"),
        getGlobalProperty("bahmni.encounterType.default"),
        getRegistrationConcepts,
        getIdentifierTypes,
        getOrderTypes
      )
  )

  val goToClinicalSearch: ChainBuilder = exec(
    getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.activePatients")
      .resources(
        getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.activePatients")
        .check(
          status.is(200),
          jsonPath("$..uuid").find.saveAs("opdPatientId"),
          jsonPath("$..activeVisitUuid").find.saveAs("opdVisitId")
        ),
        getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.activePatientsByProvider"),
        getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.activePatientsByLocation")
      )
  )

  private def gotToDashboard(patientUuid: String, visitUuid: String): ChainBuilder = {
    exec(
      getRelationship(patientUuid)
        .resources(
          getPatient(patientUuid),
          getDiagnoses(patientUuid),
          getDrugOrderConfig,
          getDrugOrdersForPatient(patientUuid),
          getOrdersForPatient(patientUuid, RADIOLOGY_ORDER_TYPE_UUID),
          getOrdersForPatient(patientUuid, USG_ORDER_TYPE_UUID,
            List("USG Order fulfillment, Clinician", "USG Order fulfillment Notes, Findings", "USG Order fulfillment, Remarks")),
          getProgramEnrollment(patientUuid),
          getVisits(patientUuid),
          getObsForDisplayControl(patientUuid),
          getConcept("All Observation Templates"),
          getLabOrderResults(patientUuid),
          getVitals(patientUuid),
          getDisposition(patientUuid),
          getVisit(visitUuid),
          getGlobalProperty("drugOrder.drugOther"),
          getProgramAttributeTypes,
          getEncounter(patientUuid, LOGIN_LOCATION_UUID, PROVIDER_UUID)
        )
    )
  }


 val scn: ScenarioBuilder = scenario("clinical search")
     .exec(goToClinicalApp)
       .exec(goToClinicalSearch)
       .pause(5 seconds, 10 seconds)
       .exec(gotToDashboard("${opdPatientId}", "${opdVisitId}"))
       .pause(5 seconds, 10 seconds)

}
