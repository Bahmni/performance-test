package registries

import api.Constants._
import api.DoctorHttpRequests._
import api.HttpRequests._
import configurations.Feeders._
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import registries.FrontDesk._


import scala.collection.mutable
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Random

object Doctor {

  val inMemoryOpdPatientsQueue: mutable.Map[String, Boolean] = mutable.Map.empty[String, Boolean]
  var inMemoryOpdPatientsIds: Map[String, String] = Map.empty[String, String]
  var nextPatientUuid: String=""
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
    nextPatientUuid = waitingOpdPatients.keys.toList(Random.nextInt(waitingOpdPatients.size))
    inMemoryOpdPatientsQueue += (nextPatientUuid -> true)
    session.set("opdPatientId", nextPatientUuid)
  }.exec{session=>
    session.set("patientId",inMemoryOpdPatientsIds.get(nextPatientUuid).getOrElse("ET"))
  }

  def refreshInMemoryOpdPatientQueue: ChainBuilder =
    doIf(shouldRefreshInMemoryPatientQueue) {
      exec(
        getActiveOpdPatients(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.activePatients")
          .check(
            jsonPath("$..uuid").findAll.saveAs("patientUUIDs"),
            jmesPath("[*].\"Patient ID\"").ofType[Any].not(None).saveAs("ids")
          )
          .resources(
            getActiveOpdPatients(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.activeMyPatientsByAppointmentProvider"),
            getActiveOpdPatientsBySpeciality(LOGIN_LOCATION_UUID, PROVIDER_UUID, "Orthopaedics"),
            getActiveOpdPatientsBySpeciality(LOGIN_LOCATION_UUID, PROVIDER_UUID, "Plastics"),
            getActiveOpdPatientsBySpeciality(LOGIN_LOCATION_UUID, PROVIDER_UUID, "Paediatrics"),
            getActiveOpdPatientsBySpeciality(LOGIN_LOCATION_UUID, PROVIDER_UUID, "General+Surgery"),
            getActiveOpdPatientsBySpeciality(LOGIN_LOCATION_UUID, PROVIDER_UUID, "Anesthesia"),
            getActiveOpdPatientsBySpeciality(LOGIN_LOCATION_UUID, PROVIDER_UUID, "Radiology"),
            getActiveOpdPatientsBySpeciality(LOGIN_LOCATION_UUID, PROVIDER_UUID, "Diagnostic+Testing"),
            getActiveOpdPatientsBySpeciality(LOGIN_LOCATION_UUID, PROVIDER_UUID, "Physiotherapy"),
            getActiveOpdPatientsBySpeciality(LOGIN_LOCATION_UUID, PROVIDER_UUID, "Occupational+Therapy"),
            getActiveOpdPatientsBySpeciality(LOGIN_LOCATION_UUID, PROVIDER_UUID, "Speech+Therapy"),
            getActiveOpdPatientsBySpeciality(LOGIN_LOCATION_UUID, PROVIDER_UUID, "Prosthetics+and+Orthotics"),
            getActiveOpdPatientsBySpeciality(LOGIN_LOCATION_UUID, PROVIDER_UUID, "Gait+Lab"),
            getActiveOpdPatientsBySpeciality(LOGIN_LOCATION_UUID, PROVIDER_UUID, "Nutrition"),
            getActiveOpdPatientsBySpeciality(LOGIN_LOCATION_UUID, PROVIDER_UUID, "Epilepsy"),
            getActiveOpdPatientsBySpeciality(LOGIN_LOCATION_UUID, PROVIDER_UUID, "Other+Speciality"),
            getActiveOpdPatientsBySpeciality(LOGIN_LOCATION_UUID, PROVIDER_UUID, "Neuro"),
            getActiveOpdPatientsBySpeciality(LOGIN_LOCATION_UUID, PROVIDER_UUID, "ENT")
          )
      )
        .exec { session =>
          inMemoryOpdPatientsIds=(session("patientUUIDs").as[Vector[String]] zip session("ids").as[Vector[String]]).toMap
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
      getEncoutnerByEncounterTypeUuid("#{runTimeUuid}",ENCOUNTER_TYPE_UUID),
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
        jmesPath("[?drugOrder.drug.uuid=='"+REGLAN_DRUG+"'].uuid | [0]").optional.saveAs(
          "reglanDrugOrderUuid"
        ),
        jmesPath("[?drugOrder.drug.uuid=='"+LOPERAMIDE_DRUG+"'].uuid | [0]").optional.saveAs(
          "lopeDrugOrderUuid"
        ),
        jmesPath("[?drugOrder.drug.uuid=='"+PROMETHAZINE_DRUG+"'].uuid | [0]").optional.saveAs(
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
          jsonPath("$[?(@.name==\"Orthopaedic Triage\")].uuid").find.saveAs("form_uuid"),
          jsonPath("$[?(@.name==\"Orthopaedic Triage\")].name").find.saveAs("form_name")
        )
    )
  )

  def fillForms(pUuid:String):ChainBuilder=exec(

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
def openTheForm():ChainBuilder={
  exec(
      getForm("#{form_uuid}").resources(
      getAllForms(),
      getFormTranslations("#{form_name}", "#{form_uuid}")
    )
  )
}
}
