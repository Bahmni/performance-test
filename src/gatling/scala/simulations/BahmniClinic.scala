package simulations

import configurations.UserFlow
import io.gatling.core.Predef._
import scenarios.{Consultation, Registration}

//This name will be used as title for report - hence prefixing it with Bahmni
class BahmniClinic extends Simulation {

  setUp(
    Registration.scenario(UserFlow.Registration.trafficLoadShare) ++
      Consultation.scenario(UserFlow.Consultation.trafficLoadShare)
  )

}
