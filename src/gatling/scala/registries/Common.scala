package registries

import api.HttpRequests.{getGlobalProperty, getLoginLocations}
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

object Common {
  val login: ChainBuilder = exec(
    getLoginLocations
      .resources(
        getGlobalProperty("locale.allowed.list")
      )
  )
}
