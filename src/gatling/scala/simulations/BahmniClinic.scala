package simulations

import io.gatling.core.Predef._
import scenarios.{Consultation, Registration}

//This name will be used as title for report - hence prefixing it with Bahmni
class BahmniClinic extends Simulation {
  setUp(
//    Registration.scenario(100),
    Consultation.scenario(100)
  )
}
