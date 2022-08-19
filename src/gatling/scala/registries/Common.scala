package registries

import api.HttpRequests.{getGlobalProperty, getLoginLocations}
import configurations.MaximumResponseTimes
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder

object Common {
  val login: ChainBuilder = exec(
    getLoginLocations
      .resources(
        getGlobalProperty("locale.allowed.list")
      )
  )
}
