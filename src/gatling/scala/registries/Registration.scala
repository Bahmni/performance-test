package registries

import api.HttpRequests._
import io.gatling.http.Predef._
import io.gatling.core.Predef._
import api.Constants.LOGIN_USER
import configurations.MaximumResponseTimes
import io.gatling.core.structure.ChainBuilder

object Registration {
  def goToHomePage(responseTimes: MaximumResponseTimes): ChainBuilder = exec(
    getUser(LOGIN_USER)
      .check(
        jsonPath("$..results[0].uuid").find.saveAs("runTimeUuid")
      )
      .resources(
        getProviderForUser("${runTimeUuid}")
          .check(
            status.is(200),
            responseTimeInMillis.lte(responseTimes.query.toMillis.toInt)
          ),
        getSession,
        postUserInfo("${runTimeUuid}"),
        getGlobalProperty("bahmni.enableAuditLog"),
        postAuditLog
      )
  )
}
