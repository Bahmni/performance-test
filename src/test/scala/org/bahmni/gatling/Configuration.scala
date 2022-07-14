package org.bahmni.gatling

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration.DurationInt

object Configuration {

  object Constants {
    val BASE_HTTPS_URL = "https://demo-v93.mybahmni.org"
    val BASE_HTTP_URL = "http://demo-v93.mybahmni.org"
    val LOGIN_USER = "superman"
    val LOGIN_USER_UUID = "cac3a2c0-4929-4103-90da-de7f99573aab"
    val PROVIDER_UUID = "d390d057-ec33-45c1-8342-9e23d706aa4d"
    val LOGIN_LOCATION_UUID = "baf7bd38-d225-11e4-9c67-080027b662ec"
    val VISIT_TYPE_ID = "c22a5000-3f10-11e4-adec-0800271c1b75"
    val PATIENT_IDENTIFIER = "GAN179326"
    val PATIENT_IDENTIFIER1 = "SIV156624"
    val PATIENT_IDENTIFIER2 = "BAM118663"
    /*val PATIENT_IDENTIFIER = "Sujan Singh"
    val PATIENT_IDENTIFIER1 = "Nidhi"
    val PATIENT_IDENTIFIER2 = "kirtanjli"*/
    val PATIENT_UUID = "dc9444c6-ad55-4200-b6e9-407e025eb948"
    val VISIT_UUID = "8281dd37-45c0-4a45-a939-ecb95fdb6ed7"
    val ANOTHER_PATIENT_UUID = "08047a4e-bb16-42a3-ab0a-b83674756d62"
    val ANOTHER_VISIT_UUID = "71a7e789-1741-44f5-b54e-42e88c3b8e82"
    //val RADIOLOGY_ORDER_TYPE_UUID = "244b43be-28f1-11e4-86a0-005056822b0b" // possible DB
    val RADIOLOGY_ORDER_TYPE_UUID = "8189dbdd-3f10-11e4-adec-0800271c1b75"
    val USG_ORDER_TYPE_UUID = "c39840d9-57a1-11e6-8158-d4ae52d4c69b"
    var ENCOUNTER_TYPE_UUID = "da7a4fe0-0a6a-11e3-939c-8c50edb4be99"
    var ATOMFEED_ENCOUNTER_UUID = "5ec7042b-3998-4eec-95a7-74d81a871057"
    //val ALL_TESTS_AND_PANELS ="24d98284-28f1-11e4-86a0-005056822b0b" //possible DB
    val ALL_TESTS_AND_PANELS ="e4edc5a4-e349-11e3-983a-91270dcbd3bf"
  }

  object HttpConf {
    val HTTPS_PROTOCOL = http
      .baseUrl(Configuration.Constants.BASE_HTTPS_URL).disableCaching
      .inferHtmlResources()
      .basicAuth("superman", "Admin123")
      .acceptHeader("Cache-Control, max-age=0, no-store")
      .acceptHeader("application/json, text/plain, */*")
      .acceptEncodingHeader("gzip, deflate, sdch, br")
      .acceptLanguageHeader("en-US,en;q=0.8")
      .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36");

    val HTTPS_PROTOCOL_FrontDesk = http
      .baseUrl(Configuration.Constants.BASE_HTTPS_URL).disableCaching
      .inferHtmlResources()
      .basicAuth("superman", "Admin123")
      .acceptHeader("Cache-Control, max-age=0, no-store")
      .acceptHeader("application/json, text/plain, */*")
      .acceptEncodingHeader("gzip, deflate, sdch, br")
      .acceptLanguageHeader("en-US,en;q=0.8")
      .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36");

    val HTTPS_PROTOCOL_Doctor = http
      .baseUrl(Configuration.Constants.BASE_HTTPS_URL).disableCaching
      .inferHtmlResources()
      .basicAuth("superman", "Admin123")
      .acceptHeader("Cache-Control, max-age=0, no-store")
      .acceptHeader("application/json, text/plain, */*")
      .acceptEncodingHeader("gzip, deflate, sdch, br")
      .acceptLanguageHeader("en-US,en;q=0.8")
      .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36");

    val HTTP_PROTOCOL = http
      .baseUrl("http://product-qa09.mybahmni.local:8050")

      .inferHtmlResources()
      .basicAuth("superman", "Admin123")
      .acceptHeader("application/json, text/plain, */*")
      .acceptHeader("Cache-Control, max-age=0, no-store")
      .acceptEncodingHeader("gzip, deflate, sdch, br")
      .acceptLanguageHeader("en-US,en;q=0.8")
      .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36");
  }

  object Load {
    var ATOMFEED_USER_PROFILE = rampUsers(1).during(20)
    var DURATION = 120 seconds
  }

}
