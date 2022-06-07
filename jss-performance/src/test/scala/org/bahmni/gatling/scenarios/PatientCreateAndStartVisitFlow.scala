package org.bahmni.gatling.scenarios


import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import org.bahmni.gatling.Configuration
import org.bahmni.gatling.HttpRequests._
import io.gatling.http.Predef._
import org.bahmni.gatling.Configuration.Constants.{LOGIN_LOCATION_UUID, LOGIN_USER, VISIT_TYPE_ID}

import scala.util.Random

/**
  * Created by swarup on 12/20/16.
  */
object PatientCreateAndStartVisitFlow {

  val gotoCreatePatientPage : ChainBuilder = exec(
    getUser(LOGIN_USER)
      .check(
        jsonPath("$..results[0].uuid").find.saveAs("runTimeUuid")
      )
      .resources(
        getProviderForUser("${runTimeUuid}"),
        getSession,
        postAuditLog,
        getGlobalProperty("concept.reasonForDeath"),
        getReasonForDeath
        )
  )

  val rnd = new Random()

  def randomString(length: Int) = {
    rnd.alphanumeric.filter(_.isLetter).take(length).mkString
  }

  val jsonFeeder = Iterator.continually(
    Map(
      "givenName" -> randomString(5),
      "streetAddress1" -> randomString(8)
    )
  )

  val createPatient : ChainBuilder = {
        exec(
          createPatientRequest(ElFileBody("patient_profile.json"))
            .check(
              jsonPath("$.patient.uuid").saveAs("patient_uuid"),
              status.is(200)
            ).resources(
            getPatientProfileAfterRegistration("${patient_uuid}"),

            startVisitRequest("${patient_uuid}", VISIT_TYPE_ID, LOGIN_LOCATION_UUID).check(
              status.is(201)
            )
        )
      )
    }

  val scn : ScenarioBuilder = scenario("create Patient, start visit and capture Encounter")
    .during(Configuration.Load.DURATION){
        exec(gotoCreatePatientPage)
      .feed(jsonFeeder)
        .exec(createPatient)
	  .pause(28)
    }
}
