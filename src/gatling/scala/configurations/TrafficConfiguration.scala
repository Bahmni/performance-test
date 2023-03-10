package configurations

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.math.max

object UserFlow {
  object Registration {
    val trafficLoadShare: Int = 50
    val newPatientOpdVisit: ScenarioConfig =
      ScenarioConfig("Registration - New Patient - OPD Visit", 40, 2 minutes)
    val existingPatientIdSearchOpdVisit: ScenarioConfig =
      ScenarioConfig("Registration - Existing Patient ID Search - OPD Visit", 30, 2 minutes)
    val existingPatientNameSearchOpdVisit: ScenarioConfig =
      ScenarioConfig("Registration - Existing Patient Name Search - OPD Visit", 20, 2 minutes)
    val patientDocumentUpload: ScenarioConfig =
      ScenarioConfig("Registration - Patient document upload", 10, 2 minutes)
  }
  object Consultation {
    val trafficLoadShare: Int = 100
    val averagePatient: ScenarioConfig = ScenarioConfig("Consultation - Average Patient", 100, 5 minutes)
  }
}

object TrafficLoad {
  val standard: TrafficConfiguration = TrafficConfiguration(
    activeUsers = 40,
    duration = 30 minutes
  )

  val high: TrafficConfiguration = TrafficConfiguration(
    activeUsers = 70,
    duration = 30 minutes
  )

  val peak: TrafficConfiguration = TrafficConfiguration(
    activeUsers = 90,
    duration = 30 minutes
  )

  val dev: TrafficConfiguration = TrafficConfiguration(
    activeUsers = System.getenv("ACTIVE_USERS").toInt,
    duration = FiniteDuration.apply(System.getenv("DURATION").toInt,System.getenv("UNITS"))
  )

  def getTrafficShareConfiguration(loadSharePercentage: Int): TrafficShareConfiguration = {
    System.out.println(s"Load Simulation Type is : ${System.getenv("LOAD_SIMULATION_TYPE") toLowerCase}")
    System.getenv("LOAD_SIMULATION_TYPE") toLowerCase match {
      case "dev" =>
        new TrafficShareConfiguration(dev, loadSharePercentage)
      case "high" =>
        new TrafficShareConfiguration(high, loadSharePercentage)
      case "peak" =>
        new TrafficShareConfiguration(peak, loadSharePercentage)
      case "standard" | _ =>
        new TrafficShareConfiguration(standard, loadSharePercentage)
    }
  }
}

case class TrafficConfiguration(
    activeUsers: Int,
    duration: FiniteDuration
)

class TrafficShareConfiguration(
    trafficConfiguration: TrafficConfiguration,
    loadSharePercentage: Int
) {
  private val shareFactor: Int = roundUp(100 / loadSharePercentage)
  val activeUsers: Int = atLeast10(roundUp(trafficConfiguration.activeUsers / shareFactor))
  val totalDuration: FiniteDuration = atLeast1Minute(trafficConfiguration.duration)

  private def roundUp(d: Double): Int = math.ceil(d).toInt

  private def atLeast10(value: Int) = max(1, value)
  private def atLeast1Minute(value: FiniteDuration) = value.max(1 minutes)
}
