package simulations

import configurations.Load
import io.gatling.core.Predef._
import scenarios.{Consultation, Registration}
import api.Constants._

import scala.concurrent.duration.{Duration, FiniteDuration, SECONDS}

//This name will be used as title for report - hence prefixing it with Bahmni
class BahmniClinic extends Simulation {

  setUp(
    //Registration.scenario(50),
    Consultation.scenario(CONSULTATION_LOAD_SHARE)
  ).maxDuration(Load.getLoadParameters.duration)
  //Duration(Load.getLoadParameters.duration.plus(Load.getLoadParameters.duration.mul(0.1)).toSeconds,SECONDS)
  //.maxDuration(Some(Load.getLoadParameters.duration.plus(Load.getLoadParameters.duration.mul(0.1))).collect{case d: FiniteDuration => d}.get)

}
