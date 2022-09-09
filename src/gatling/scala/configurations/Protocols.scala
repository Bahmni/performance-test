package configurations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

object Protocols {
  private val url = "https://performance.lite.mybahmni.in" //System.getProperty("base_url")
  val default: HttpProtocolBuilder = http
    .baseUrl(url)
    .disableCaching
    .inferHtmlResources()
    .basicAuth("superman", "Admin123")
    .acceptHeader("Cache-Control, max-age=0, no-store")
    .acceptHeader("application/json, text/plain, */*")
    .contentTypeHeader("application/json")
    .acceptEncodingHeader("gzip, deflate, sdch, br")
    .acceptLanguageHeader("en-US,en;q=0.8")
    .userAgentHeader(
      "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36"
    )
}
