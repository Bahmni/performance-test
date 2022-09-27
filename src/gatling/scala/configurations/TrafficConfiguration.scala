package configurations



import scala.concurrent.duration._
import scala.language.postfixOps
import scala.math.max
import api.Constants._

object Load {
  val standard: TrafficConfiguration = TrafficConfiguration(
    activeUsers = 40,
    duration = 30 minutes,
    patients=220,
    responseTimes = MaximumResponseTimes(1000 milliseconds, 1000 milliseconds, 1000 milliseconds)
  )

  val high: TrafficConfiguration = TrafficConfiguration(
    activeUsers = 70,
    duration = 30 minutes,
    patients=385,
    responseTimes = MaximumResponseTimes(1200 milliseconds, 1200 milliseconds, 1200 milliseconds)
  )

  val peak: TrafficConfiguration = TrafficConfiguration(
    activeUsers = 90,
    duration = 30 minutes,
    patients=495,
    responseTimes = MaximumResponseTimes(28000 milliseconds, 28000 milliseconds, 28000 milliseconds)
  )

  val dev: TrafficConfiguration = TrafficConfiguration(
    activeUsers = 40,
    duration =5 minutes,
    patients=220,
    responseTimes = MaximumResponseTimes(1000 milliseconds, 1000 milliseconds, 1000 milliseconds)
  )

  def getTrafficShareConfiguration(loadSharePercentage: Int): TrafficShareConfiguration = {
    System.out.println(s"Load Simulation Type is : ${System.getenv("LOAD_SIMULATION_TYPE") toLowerCase}")
    System.getenv("LOAD_SIMULATION_TYPE") toLowerCase match {
      case "high" =>
        new TrafficShareConfiguration(high, loadSharePercentage)
      case "peak" =>
        new TrafficShareConfiguration(peak, loadSharePercentage)
      case "dev" =>
        new TrafficShareConfiguration(dev, loadSharePercentage)
      case "standard" | _ =>
        new TrafficShareConfiguration(standard, loadSharePercentage)
    }
  }

  val getLoadParameters: TrafficConfiguration = {
    System.getenv("LOAD_SIMULATION_TYPE") toLowerCase match {
      case "high" =>
        high
      case "peak" =>
        peak
      case "dev" =>
        dev
      case "standard" | _ =>
        standard
    }
  }

  val getPatientCount: String => Int = (registries: String) => {
    val trafficConfig = getLoadParameters
    if(PATIENT_SWITCH) {
      registries toLowerCase match {
        case "doctor" =>
          (trafficConfig.duration.div(5 minutes)*(trafficConfig.activeUsers*(CONSULTATION_LOAD_SHARE/100.00))).toInt
        case "frontdesk" =>
          (trafficConfig.duration.div(2 minutes)*(trafficConfig.activeUsers*(REGISTRATION_LOAD_SHARE/100.00))).toInt
      }
    } else {
      registries toLowerCase match {
        case "doctor" =>
          (0.272*trafficConfig.patients).toInt
        case "frontdesk" =>
          (0.68*trafficConfig.patients).toInt
      }
    }
  }
}

case class TrafficConfiguration(
    activeUsers: Int,
    duration: FiniteDuration,
    patients:Int,
    responseTimes: MaximumResponseTimes
)

class TrafficShareConfiguration(
    trafficConfiguration: TrafficConfiguration,
    loadSharePercentage: Int
) {
  val shareFactor: Int = roundUp(100 / loadSharePercentage)
  val activeUsers: Int = atLeast10(roundUp(trafficConfiguration.activeUsers / shareFactor))
  val totalDuration: FiniteDuration = atLeast1Minute(trafficConfiguration.duration)
  val maximumResponseTimes: MaximumResponseTimes = trafficConfiguration.responseTimes
  val initialRampUpDuration: FiniteDuration = Duration(totalDuration.mul(0.1).toSeconds,SECONDS).min(5 minutes) //should be 10% of total duration with max of 5 mins
  val patients:Int=roundUp(trafficConfiguration.patients)

  private def roundUp(d: Double): Int = math.ceil(d).toInt

  private def atLeast10(value: Int) = max(1, value)
  private def atLeast1Minute(value: FiniteDuration) = value.max(1 minutes)
}

/** Need to define various possible measurement request types e.g. get, query, list, update, create
  * We could also have a response type per API calls
  * @param query
  * @param list
  * @param update
  */

case class MaximumResponseTimes(
    query: FiniteDuration,
    list: FiniteDuration,
    update: FiniteDuration
)
