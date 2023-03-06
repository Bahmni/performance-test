package configurations

import api.Constants.{HAEMOGRAM_ORDER, IMAGES_ENCOUNTER_UUID, LOGIN_LOCATION_UUID, LOPERAMIDE_DRUG, PROMETHAZINE_DRUG, PROVIDER_UUID, REGLAN_DRUG, THYROID_ORDER}

import java.text.SimpleDateFormat
import java.util.Calendar
import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.util.Random
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

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

  def getRandomPause(min :Int,max:Int):FiniteDuration={
     (60-rnd.between(min,max)) seconds
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
  var observationsFeeder: Iterator[Map[String, Serializable]] = Iterator.continually(
    Map(
      "regUuid" -> REGLAN_DRUG,
      "promUuid" -> PROMETHAZINE_DRUG,
      "lopUuid" -> LOPERAMIDE_DRUG,
      "haeUuid" -> HAEMOGRAM_ORDER,
      "thyUuid"->THYROID_ORDER
    )
  )

  var pauseFeeder: Iterator[Map[String, Serializable]] = Iterator.continually(
    Map(
      "pausePeriod" -> getRandomPause(0,60)
    )
  )

}
