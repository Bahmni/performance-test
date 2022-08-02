package registries

import configurations.MaximumResponseTimes
import io.gatling.core.Predef._
import io.gatling.http.Predef._

object GoogleSearch {
  def home(responseTimes: MaximumResponseTimes) = {
    exec(
      http("www.google.com")
        .get("https://www.google.com")
        .check(
          status.is(200),
          responseTimeInMillis.lte(responseTimes.query.toMillis.toInt)
        )
    )
  }

  def search(responseTimes: MaximumResponseTimes) = {
    exec(
      http("search bahmni")
        .get("https://www.google.com/search?q=bahmni")
    )
  }
}
