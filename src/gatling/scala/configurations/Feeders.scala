package configurations

import api.Constants.{LOGIN_LOCATION_UUID, PROVIDER_UUID}
import io.gatling.core.Predef.{configuration, csv}
import io.gatling.core.feeder.BatchableFeederBuilder

import scala.util.Random

object Feeders {
  val patientName: BatchableFeederBuilder[String] = csv("patientName.csv").circular
  val rnd = new Random()
  var identifierType: String = ""
  var identifierSourceId: String = ""
  var patientUuid: String = ""
  var encounterTypeUuid: String = ""
  var orders: String = "[]"
  var drugOrders: String = "[]"
  def randomString(length: Int): String = {
    rnd.alphanumeric.filter(_.isLetter).take(length).mkString
  }

  var jsonFeeder: Iterator[Map[String, Serializable]] = Iterator.continually(
    Map(
      "givenName" -> randomString(5),
      "streetAddress1" -> randomString(8),
      "identifierType" -> identifierType,
      "identifierSourcesId" -> identifierSourceId,
      "locationUuid" -> LOGIN_LOCATION_UUID,
      "patientUuid" -> patientUuid,
      "encounterTypeUuid" -> encounterTypeUuid,
      "providerUuid" -> PROVIDER_UUID,
      "orders" -> orders,
      "drugOrders" -> drugOrders
    )
  )
}
