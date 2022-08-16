package registries

import api.HttpRequests._
import configurations.MaximumResponseTimes
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

object Common {
  def login(responseTimes: MaximumResponseTimes): ChainBuilder = exec(
    getLoginLocations.check(
      status.is(200),
      responseTimeInMillis.lte(responseTimes.query.toMillis.toInt)
    )
      .resources(
        getGlobalProperty("locale.allowed.list").check(
          status.is(200),
          responseTimeInMillis.lte(responseTimes.query.toMillis.toInt)
        )
      )
  )
}
