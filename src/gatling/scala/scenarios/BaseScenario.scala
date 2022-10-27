package scenarios

import configurations._
import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, PopulationBuilder}
import registries.Common.waitBeforeNextWorkLoad
import registries.Doctor.setStartTime

import scala.concurrent.duration.{Duration, DurationInt, SECONDS}
import scala.language.postfixOps

object BaseScenario {
  def setupScenario(
                     scn: Scenario,
                     trafficSharePercentage: Int
                   ): PopulationBuilder = {
    val trafficShareConfiguration = TrafficLoad.getTrafficShareConfiguration(trafficSharePercentage)
    val workLoad = createWorkLoad(trafficShareConfiguration, scn.config)
    val rampUpDuration = Duration(trafficShareConfiguration.totalDuration.mul(0.1).toSeconds, SECONDS).min(5 minutes)
    val constantConcurrentDuration = trafficShareConfiguration.totalDuration - rampUpDuration.plus(rampUpDuration)
    scenario(scn.config.name)
      .exec(scn.userFlow(workLoad))
      .inject(
        rampConcurrentUsers(0).to(workLoad.activeUsersCount).during(rampUpDuration),
        constantConcurrentUsers(workLoad.activeUsersCount).during(constantConcurrentDuration)
      )
      .protocols(Protocols.default)
  }

  def handleWorkLoad(userFlow: ChainBuilder, workLoad: ScenarioWorkLoad): ChainBuilder = {
    repeat(workLoad.patientsLoadPerUser)(
      exec(setStartTime())
        .exec(userFlow)
        .exec(waitBeforeNextWorkLoad(workLoad.perPatientPace))
    )
  }

  private def createWorkLoad(
                              trafficShareConfiguration: TrafficShareConfiguration,
                              scenarioConfig: ScenarioConfig
                            ): ScenarioWorkLoad = {
    val activeUsersCount = (trafficShareConfiguration.activeUsers * (scenarioConfig.loadShare / 100.00)).toInt
    val patientsLoadPerUser = (trafficShareConfiguration.totalDuration / scenarioConfig.pace).toInt
    val totalPatients = patientsLoadPerUser * activeUsersCount

    ScenarioWorkLoad(activeUsersCount, scenarioConfig.pace, patientsLoadPerUser, totalPatients)
  }
}
