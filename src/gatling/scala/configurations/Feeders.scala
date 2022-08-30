package configurations

import io.gatling.core.Predef.{configuration, csv}
import io.gatling.core.feeder.BatchableFeederBuilder
import api.HttpRequests._

import scala.util.Random

import scala.util.Random

object Feeders {
  val patientName: BatchableFeederBuilder[String] = csv("patientName.csv").circular
  val rnd = new Random()
  var identifierType:String=null
  var identiferSourceId:String=null

  def randomString(length: Int) = {
    rnd.alphanumeric.filter(_.isLetter).take(length).mkString
  }

  val jsonFeeder = Iterator.continually(
    Map(
      "givenName" -> randomString(5),
      "streetAddress1" -> randomString(8),
      "identifierType"-> identifierType,
      "identifierSourcesId"->identiferSourceId

    )
  )
}
