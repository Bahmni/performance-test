package registries

import infrastructure.{MaximumResponseTimes, Possibility}
import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Google {
  val possibleCalls: List[Possibility] = List(
    Possibility(home, 100)
//    Possibility(search, 50)
  )

  def home(responseTimes: MaximumResponseTimes) = {
    exec(
      http("www.google.com")
        .get("/")
    )
  }

  def search(responseTimes: MaximumResponseTimes) = {
    exec(
      http("www.google.com")
        .get("/")
    ).pause(1)
      .exec(
        http("search bahmni")
          .get("/search?q=bahmni")
      )
  }
}
