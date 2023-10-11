package simulations


import api.Constants._
import configurations.{TrafficLoad, UserFlow}
import io.gatling.core.Predef._
import scenarios.{Consultation, Registration}

import scala.concurrent.duration._
import scala.language.postfixOps

//This name will be used as title for report - hence prefixing it with Bahmni
class BahmniClinic extends Simulation {
  setUp(
      Registration.scenario(UserFlow.Registration.trafficLoadShare)++
     Consultation.scenario(UserFlow.Consultation.trafficLoadShare)
  ).assertions(
    forAll.responseTime.percentile(PERCENTILE1).lt(PERCENTILE1_EXPECTED_RESPONSE_TIME),
    forAll.responseTime.percentile(PERCENTILE2).lt(PERCENTILE2_EXPECTED_RESPONSE_TIME),
    forAll.responseTime.max.lt(MAX_EXPECTED_RESPONSE_TIME)
  ).maxDuration(TrafficLoad.getTrafficShareConfiguration(OVERALL_LOAD_SHARE).totalDuration + ADDITIONAL_HARD_STOP_TIME)
}

