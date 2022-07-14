package org.bahmni.gatling.scenarios

import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import org.bahmni.gatling.Configuration
import org.bahmni.gatling.HttpRequests._

object PatientImage {

  private def fetchPatientImage: ChainBuilder = {
    exec(getPatientImage)
  }

  val scn: ScenarioBuilder = scenario("fetch patient image")
    .during(Configuration.Load.DURATION) {
      exec(fetchPatientImage)
        .pause(100)
    }
}
