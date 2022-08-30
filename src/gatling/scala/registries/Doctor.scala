package registries

import api.Constants._
import api.DoctorHttpRequests._
import api.HttpRequests._
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

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

  def goToClinicalSearch(): ChainBuilder = exec(
    getPatientsInSearchTab(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.activePatients")
      .check(
        jsonPath("$..uuid").findAll.transform(Random.shuffle(_).head).optional.saveAs("opdPatientId"),
        jsonPath("$..[?(@.uuid=="+"\"#{opdPatientId}\")].activeVisitUuid").find.saveAs("opdVisitId")
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
        Map(
          "concept" -> "HEIGHT",
          "concept" -> "Weight",
          "concept" -> "BMI Data",
          "concept" -> "BMI Status Data",
          "patientUuid" -> "#{runTimeUuid}",
          "scope" -> "latest",
          "numberOfVisits" -> "1"
        )
      ),
      getObservation(
        Map(
          "concept" -> "Prescription",
          "concept" -> "Discharge Summary",
          "concept" -> "Radiology Report",
          "concept" -> "Referral Documents",
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
    getSession.check(
      jsonPath("$.currentProvider.uuid").find.saveAs("currentProviderUuid")
    ).resources(getProviderForUser("#{currentProviderUuid}"))
  )
  def setVisit(patientUuid: String): ChainBuilder = exec(
    getVisits(patientUuid).check(
      jsonPath("$.results[0].uuid").find.saveAs("visitTypeUuid"),
      jsonPath("$..location.uuid").find.saveAs("locationUuid")).resources(
      getVisitLocation("#{locationUuid}"),
      getSummaryByVisitUuid("#{visitTypeUuid}"),
      getVisit("#{visitTypeUuid}"))
  )

}
