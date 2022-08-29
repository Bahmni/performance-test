package configurations

import io.gatling.core.Predef.{configuration, csv}
import io.gatling.core.feeder.BatchableFeederBuilder

import scala.util.Random

object Feeders {
  val patientName: BatchableFeederBuilder[String] = csv("patientName.csv").circular
  val rnd = new Random()

  def randomString(length: Int) = {
    rnd.alphanumeric.filter(_.isLetter).take(length).mkString
  }

  val jsonFeeder = Iterator.continually(
    Map(
      "givenName" -> randomString(5),
      "streetAddress1" -> randomString(8)
    )
  )
}
