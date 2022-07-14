package org.bahmni.gatling.scenarios

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import org.bahmni.gatling.Configuration
import org.bahmni.gatling.Configuration.Constants._
import org.bahmni.gatling.HttpRequests._

object AtomfeedScenarios {

  val headers = Map(
    "Cache-Control" -> "no-cache",
    "Disable-WWW-Authenticate" -> "true",
    "Pragma" -> "no-cache",
    "Connection" -> "keep-alive")

  val labFeed: ScenarioBuilder = scenario("lab recent scenario")
    .during(Configuration.Load.DURATION) {
      exec(getLabFeedRecentPage.headers(headers))
        .pause(15)
    }

  val patientFeed: ScenarioBuilder = scenario("patient recent scenario")
    .during(Configuration.Load.DURATION) {
      exec(getPatienFeedRecentPage.headers(headers))
        .pause(15)
    }

  val encounterFeed: ScenarioBuilder = scenario("encounter recent scenario")
    .during(Configuration.Load.DURATION) {
      exec(getEncounterFeedRecentPage.headers(headers))
        .pause(15)
    }

  val encounterFeedContent: ScenarioBuilder = scenario("encounter feed content scenario")
    .during(Configuration.Load.DURATION) {
      exec(getEncounterFeedcontent(ATOMFEED_ENCOUNTER_UUID).headers(headers))
        .pause(15)
    }

  val patientFeedContent: ScenarioBuilder = scenario("patient feed content scenario")
    .during(Configuration.Load.DURATION) {
      exec(getPatientFeedcontent(PATIENT_UUID).headers(headers))
        .pause(15)
    }

  val labFeedContent: ScenarioBuilder = scenario("lab feed content scenario")
    .during(Configuration.Load.DURATION) {
      exec(getLabFeedcontent(ALL_TESTS_AND_PANELS).headers(headers))
        .pause(15)
    }
}
