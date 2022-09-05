package configurations

import api.Constants.{IMAGES_ENCOUNTER_UUID, IMAGES_PROVIDER_UUID, LOGIN_LOCATION_UUID, PROVIDER_UUID}
import io.gatling.core.Predef.{configuration, csv}
import io.gatling.core.feeder.BatchableFeederBuilder

import java.text.SimpleDateFormat
import java.util.Calendar
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
  var ptUuid: String = ""
  var visitTypeUuid: String =null
  var visitUuid: String = null

  def randomString(length: Int): String = {
    rnd.alphanumeric.filter(_.isLetter).take(length).mkString
  }

  def getCurrentDateAndTime(): String = {
    val inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    inputFormat.format(Calendar.getInstance.getTime)
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

  var docUploadFeeder: Iterator[Map[String, Serializable]] = Iterator.continually(
    Map(
      "ptUuid" -> ptUuid,
      "visitTypeUuid" -> visitTypeUuid,
      "visitStartDate" -> getCurrentDateAndTime(),
      "encounterTypeUuid" -> IMAGES_ENCOUNTER_UUID,
      "providerUuid" -> IMAGES_PROVIDER_UUID,
      "visitUuid" -> visitUuid,
      "locationUuid" -> LOGIN_LOCATION_UUID
    )
  )
}
