package registries

import api.Constants.{IMAGES_ENCOUNTER_UUID, LOGIN_LOCATION_UUID, LOGIN_USER, PROVIDER_UUID}
import api.DoctorHttpRequests._
import api.FrontdeskHttpRequests._
import api.HttpRequests._
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

object FrontDesk {

  def goToRegistrationSearchPage: ChainBuilder = exec(
    getVisitLocation(LOGIN_LOCATION_UUID)
      .resources(
        getUser(LOGIN_USER).check(
          jsonPath("$..results[0].uuid").find.saveAs("runTimeUuid")
        ),
        getSession,
        getProviderForUser("#{runTimeUuid}"),
        getGlobalProperty("mrs.genders"),
        getGlobalProperty("bahmni.relationshipTypeMap"),
        getAddressHierarchyLevel,
        getIdentifierTypes,
        getRelationshipTypes,
        getEntityMapping("loginlocation_visittype"),
        getPersonAttributeTypes,
        getRegistrationConcepts.check(jsonPath("$.visitTypes.OPD").find.exists.saveAs("visit_type_id")),
        getByVisitLocation(LOGIN_LOCATION_UUID),
        getGlobalProperty("bahmni.enableAuditLog"),
        postAuditLog
      )
  )

  def performNameSearch(patientName: String): ChainBuilder = {
    exec(
      searchPatientUsingName(LOGIN_LOCATION_UUID, patientName)
        .check(jsonPath("$..uuid").find.saveAs("pt_uuID"))
    ).exec(getPatientProfileAfterRegistration("#{pt_uuID}"))
  }

  def performIdSearch(patientIdentifier: String): ChainBuilder = {
    exec(
      searchPatientUsingIdentifier(LOGIN_LOCATION_UUID, patientIdentifier)
        .check(jsonPath("$..uuid").find.saveAs("p_uuID"))
    )
      .exec(getPatientProfileAfterRegistration("#{p_uuID}"))
  }

  def startVisitForID: ChainBuilder = {
    exec(
      startVisitRequest("#{p_uuID}", "#{visit_type_id}", LOGIN_LOCATION_UUID)
    )
  }

  def startVisitForName: ChainBuilder = {
    exec(
      startVisitRequest("#{pt_uuID}", "#{visit_type_id}", LOGIN_LOCATION_UUID)
    )
  }

  def startVisitForCreatePatient: ChainBuilder = {
    exec(
      startVisitRequest("#{patient_uuid}", "#{visit_type_id}", LOGIN_LOCATION_UUID)
    )
  }

  def gotoCreatePatientPage: ChainBuilder = exec(
    exec(getVisitLocation(LOGIN_LOCATION_UUID))
      .exec(getSession)
      .exec(
        getUser(LOGIN_USER)
          .check(
            jsonPath("$..results[0].uuid").find.saveAs("runTimeUuid")
          )
      )
      .exec(getProviderForUser("#{runTimeUuid}"))
      .exec(getRegistrationConcepts.check(jsonPath("$.visitTypes.OPD").find.exists.saveAs("visit_type_id")))
      .exec(
        getPersonaAttributeType
          .resources(
            getIdentifierTypes.check(
              jsonPath("$[?(@.name==\"Patient Identifier\")].uuid").find.saveAs("identifier_type"),
              jsonPath("$[?(@.name==\"Patient Identifier\")].identifierSources..uuid").find.saveAs(
                "identifier_sources_id"
              )
            ),
            getGlobalProperty("mrs.genders"),
            getRelationshipTypes,
            getGlobalProperty("bahmni.relationshipTypeMap"),
            getEntityMapping("loginlocation_visittype")
          )
      )
  )
    .exec(getByVisitLocation(LOGIN_LOCATION_UUID))
    .exec(getGlobalProperty("bahmni.enableAuditLog"))
    .exec(postAuditLog)

  def createPatient: ChainBuilder = {
    exec(
      createPatientRequest(ElFileBody("patient_profile.json"))
        .check(
          jsonPath("$.patient.uuid").saveAs("patient_uuid")
        )
        .resources(
          findEncounter("#{patient_uuid}"),
          activateVisit("#{patient_uuid}"),
          getNutrition,
          getObservation(Seq("Height", "Weight"), Map("patientUuid" -> "#{patient_uuid}")),
          getVital,
          getFeeInformation,
          getPatientProfileAfterRegistration("#{patient_uuid}")
        )
    )
  }

  def getActivePatients: ChainBuilder = {
    exec(
      getActiveOpdPatients(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.activePatients")
        .check(
          jsonPath("$..uuid").findAll.saveAs("patientUUIDs")
        )
        .resources(
          getUser(LOGIN_USER)
            .check(
              jsonPath("$..results[0].uuid").find.saveAs("runTimeUuid")
            ),
          getSession,
          getRegistrationConcepts,
          getGlobalProperty("mrs.genders"),
          getIdentifierTypes
        )
    )
      .exec(getProviderForUser("#{runTimeUuid}"))
  }

  def getPatientAvatars: ChainBuilder = {
    val maxVisiblePatientTiles = 54
    exec(session => {
      val opdPatients = session("patientUUIDs").as[Vector[String]]
      val size = if (opdPatients.size > maxVisiblePatientTiles) maxVisiblePatientTiles else opdPatients.size
      session.set("opdPatients", opdPatients.slice(0, size))
    }).foreach("#{opdPatients}", "patientUuid") {
      exec(getPatientAvatar("#{patientUuid}"))
    }
  }

  def goToPatientDocumentUpload = {
    exec(
      getVisitByPatient("#{pt_uuID}")
        .check(
          jsonPath("$..results[0].uuid").find.saveAs("visitUUID"),
          jsonPath("$..results[0].visitType.uuid").find.saveAs("visitTypeUUID")
        )
        .resources(
          getVisitType,
          findEncounter("#{pt_uuID}", PROVIDER_UUID, IMAGES_ENCOUNTER_UUID),
          getPatientDocumentConcept,
          getPatientFull("#{pt_uuID}"),
          getEncounterByEncounterTypeUuid("#{pt_uuID}", IMAGES_ENCOUNTER_UUID)
        )
    )
  }

  def uploadPatientDocument = {
    exec(postUploadDocument("#{pt_uuID}"))
  }

  def verifyPatientDocument = {
    exec(
      postVisitDocument
        .resources(
          getGlobalProperty("bahmni.enableAuditLog"),
          postAuditLog("#{pt_uuID}"),
          getEncounterByEncounterTypeUuid("#{pt_uuID}", IMAGES_ENCOUNTER_UUID),
          findEncounter("#{pt_uuID}", PROVIDER_UUID, IMAGES_ENCOUNTER_UUID)
        )
    )
  }
  def filterActivePatients(): ChainBuilder = {
    exec(
      getActiveOpdPatients(LOGIN_LOCATION_UUID, PROVIDER_UUID, "emrapi.sqlSearch.activePatients")
        .check(
          jsonPath("$..name").findAll.optional.saveAs("activepatientnames"),
          jsonPath("$..identifier").findAll.optional.saveAs("activepatientids")
        )
    ).doIf("#{activepatientnames.exists()}") {
      exec(session => {
        val id = session("Registration Number").as[String]
        val name = session("First Name").as[String] + " " + session("Last Name").as[String]
        if (
          session("activepatientids")
            .as[Vector[String]]
            .contains(id) || session("activepatientnames").as[Vector[String]].contains(name)
        ) {
          session
        } else {
          session.set("name", session("Registration Number").as[String])
        }
      })
    }

  }
}
