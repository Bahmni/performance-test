package registries

import api.Constants._
import api.DoctorHttpRequests._
import api.HttpRequests._
import configurations.Feeders._
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import registries.Frontdesk.getPatientAvatars

import scala.collection.mutable
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Random

object Doctor {

  val inMemoryOpdPatientsQueue: mutable.Map[String, Boolean] = mutable.Map.empty[String, Boolean]
  var remainingTime: FiniteDuration = 0 seconds
  var dt: Long = 0
  def goToClinicalApp: ChainBuilder = exec(
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

  def selectNextPatientFromOpdQueue: ChainBuilder = exec { session =>
    val waitingOpdPatients = inMemoryOpdPatientsQueue.filter(patient => !patient._2)
    val nextPatientUuid: String = waitingOpdPatients.keys.toList(Random.nextInt(waitingOpdPatients.size))
    inMemoryOpdPatientsQueue += (nextPatientUuid -> true)
    session.set("opdPatientId", nextPatientUuid)
  }

  def refreshInMemoryOpdPatientQueue: ChainBuilder =
    doIf(shouldRefreshInMemoryPatientQueue) {
      exec(
        getActiveOpdPatients(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.activePatients")
          .check(
            jsonPath("$..uuid").findAll.saveAs("patientUUIDs")
          )
          .resources(
            getActiveOpdPatients(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.activePatientsByProvider"),
            getActiveOpdPatients(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.activePatientsByLocation")
          )
      ).exec(getPatientAvatars)
        .exec { session =>
          session("patientUUIDs")
            .as[Vector[String]]
            .foreach(uuid => {
              if (!inMemoryOpdPatientsQueue.contains(uuid))
                inMemoryOpdPatientsQueue += (uuid -> false)
            })
          session
        }
    }

  private def shouldRefreshInMemoryPatientQueue = {
    inMemoryOpdPatientsQueue.values.toList.reduceOption(_ && _).getOrElse(true)
  }

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
      getPatientAvatar("#{runTimeUuid}"),
      getEncoutnerByEncounterTypeUuid("#{runTimeUuid}"),
      getPatientContext(patientUuid, "ABHA Address", "phoneNumber"),
      getPatientsInfoWithSqlInpatientInfoTabOfClinic(patientUuid, "bahmni.sqlGet.upComingAppointments"),
      getPatientsInfoWithSqlInpatientInfoTabOfClinic(patientUuid, "bahmni.sqlGet.pastAppointments"),
      getRelationship("#{runTimeUuid}"),
      getDrugOrderConfig,
      getDrugOrdersForPatient(patientUuid),
      //getLabOrderResults("#{runTimeUuid}"),
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
      getactiveDrugOrder(patientUuid).check(
        jmesPath("[-1].visit.uuid").ofType[Any].not(None).saveAs("opdVisitId"),
        jmesPath("[?drugOrder.drug.uuid=='1bdc7e85-aa09-4d66-a644-9f3489173a4d'].uuid | [0]").optional.saveAs(
          "reglanDrugOrderUuid"
        ),
        jmesPath("[?drugOrder.drug.uuid=='468908b5-c141-4287-b116-761c0bfea007'].uuid | [0]").optional.saveAs(
          "lopeDrugOrderUuid"
        ),
        jmesPath("[?drugOrder.drug.uuid=='99f32f18-a5bc-44a7-aa7d-4c7ff63d7a14'].uuid | [0]").optional.saveAs(
          "promDrugOrderUuid"
        )
      ),
      getAllObservationTemplates,
      getObs(patientUuid, "visitFormDetails"),
      getPatientFormTypes(patientUuid),
      getLatestPublishedForms,
      getGlobalProperty("drugOrder.drugOther")
    )
  ).exec(
    getVisits(patientUuid).checkIf("#{opdVisitId.exists()}") {
      jsonPath("$..results[?(@.uuid==\"#{opdVisitId}\")].encounters[0].uuid")
        .ofType[Any]
        .not(None)
        .saveAs("encounterUuid")
    }
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
        jsonPath("$..location.uuid").find.saveAs("locationUuid")
      )
      .checkIf("#{opdVisitId.isUndefined()}") { jsonPath("$.results[0].uuid").find.saveAs("opdVisitId") }
  ).exec(
    getVisitLocation("#{locationUuid}").resources(getSummaryByVisitUuid("#{opdVisitId}"), getVisit("#{opdVisitId}"))
  )
  def goToObservations(pUuid: String): ChainBuilder = exec(
    getConcept(
      "All Observation Templates",
      "custom:(uuid,name:(name,display),names:(uuid,conceptNameType,name),setMembers:(uuid,name:(name,display),names:(uuid,conceptNameType,name),setMembers:(uuid,name:(name,display),names:(uuid,conceptNameType,name))))"
    ).resources(
      postAuditLog(pUuid),
      getLatestPublishedForms
        .check(
          jsonPath("$[?(@.name==\"History and Examination\")].uuid").find.saveAs("form_uuid"),
          jsonPath("$[?(@.name==\"History and Examination\")].name").find.saveAs("form_name")
        )
    )
  )

  def saveEncounter: ChainBuilder = exec(
    getForm("#{form_uuid}").resources(
      getFormTranslations("#{form_name}", "#{form_uuid}"),
      getEntityMappingByLocationEncounter(LOGIN_LOCATION_UUID),
      getEncounterTypeConsultation.check(jsonPath("$..uuid").find.saveAs("encounterTypeUuid"))
    )
  ).doIfOrElse("#{encounterUuid.exists()}") { exec(postEncounter("bodies/encounter_revise_drugorder.json")) } {
    exec(postEncounter("bodies/encounter.json"))
  }
  def setOrders(): ChainBuilder = exec { session =>
    observations = "[]"
    val path = System.getProperty("user.dir") + "/src/gatling/resources"
    orders = scala.io.Source.fromFile(path + "/bodies/orders.json").mkString
    session
  }

  def goToMedications(patientUuid: String): ChainBuilder = exec(
    getConfigDrugOrder().resources(
      getGlobalProperty("drugOrder.drugOther"),
      postAuditLog(patientUuid),
      getDrugOrders(patientUuid)
    )
  )

  def addDrug(name: String): ChainBuilder = exec(
    findDrug(name).resources(
      getEntityMappingByLocationEncounter(LOGIN_LOCATION_UUID),
      getEncounterTypeConsultation
    )
  )

  def setMedication(): ChainBuilder = exec { session =>
    orders = "[]"
    val path = System.getProperty("user.dir") + "/src/gatling/resources"
    drugOrders = scala.io.Source.fromFile(path + "/bodies/medications.json").mkString
    session
  }

  def setObservation(): ChainBuilder = exec {
    exec { session =>
      val path = System.getProperty("user.dir") + "/src/gatling/resources"
      observations = scala.io.Source.fromFile(path + "/bodies/observations.json").mkString
      session
    }
  }

  def setStartTime(): ChainBuilder = {
    exec(session => {
      session.set("startTime", System.currentTimeMillis())
    })
  }
  def pauseRemainingTime(value: Long): ChainBuilder = {
    exec { session =>
      dt = (System.currentTimeMillis() - session("startTime").as[Long]) / 1000
      if (dt < value) {
        remainingTime = (value - dt) seconds
      } else {
        remainingTime = 0 seconds
      }
      session.set("rt", remainingTime)
    }
      .exec(pause("#{rt}"))

  }

}
