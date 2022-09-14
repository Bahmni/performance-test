package configurations

import api.Constants.{IMAGES_ENCOUNTER_UUID, LOGIN_LOCATION_UUID, PROVIDER_UUID}
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import io.gatling.core.Predef.{configuration, csv}
import io.gatling.core.feeder.BatchableFeederBuilder

import java.text.SimpleDateFormat
import java.util.Calendar
import scala.util.Random

object Feeders {
  val rnd = new Random()
  val inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  var orders: String ="[]"
  var drugOrders: String = "[]"
  var observations:String= "[]"
  var visitUuid: String = null


  def randomString(length: Int): String = {
    rnd.alphanumeric.filter(_.isLetter).take(length).mkString
  }

  def getCurrentDateAndTime(): String = {
    inputFormat.format(Calendar.getInstance.getTime)
  }

  var jsonFeeder: Iterator[Map[String, Serializable]] = Iterator.continually(
    Map(
      "givenName" -> randomString(5),
      "streetAddress1" -> randomString(8),
      "locationUuid" -> LOGIN_LOCATION_UUID,
      "providerUuid" -> PROVIDER_UUID,
      "orders" -> orders,
      "drugOrders" -> drugOrders,
      "observations"->observations
    )
  )

  var docUploadFeeder: Iterator[Map[String, Serializable]] = Iterator.continually(
    Map(
      "visitStartDate" -> getCurrentDateAndTime(),
      "encounterTypeUuid" -> IMAGES_ENCOUNTER_UUID,
      "providerUuid" -> PROVIDER_UUID,
      "locationUuid" -> LOGIN_LOCATION_UUID
    )
  )

}
