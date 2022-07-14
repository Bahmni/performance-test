package org.bahmni.gatling.simulations

import io.gatling.core.Predef._
import org.bahmni.gatling.Configuration.HttpConf._
import org.bahmni.gatling.Configuration.Load
import org.bahmni.gatling.scenarios._

import scala.concurrent.duration.DurationInt

class UserSimulation extends Simulation {

  setUp(
      PatientCreateAndStartVisitFlow.scn.inject
      (
        nothingFor(10 seconds),
        rampUsers(1).during(45 seconds),
    ).protocols(HTTPS_PROTOCOL_FrontDesk),

    ClinicalFlow_Add_Observation.scn.inject
    (
      nothingFor(60 seconds),
      rampUsers(1).during(1 minutes)
   ).protocols(HTTPS_PROTOCOL_Doctor),

    Registration_Exact_Search_Flow.scn.inject(
      rampUsers(1) during (30 seconds)
    ).protocols(HTTPS_PROTOCOL),

    Registration_Name_Search_Flow.scn.inject(
      nothingFor(45 seconds),
      rampUsers(2) during (60 seconds)
    ).protocols(HTTPS_PROTOCOL),

    PatientImage.scn.inject(
      incrementUsersPerSec(1300) .times(5) .eachLevelLasting(5 seconds)
      .startingFrom(20).separatedByRampsLasting(5 seconds)
    ).protocols(HTTP_PROTOCOL),
    AdtFlow.scn.inject(rampUsers(2) during (20 seconds)).protocols(HTTPS_PROTOCOL),
    AtomfeedScenarios.patientFeed.inject(Load.ATOMFEED_USER_PROFILE).protocols(HTTP_PROTOCOL),
    AtomfeedScenarios.patientFeedContent.inject(Load.ATOMFEED_USER_PROFILE).protocols(HTTP_PROTOCOL),
    AtomfeedScenarios.encounterFeed.inject(Load.ATOMFEED_USER_PROFILE).protocols(HTTP_PROTOCOL),
    AtomfeedScenarios.encounterFeedContent.inject(Load.ATOMFEED_USER_PROFILE).protocols(HTTP_PROTOCOL),
    AtomfeedScenarios.labFeed.inject(Load.ATOMFEED_USER_PROFILE).protocols(HTTP_PROTOCOL),
    AtomfeedScenarios.labFeedContent.inject(Load.ATOMFEED_USER_PROFILE).protocols(HTTP_PROTOCOL))

    .assertions(
      global.successfulRequests.percent.gte(90),
      details("Search Patient by Identifier").successfulRequests.percent.gte(50),
      //details("Search Patient by Identifier").responseTime.percentile3.lte(42000),
      //details("Search Patient by Name").responseTime.percentile3.lte(37000),
      details("Search Patient by Name").successfulRequests.percent.gte(95),
      details("getVisits").responseTime.percentile3.lte(6000),
      details("get lab order results").responseTime.percentile3.lte(17000),
      details("get diagnoses for patient").responseTime.percentile3.lte(9000),
      details("create patient").responseTime.percentile3.lte(22000),
      details("get obs").responseTime.percentile3.lte(5000),
      details("capture encounter").responseTime.percentile3.lte(8000)
    ).maxDuration(2 minutes)
}