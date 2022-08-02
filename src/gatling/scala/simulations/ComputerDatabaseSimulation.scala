package simulations

import io.gatling.core.Predef._
import io.gatling.core.feeder.BatchableFeederBuilder
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import java.util.concurrent.ThreadLocalRandom

/**
 * This sample is based on our official tutorials:
 *
 *   - [[https://gatling.io/docs/gatling/tutorials/quickstart Gatling quickstart tutorial]]
 *   - [[https://gatling.io/docs/gatling/tutorials/advanced Gatling advanced tutorial]]
 */
class ComputerDatabaseSimulation extends Simulation {

  val feeder: BatchableFeederBuilder[String] = csv("search.csv").random

  private def search =
    exec(
      http("Home")
        .get("/")
    )
      .pause(1)
      .feed(feeder)
      .exec(
        http("Search")
          .get("/computers?f=#{searchCriterion}")
          .check(
            css("a:contains('#{searchComputerName}')", "href").saveAs("computerUrl")
          )
      )
      .pause(1)
      .repeat(2)(
        exec(
          http("Select")
            .get("#{computerUrl}")
            .check(status.is(200))
        ).pause(1)
      )

  // Note we should be using a feeder here
  // let's demonstrate how we can retry: let's make the request fail randomly and retry a given
  // number of times

  private def browse =
    repeat(4, "i") {
      exec(
        http("Page #{i}").get("/computers?p=#{i}")
      ).pause(1)
    }

  private def edit =
    tryMax(2) {
      exec(
        http("Form")
          .get("/computers/new")
      )
        .pause(1)
        .exec(
          http("Post")
            .post("/computers")
            .formParam("name", "Beautiful Computer")
            .formParam("introduced", "2012-05-30")
            .formParam("discontinued", "")
            .formParam("company", "37")
            .check(
              status.is { _ =>
                // we do a check on a condition that's been customized with
                // a lambda. It will be evaluated every time a user executes
                // the request
                200 + ThreadLocalRandom.current().nextInt(2)
              }
            )
        )
    }
      // if the chain didn't finally succeed, have the user exit the whole scenario
      .exitHereIfFailed

  // http configuration
  val httpProtocol: HttpProtocolBuilder =
    http.baseUrl("https://computer-database.gatling.io")
      .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
      .acceptLanguageHeader("en-US,en;q=0.5")
      .acceptEncodingHeader("gzip, deflate")

  // scenario definition
  val users: ScenarioBuilder = scenario("Users").exec(search, browse)
  val admins: ScenarioBuilder = scenario("Admins").exec(search, browse, edit)

  // load scenarios - no of users, how they ramp up and how long they run etc
  setUp(
    users.inject(rampUsers(10).during(10)),
    admins.inject(rampUsers(2).during(10))
  ).protocols(httpProtocol)
}
