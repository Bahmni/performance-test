package registries

import api.Constants.{LOGIN_LOCATION_UUID, LOGIN_USER}
import api.FrontdeskHttpRequests._
import api.HttpRequests._
import configurations.Feeders.{identifierSourceId, identifierType}
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

import scala.util.Random

object Frontdesk {

  val goToRegistrationSearchPage: ChainBuilder = exec(
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
        getRegistrationConcepts.check(jsonPath("$.visitTypes.OPD").find.saveAs("visit_type_id")),
        getByVisitLocation(LOGIN_LOCATION_UUID),
        getGlobalProperty("bahmni.enableAuditLog"),
        postAuditLog
      )
  )

  def performNameSearch(patientName: String): ChainBuilder = {
    exec(
      searchPatientUsingName(LOGIN_LOCATION_UUID, patientName)
        .check(jsonPath("$..uuid").findAll.transform(Random.shuffle(_).head).optional.saveAs("pt_uuID"))
        .resources(
          getPatientProfileAfterRegistration("#{pt_uuID}")
        )
    )
  }

  def performIdSearch(patientIdentifier: String): ChainBuilder = {
    exec(
      searchPatientUsingIdentifier(LOGIN_LOCATION_UUID, patientIdentifier)
        .check(jsonPath("$..uuid").findAll.transform(Random.shuffle(_).head).optional.saveAs("p_uuID"))
        .resources(
          getPatientProfileAfterRegistration("#{p_uuID}")
        )
    )
  }

  val startVisitForID: ChainBuilder = {
    exec(
      startVisitRequest("#{p_uuID}", "#{visit_type_id}", LOGIN_LOCATION_UUID)
    )
  }

  val startVisitForName: ChainBuilder = {
    exec(
      startVisitRequest("#{pt_uuID}", "#{visit_type_id}", LOGIN_LOCATION_UUID)
    )
  }

  val startVisitForCreatePatient: ChainBuilder = {
    exec(
      startVisitRequest("#{patient_uuid}", "#{visit_type_id}", LOGIN_LOCATION_UUID)

    )
  }

  val gotoCreatePatientPage: ChainBuilder = exec(
    getUser(LOGIN_USER)
      .check(
        jsonPath("$..results[0].uuid").find.saveAs("runTimeUuid")
      )
      .resources(
        getLoginLocations,
        getProviderForUser("#{runTimeUuid}"),
        postUserInfo("#{runTimeUuid}"),
        getSession,
        getVisitLocation(LOGIN_LOCATION_UUID),
        getRegistrationConcepts,
        getPersonaAttributeType,
        getIdentifierTypes.check(
          jsonPath("$[?(@.name==\"Patient Identifier\")].uuid").find.saveAs("identifier_type"),
          jsonPath("$[?(@.name==\"Patient Identifier\")].identifierSources..uuid").find.saveAs("identifier_sources_id")
        ),
        getAddressHierarchyLevel,
        getGlobalProperty("mrs.genders"),
        getRelationshipTypes,
        getGlobalProperty("bahmni.relationshipTypeMap"),
        getEntityMapping("loginlocation_visittype"),
        getGlobalProperty("bahmni.enableAuditLog"),
        postAuditLog,
        getGlobalProperty("concept.reasonForDeath")
      )
  ).exec { session =>
    identifierType = session("identifier_type").as[String]
    identifierSourceId = session("identifier_sources_id").as[String]
    session
  }

  var createPatient: ChainBuilder = {
    exec(
      createPatientRequest(ElFileBody("patient_profile.json"))
        .check(
          jsonPath("$.patient.uuid").saveAs("patient_uuid")
        )
        .resources(
          findEncounter("#{patient_uuid}"),
          activateVisit("#{patient_uuid}"),
          getNutrition,
          getObservation(Seq("Height","Weight"),Map("patientUuid"->"#{patient_uuid}")),
          getVital,
          getFeeInformation,
          getPatientProfileAfterRegistration("#{patient_uuid}")
        )
    )
  }

}
