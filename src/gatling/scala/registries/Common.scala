package registries

import api.HttpRequests.{getGlobalProperty, getLoginLocations}
import configurations.MaximumResponseTimes
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder

object Common {
  def login(responseTimes: MaximumResponseTimes): ChainBuilder = exec(
    getLoginLocations
      .resources(
        getGlobalProperty("locale.allowed.list")
      )
  )
}
