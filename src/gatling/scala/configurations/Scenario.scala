package configurations

import io.gatling.core.structure.ChainBuilder

import scala.concurrent.duration.FiniteDuration
import scala.language.implicitConversions

case class Scenario(
    userFlow: ScenarioWorkLoad => ChainBuilder,
    config: ScenarioConfig
)

//case class Scenarios(scenarios: List[Scenario])
//object Scenarios {
//  implicit def fromList(list: List[Scenario]): Scenarios =
//    Scenarios(list)
//}

case class ScenarioConfig(name: String, loadShare: Int, pace: FiniteDuration)

case class ScenarioWorkLoad(
    activeUsersCount: Int,
    perPatientPace: FiniteDuration,
    patientsLoadPerUser: Int,
    totalPatient: Int
)
