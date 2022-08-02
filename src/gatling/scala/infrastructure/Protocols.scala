package infrastructure

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

object Protocols {
//  private val url = "https://computer-database.gatling.io" //System.getProperty("base_url")
  private val url = "https://www.google.com" //System.getProperty("base_url")
//  private val warmupUrl = "https://computer-database.gatling.io" //System.getProperty("warmup_url")
  private val warmupUrl = "https://www.google.com" //System.getProperty("warmup_url")

  val default: HttpProtocolBuilder = http
    .baseUrl(url)
    .warmUp(warmupUrl)
    .acceptHeader("application/json, text/javascript, */*; q=0.5")
    .acceptLanguageHeader("en-US,en;q=0.9,nl;q=0.8")
    .acceptEncodingHeader("gzip, deflate, br")
    .disableCaching
//    .proxy(Proxy("192.168.1.3", 9090))
}
