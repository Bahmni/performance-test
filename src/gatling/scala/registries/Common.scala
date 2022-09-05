package registries

import api.HttpRequests.{getGlobalProperty, getLoginLocations}
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import api.HttpRequests._
import api.Constants._

object Common {
  val login: ChainBuilder = exec(
    getLoginLocations
      .resources(
        getGlobalProperty("locale.allowed.list")
      )
  )

  val goToHomePage: ChainBuilder = exec(
    getUser(LOGIN_USER)
      .check(
        jsonPath("$..results[0].uuid").find.saveAs("runTimeUuid")
      )
      .resources(
        getProviderForUser("#{runTimeUuid}"),
        deleteSession,
        getSession,
        getGlobalProperty("bahmni.enableAuditLog"),
        postAuditLog
      )
  )
    .exec(postUserInfo("#{runTimeUuid}"))
}
