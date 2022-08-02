package infrastructure

import io.gatling.core.Predef.{configuration, csv}
import io.gatling.core.feeder.BatchableFeederBuilder

object Feeders {
  val search: BatchableFeederBuilder[String] = csv("search.csv").random
}
