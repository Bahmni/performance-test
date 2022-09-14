package configurations

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.math.max

object Load {
  val standard: TrafficConfiguration = TrafficConfiguration(
    activeUsers = 40,
    duration = 15 minutes,
    responseTimes = MaximumResponseTimes(1000 milliseconds, 1000 milliseconds, 1000 milliseconds)
  )

  val high: TrafficConfiguration = TrafficConfiguration(
    activeUsers = 70,
    duration = 60 minutes,
    responseTimes = MaximumResponseTimes(1200 milliseconds, 1200 milliseconds, 1200 milliseconds)
  )

  val peak: TrafficConfiguration = TrafficConfiguration(
    activeUsers = 110,
    duration = 60 minutes,
    responseTimes = MaximumResponseTimes(28000 milliseconds, 28000 milliseconds, 28000 milliseconds)
  )

  val dev: TrafficConfiguration = TrafficConfiguration(
    activeUsers = 20,
    duration = 5 minutes,
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
}

case class TrafficConfiguration(
    activeUsers: Int,
    duration: FiniteDuration,
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
  val initialRampUpDuration: FiniteDuration = 1 minutes //should be 10% of total duration with max of 5 mins

  private def roundUp(d: Double): Int = math.ceil(d).toInt

  private def atLeast10(value: Int) = max(10, value)
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
