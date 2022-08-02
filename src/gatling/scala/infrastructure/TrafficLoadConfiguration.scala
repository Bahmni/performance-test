package infrastructure

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.math.{max, min}

object Load {

  // Day to day usage, a steady stream of 10rps coming in
  val Standard = new TrafficLoadConfiguration(
    5, // initialNumberOfUsers
    10, // totalNumberOfUsers
    30 seconds, // ramp up time
    1 minutes, // cycle duration
    MaximumResponseTimes(300 milliseconds, 300 milliseconds, 300 milliseconds)
  )

  // A sudden higher load, going from our 10rps to double
  val High = new TrafficLoadConfiguration(
    10, // initialNumberOfUsers
    20, // totalNumberOfUsers
    30 seconds, // ramp up time
    10 minutes, // cycle duration
    MaximumResponseTimes(500 milliseconds, 500 milliseconds, 500 milliseconds)
  )

  // We were already under high load, and it only peaked more (we got into the news!)
  // Going all the way up from 30rps to 380rps
  val Peak = new TrafficLoadConfiguration(
    30, // initialNumberOfUsers
    380, // totalNumberOfUsers
    1 minutes, // ramp up time
    5 minutes, // cycle duration
    MaximumResponseTimes(
      28000 milliseconds,
      28000 milliseconds,
      28000 milliseconds
    )
  )
}

class TrafficLoadConfiguration(
    initialNumberOfUsers: Int,
    numberOfUsers: Int,
    desiredRampDuration: FiniteDuration,
    desiredCycleDuration: FiniteDuration,
    val responseTimes: MaximumResponseTimes
) {

  val totalNumberOfUsers = atLeast1(numberOfUsers) //10
  val incrementUsersPerSec = atLeast1(atMost20(totalNumberOfUsers / 5)) //2
  val repeatLevels = atLeast1(
    (totalNumberOfUsers / incrementUsersPerSec) - 1
  ) // 10/2 -1 = 4

  val eachLevelLasting = atLeast1Minute(desiredCycleDuration) // 1 min
  val rampDuration = atLeast10Seconds(desiredRampDuration) // 30 seconds
  val startingRate = atLeast1(initialNumberOfUsers) // 5

  private def atLeast1(value: Int) = max(1, value)
  private def atMost20(value: Int) = min(20, value)

  private def atLeast1Minute(value: FiniteDuration) = value.max(1 minutes)
  private def atLeast10Seconds(value: FiniteDuration) = value.max(10 seconds)
}

case class MaximumResponseTimes(
    query: FiniteDuration,
    list: FiniteDuration,
    update: FiniteDuration
)
