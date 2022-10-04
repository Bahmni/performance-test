package simulations

import configurations.Load
import io.gatling.core.Predef._
import scenarios.{Consultation, Registration}
import api.Constants._

//This name will be used as title for report - hence prefixing it with Bahmni
class BahmniClinic extends Simulation {

  setUp(
    Registration.scenario(REGISTRATION_LOAD_SHARE),
    Consultation.scenario(CONSULTATION_LOAD_SHARE)
  ).maxDuration(Load.getLoadParameters.duration)

}
