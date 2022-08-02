package configurations

import io.gatling.core.Predef.{configuration, csv}
import io.gatling.core.feeder.BatchableFeederBuilder

object Feeders {
  val patientName: BatchableFeederBuilder[String] = csv("patientName.csv").circular
}
