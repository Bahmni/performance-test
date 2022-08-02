package registries

import infrastructure.{Feeders, MaximumResponseTimes, Possibility}
import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Explore {
  val possibleCalls = List(
    Possibility(search, 80),
    Possibility(browse, 20)
  )

  def search(responseTimes: MaximumResponseTimes) = {
    exec(
      http("Home")
        .get("/")
    )
      .pause(1)
      .feed(Feeders.search)
      .exec(
        http("Search")
          .get("/computers?f=#{searchCriterion}")
          .check(
            status.is(200), // we are hitting RATE LIMIT 429
            responseTimeInMillis.lte(responseTimes.query.toMillis.toInt),
            css("a:contains('#{searchComputerName}')", "href").saveAs("computerUrl")
          )
      )
  }

  def browse(responseTimes: MaximumResponseTimes) =
    repeat(4, "i") {
      exec(
        http("Page #{i}")
          .get("/computers?p=#{i}")
      ).pause(1)
    }
}
