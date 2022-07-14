package org.bahmni.gatling

import io.gatling.commons.validation.Validation
import io.gatling.core.Predef._
import io.gatling.core.json.Json
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import org.bahmni.gatling.Configuration.Constants._

import scala.io.Source
import scala.util.parsing.json.JSONObject

object HttpRequestsEncounter {
  def captureEncounter(patient_uuid: String): HttpRequestBuilder = {
    http("capture encounter")
      .post("/openmrs/ws/rest/v1/bahmnicore/bahmniencounter")
      .body(StringBody(s"""{
  "locationUuid": "b4831eb8-c79a-11e2-b284-107d46e7b2c5",
  "patientUuid": "$patient_uuid",
  "encounterUuid": null,
  "visitUuid": null,
  "providers": [
    {
      "uuid": "d390d057-ec33-45c1-8342-9e23d706aa4d"
    }
  ],
  "encounterDateTime": null,
  "extensions": {
    "mdrtbSpecimen": []
  },
  "context": {},
  "visitType": null,
  "bahmniDiagnoses": [],
  "orders": [],
  "drugOrders": [],
  "disposition": null,
  "observations": [
    {
      "concept": {
        "uuid": "f5c4b6a0-e349-11e3-983a-91270dcbd3bf",
        "name": "History and Examination",
        "dataType": "N/A"
      },
      "units": null,
      "label": "History and Examination",
      "possibleAnswers": [],
      "groupMembers": [
        {
          "concept": {
            "uuid": "ef4bf23f-b3ea-4bd3-a3bc-8ff9feb47732",
            "name": "History",
            "dataType": "Text"
          },
          "units": null,
          "label": "History",
          "possibleAnswers": [],
          "groupMembers": [],
          "comment": null,
          "isObservation": true,
          "conceptUIConfig": [],
          "uniqueId": "observation_6",
          "erroneousValue": null,
          "value": "some data",
          "autocompleteValue": "some data",
          "__prevValue": "some dat",
          "_value": "some data",
          "disabled": false,
          "voided": false
        },
        {
          "concept": {
            "uuid": "ee488036-cfa0-455a-87aa-033f5e5117cf",
            "name": "Taking Medications",
            "dataType": "Coded"
          },
          "units": null,
          "label": "Taking Medications",
          "possibleAnswers": [
            {
              "uuid": "693d783e-8a86-486e-af9d-6c8be61c38a8",
              "name": {
                "display": "Medication Taking - Yes",
                "uuid": "2282cd7b-7bce-4477-9b9d-b25869a75ca7",
                "name": "Medication Taking - Yes",
                "locale": "en",
                "localePreferred": true,
                "conceptNameType": "FULLY_SPECIFIED",
                "links": [
                  {
                    "rel": "self",
                    "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/693d783e-8a86-486e-af9d-6c8be61c38a8/name/2282cd7b-7bce-4477-9b9d-b25869a75ca7"
                  },
                  {
                    "rel": "full",
                    "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/693d783e-8a86-486e-af9d-6c8be61c38a8/name/2282cd7b-7bce-4477-9b9d-b25869a75ca7?v=full"
                  }
                ],
                "resourceVersion": "1.9"
              },
              "names": [
                {
                  "display": "Yes",
                  "uuid": "2112f8ad-7ffe-4b5a-a770-93ab1cc220eb",
                  "name": "Yes",
                  "locale": "en",
                  "localePreferred": false,
                  "conceptNameType": "SHORT",
                  "links": [
                    {
                      "rel": "self",
                      "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/693d783e-8a86-486e-af9d-6c8be61c38a8/name/2112f8ad-7ffe-4b5a-a770-93ab1cc220eb"
                    },
                    {
                      "rel": "full",
                      "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/693d783e-8a86-486e-af9d-6c8be61c38a8/name/2112f8ad-7ffe-4b5a-a770-93ab1cc220eb?v=full"
                    }
                  ],
                  "resourceVersion": "1.9"
                },
                {
                  "display": "Medication Taking - Yes",
                  "uuid": "2282cd7b-7bce-4477-9b9d-b25869a75ca7",
                  "name": "Medication Taking - Yes",
                  "locale": "en",
                  "localePreferred": true,
                  "conceptNameType": "FULLY_SPECIFIED",
                  "links": [
                    {
                      "rel": "self",
                      "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/693d783e-8a86-486e-af9d-6c8be61c38a8/name/2282cd7b-7bce-4477-9b9d-b25869a75ca7"
                    },
                    {
                      "rel": "full",
                      "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/693d783e-8a86-486e-af9d-6c8be61c38a8/name/2282cd7b-7bce-4477-9b9d-b25869a75ca7?v=full"
                    }
                  ],
                  "resourceVersion": "1.9"
                }
              ],
              "displayString": "Medication Taking - Yes",
              "resourceVersion": "1.9"
            },
            {
              "uuid": "525611e3-4f35-478d-9fa4-4493e7388512",
              "name": {
                "display": "Medication Taking - No",
                "uuid": "41949754-329c-4abd-afc6-b4f067cf9829",
                "name": "Medication Taking - No",
                "locale": "en",
                "localePreferred": true,
                "conceptNameType": "FULLY_SPECIFIED",
                "links": [
                  {
                    "rel": "self",
                    "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/525611e3-4f35-478d-9fa4-4493e7388512/name/41949754-329c-4abd-afc6-b4f067cf9829"
                  },
                  {
                    "rel": "full",
                    "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/525611e3-4f35-478d-9fa4-4493e7388512/name/41949754-329c-4abd-afc6-b4f067cf9829?v=full"
                  }
                ],
                "resourceVersion": "1.9"
              },
              "names": [
                {
                  "display": "No",
                  "uuid": "e23dc6ff-3331-435a-9c06-c68077f99610",
                  "name": "No",
                  "locale": "en",
                  "localePreferred": false,
                  "conceptNameType": "SHORT",
                  "links": [
                    {
                      "rel": "self",
                      "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/525611e3-4f35-478d-9fa4-4493e7388512/name/e23dc6ff-3331-435a-9c06-c68077f99610"
                    },
                    {
                      "rel": "full",
                      "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/525611e3-4f35-478d-9fa4-4493e7388512/name/e23dc6ff-3331-435a-9c06-c68077f99610?v=full"
                    }
                  ],
                  "resourceVersion": "1.9"
                },
                {
                  "display": "Medication Taking - No",
                  "uuid": "41949754-329c-4abd-afc6-b4f067cf9829",
                  "name": "Medication Taking - No",
                  "locale": "en",
                  "localePreferred": true,
                  "conceptNameType": "FULLY_SPECIFIED",
                  "links": [
                    {
                      "rel": "self",
                      "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/525611e3-4f35-478d-9fa4-4493e7388512/name/41949754-329c-4abd-afc6-b4f067cf9829"
                    },
                    {
                      "rel": "full",
                      "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/525611e3-4f35-478d-9fa4-4493e7388512/name/41949754-329c-4abd-afc6-b4f067cf9829?v=full"
                    }
                  ],
                  "resourceVersion": "1.9"
                }
              ],
              "displayString": "Medication Taking - No",
              "resourceVersion": "1.9"
            }
          ],
          "groupMembers": [],
          "comment": null,
          "isObservation": true,
          "conceptUIConfig": [],
          "uniqueId": "observation_8",
          "erroneousValue": null,
          "value": {
            "uuid": "525611e3-4f35-478d-9fa4-4493e7388512",
            "name": {
              "display": "Medication Taking - No",
              "uuid": "41949754-329c-4abd-afc6-b4f067cf9829",
              "name": "Medication Taking - No",
              "locale": "en",
              "localePreferred": true,
              "conceptNameType": "FULLY_SPECIFIED",
              "links": [
                {
                  "rel": "self",
                  "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/525611e3-4f35-478d-9fa4-4493e7388512/name/41949754-329c-4abd-afc6-b4f067cf9829"
                },
                {
                  "rel": "full",
                  "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/525611e3-4f35-478d-9fa4-4493e7388512/name/41949754-329c-4abd-afc6-b4f067cf9829?v=full"
                }
              ],
              "resourceVersion": "1.9"
            },
            "names": [
              {
                "display": "No",
                "uuid": "e23dc6ff-3331-435a-9c06-c68077f99610",
                "name": "No",
                "locale": "en",
                "localePreferred": false,
                "conceptNameType": "SHORT",
                "links": [
                  {
                    "rel": "self",
                    "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/525611e3-4f35-478d-9fa4-4493e7388512/name/e23dc6ff-3331-435a-9c06-c68077f99610"
                  },
                  {
                    "rel": "full",
                    "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/525611e3-4f35-478d-9fa4-4493e7388512/name/e23dc6ff-3331-435a-9c06-c68077f99610?v=full"
                  }
                ],
                "resourceVersion": "1.9"
              },
              {
                "display": "Medication Taking - No",
                "uuid": "41949754-329c-4abd-afc6-b4f067cf9829",
                "name": "Medication Taking - No",
                "locale": "en",
                "localePreferred": true,
                "conceptNameType": "FULLY_SPECIFIED",
                "links": [
                  {
                    "rel": "self",
                    "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/525611e3-4f35-478d-9fa4-4493e7388512/name/41949754-329c-4abd-afc6-b4f067cf9829"
                  },
                  {
                    "rel": "full",
                    "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/525611e3-4f35-478d-9fa4-4493e7388512/name/41949754-329c-4abd-afc6-b4f067cf9829?v=full"
                  }
                ],
                "resourceVersion": "1.9"
              }
            ],
            "displayString": "Medication Taking - No",
            "resourceVersion": "1.9"
          },
          "autocompleteValue": {
            "display": "Medication Taking - No",
            "uuid": "41949754-329c-4abd-afc6-b4f067cf9829",
            "name": "Medication Taking - No",
            "locale": "en",
            "localePreferred": true,
            "conceptNameType": "FULLY_SPECIFIED",
            "links": [
              {
                "rel": "self",
                "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/525611e3-4f35-478d-9fa4-4493e7388512/name/41949754-329c-4abd-afc6-b4f067cf9829"
              },
              {
                "rel": "full",
                "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/525611e3-4f35-478d-9fa4-4493e7388512/name/41949754-329c-4abd-afc6-b4f067cf9829?v=full"
              }
            ],
            "resourceVersion": "1.9"
          },
          "showComment": false,
          "__prevValue": {
            "uuid": "693d783e-8a86-486e-af9d-6c8be61c38a8",
            "name": {
              "display": "Medication Taking - Yes",
              "uuid": "2282cd7b-7bce-4477-9b9d-b25869a75ca7",
              "name": "Medication Taking - Yes",
              "locale": "en",
              "localePreferred": true,
              "conceptNameType": "FULLY_SPECIFIED",
              "links": [
                {
                  "rel": "self",
                  "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/693d783e-8a86-486e-af9d-6c8be61c38a8/name/2282cd7b-7bce-4477-9b9d-b25869a75ca7"
                },
                {
                  "rel": "full",
                  "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/693d783e-8a86-486e-af9d-6c8be61c38a8/name/2282cd7b-7bce-4477-9b9d-b25869a75ca7?v=full"
                }
              ],
              "resourceVersion": "1.9"
            },
            "names": [
              {
                "display": "Yes",
                "uuid": "2112f8ad-7ffe-4b5a-a770-93ab1cc220eb",
                "name": "Yes",
                "locale": "en",
                "localePreferred": false,
                "conceptNameType": "SHORT",
                "links": [
                  {
                    "rel": "self",
                    "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/693d783e-8a86-486e-af9d-6c8be61c38a8/name/2112f8ad-7ffe-4b5a-a770-93ab1cc220eb"
                  },
                  {
                    "rel": "full",
                    "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/693d783e-8a86-486e-af9d-6c8be61c38a8/name/2112f8ad-7ffe-4b5a-a770-93ab1cc220eb?v=full"
                  }
                ],
                "resourceVersion": "1.9"
              },
              {
                "display": "Medication Taking - Yes",
                "uuid": "2282cd7b-7bce-4477-9b9d-b25869a75ca7",
                "name": "Medication Taking - Yes",
                "locale": "en",
                "localePreferred": true,
                "conceptNameType": "FULLY_SPECIFIED",
                "links": [
                  {
                    "rel": "self",
                    "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/693d783e-8a86-486e-af9d-6c8be61c38a8/name/2282cd7b-7bce-4477-9b9d-b25869a75ca7"
                  },
                  {
                    "rel": "full",
                    "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/693d783e-8a86-486e-af9d-6c8be61c38a8/name/2282cd7b-7bce-4477-9b9d-b25869a75ca7?v=full"
                  }
                ],
                "resourceVersion": "1.9"
              }
            ],
            "displayString": "Medication Taking - Yes",
            "resourceVersion": "1.9"
          },
          "_value": {
            "uuid": "525611e3-4f35-478d-9fa4-4493e7388512",
            "name": {
              "display": "Medication Taking - No",
              "uuid": "41949754-329c-4abd-afc6-b4f067cf9829",
              "name": "Medication Taking - No",
              "locale": "en",
              "localePreferred": true,
              "conceptNameType": "FULLY_SPECIFIED",
              "links": [
                {
                  "rel": "self",
                  "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/525611e3-4f35-478d-9fa4-4493e7388512/name/41949754-329c-4abd-afc6-b4f067cf9829"
                },
                {
                  "rel": "full",
                  "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/525611e3-4f35-478d-9fa4-4493e7388512/name/41949754-329c-4abd-afc6-b4f067cf9829?v=full"
                }
              ],
              "resourceVersion": "1.9"
            },
            "names": [
              {
                "display": "No",
                "uuid": "e23dc6ff-3331-435a-9c06-c68077f99610",
                "name": "No",
                "locale": "en",
                "localePreferred": false,
                "conceptNameType": "SHORT",
                "links": [
                  {
                    "rel": "self",
                    "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/525611e3-4f35-478d-9fa4-4493e7388512/name/e23dc6ff-3331-435a-9c06-c68077f99610"
                  },
                  {
                    "rel": "full",
                    "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/525611e3-4f35-478d-9fa4-4493e7388512/name/e23dc6ff-3331-435a-9c06-c68077f99610?v=full"
                  }
                ],
                "resourceVersion": "1.9"
              },
              {
                "display": "Medication Taking - No",
                "uuid": "41949754-329c-4abd-afc6-b4f067cf9829",
                "name": "Medication Taking - No",
                "locale": "en",
                "localePreferred": true,
                "conceptNameType": "FULLY_SPECIFIED",
                "links": [
                  {
                    "rel": "self",
                    "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/525611e3-4f35-478d-9fa4-4493e7388512/name/41949754-329c-4abd-afc6-b4f067cf9829"
                  },
                  {
                    "rel": "full",
                    "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/525611e3-4f35-478d-9fa4-4493e7388512/name/41949754-329c-4abd-afc6-b4f067cf9829?v=full"
                  }
                ],
                "resourceVersion": "1.9"
              }
            ],
            "displayString": "Medication Taking - No",
            "resourceVersion": "1.9"
          },
          "disabled": false,
          "voided": false
        },
        {
          "concept": {
            "uuid": "04b61778-0695-409e-bfb6-6b81112958f3",
            "name": "Change in symptoms",
            "dataType": "Coded"
          },
          "units": null,
          "label": "Change in symptoms",
          "possibleAnswers": [
            {
              "uuid": "9ebe6e3c-dd8f-4366-afff-fd817ad8d078",
              "name": {
                "display": "Improved",
                "uuid": "a8c4565b-50e5-4797-a0ce-91416aec1377",
                "name": "Improved",
                "locale": "en",
                "localePreferred": true,
                "conceptNameType": "FULLY_SPECIFIED",
                "links": [
                  {
                    "rel": "self",
                    "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/9ebe6e3c-dd8f-4366-afff-fd817ad8d078/name/a8c4565b-50e5-4797-a0ce-91416aec1377"
                  },
                  {
                    "rel": "full",
                    "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/9ebe6e3c-dd8f-4366-afff-fd817ad8d078/name/a8c4565b-50e5-4797-a0ce-91416aec1377?v=full"
                  }
                ],
                "resourceVersion": "1.9"
              },
              "names": [
                {
                  "display": "Improved",
                  "uuid": "a8c4565b-50e5-4797-a0ce-91416aec1377",
                  "name": "Improved",
                  "locale": "en",
                  "localePreferred": true,
                  "conceptNameType": "FULLY_SPECIFIED",
                  "links": [
                    {
                      "rel": "self",
                      "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/9ebe6e3c-dd8f-4366-afff-fd817ad8d078/name/a8c4565b-50e5-4797-a0ce-91416aec1377"
                    },
                    {
                      "rel": "full",
                      "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/9ebe6e3c-dd8f-4366-afff-fd817ad8d078/name/a8c4565b-50e5-4797-a0ce-91416aec1377?v=full"
                    }
                  ],
                  "resourceVersion": "1.9"
                }
              ],
              "displayString": "Improved",
              "resourceVersion": "1.9"
            },
            {
              "uuid": "81e07c58-ae1a-401d-a3cb-949ec688f100",
              "name": {
                "display": "Same",
                "uuid": "3ae874fa-e69c-4f40-86fa-722170b90d85",
                "name": "Same",
                "locale": "en",
                "localePreferred": true,
                "conceptNameType": "FULLY_SPECIFIED",
                "links": [
                  {
                    "rel": "self",
                    "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/81e07c58-ae1a-401d-a3cb-949ec688f100/name/3ae874fa-e69c-4f40-86fa-722170b90d85"
                  },
                  {
                    "rel": "full",
                    "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/81e07c58-ae1a-401d-a3cb-949ec688f100/name/3ae874fa-e69c-4f40-86fa-722170b90d85?v=full"
                  }
                ],
                "resourceVersion": "1.9"
              },
              "names": [
                {
                  "display": "Same",
                  "uuid": "3ae874fa-e69c-4f40-86fa-722170b90d85",
                  "name": "Same",
                  "locale": "en",
                  "localePreferred": true,
                  "conceptNameType": "FULLY_SPECIFIED",
                  "links": [
                    {
                      "rel": "self",
                      "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/81e07c58-ae1a-401d-a3cb-949ec688f100/name/3ae874fa-e69c-4f40-86fa-722170b90d85"
                    },
                    {
                      "rel": "full",
                      "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/81e07c58-ae1a-401d-a3cb-949ec688f100/name/3ae874fa-e69c-4f40-86fa-722170b90d85?v=full"
                    }
                  ],
                  "resourceVersion": "1.9"
                }
              ],
              "displayString": "Same",
              "resourceVersion": "1.9"
            },
            {
              "uuid": "169aac7f-9579-47e0-a492-200f3676beca",
              "name": {
                "display": "Worsened",
                "uuid": "e968b454-be10-49b1-8805-d9adaddc52e2",
                "name": "Worsened",
                "locale": "en",
                "localePreferred": true,
                "conceptNameType": "FULLY_SPECIFIED",
                "links": [
                  {
                    "rel": "self",
                    "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/169aac7f-9579-47e0-a492-200f3676beca/name/e968b454-be10-49b1-8805-d9adaddc52e2"
                  },
                  {
                    "rel": "full",
                    "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/169aac7f-9579-47e0-a492-200f3676beca/name/e968b454-be10-49b1-8805-d9adaddc52e2?v=full"
                  }
                ],
                "resourceVersion": "1.9"
              },
              "names": [
                {
                  "display": "Worsened",
                  "uuid": "e968b454-be10-49b1-8805-d9adaddc52e2",
                  "name": "Worsened",
                  "locale": "en",
                  "localePreferred": true,
                  "conceptNameType": "FULLY_SPECIFIED",
                  "links": [
                    {
                      "rel": "self",
                      "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/169aac7f-9579-47e0-a492-200f3676beca/name/e968b454-be10-49b1-8805-d9adaddc52e2"
                    },
                    {
                      "rel": "full",
                      "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/169aac7f-9579-47e0-a492-200f3676beca/name/e968b454-be10-49b1-8805-d9adaddc52e2?v=full"
                    }
                  ],
                  "resourceVersion": "1.9"
                }
              ],
              "displayString": "Worsened",
              "resourceVersion": "1.9"
            }
          ],
          "groupMembers": [],
          "comment": null,
          "isObservation": true,
          "conceptUIConfig": [],
          "uniqueId": "observation_9",
          "erroneousValue": null,
          "value": {
            "uuid": "9ebe6e3c-dd8f-4366-afff-fd817ad8d078",
            "name": {
              "display": "Improved",
              "uuid": "a8c4565b-50e5-4797-a0ce-91416aec1377",
              "name": "Improved",
              "locale": "en",
              "localePreferred": true,
              "conceptNameType": "FULLY_SPECIFIED",
              "links": [
                {
                  "rel": "self",
                  "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/9ebe6e3c-dd8f-4366-afff-fd817ad8d078/name/a8c4565b-50e5-4797-a0ce-91416aec1377"
                },
                {
                  "rel": "full",
                  "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/9ebe6e3c-dd8f-4366-afff-fd817ad8d078/name/a8c4565b-50e5-4797-a0ce-91416aec1377?v=full"
                }
              ],
              "resourceVersion": "1.9"
            },
            "names": [
              {
                "display": "Improved",
                "uuid": "a8c4565b-50e5-4797-a0ce-91416aec1377",
                "name": "Improved",
                "locale": "en",
                "localePreferred": true,
                "conceptNameType": "FULLY_SPECIFIED",
                "links": [
                  {
                    "rel": "self",
                    "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/9ebe6e3c-dd8f-4366-afff-fd817ad8d078/name/a8c4565b-50e5-4797-a0ce-91416aec1377"
                  },
                  {
                    "rel": "full",
                    "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/9ebe6e3c-dd8f-4366-afff-fd817ad8d078/name/a8c4565b-50e5-4797-a0ce-91416aec1377?v=full"
                  }
                ],
                "resourceVersion": "1.9"
              }
            ],
            "displayString": "Improved",
            "resourceVersion": "1.9"
          },
          "autocompleteValue": {
            "display": "Improved",
            "uuid": "a8c4565b-50e5-4797-a0ce-91416aec1377",
            "name": "Improved",
            "locale": "en",
            "localePreferred": true,
            "conceptNameType": "FULLY_SPECIFIED",
            "links": [
              {
                "rel": "self",
                "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/9ebe6e3c-dd8f-4366-afff-fd817ad8d078/name/a8c4565b-50e5-4797-a0ce-91416aec1377"
              },
              {
                "rel": "full",
                "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/9ebe6e3c-dd8f-4366-afff-fd817ad8d078/name/a8c4565b-50e5-4797-a0ce-91416aec1377?v=full"
              }
            ],
            "resourceVersion": "1.9"
          },
          "showComment": false,
          "_value": {
            "uuid": "9ebe6e3c-dd8f-4366-afff-fd817ad8d078",
            "name": {
              "display": "Improved",
              "uuid": "a8c4565b-50e5-4797-a0ce-91416aec1377",
              "name": "Improved",
              "locale": "en",
              "localePreferred": true,
              "conceptNameType": "FULLY_SPECIFIED",
              "links": [
                {
                  "rel": "self",
                  "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/9ebe6e3c-dd8f-4366-afff-fd817ad8d078/name/a8c4565b-50e5-4797-a0ce-91416aec1377"
                },
                {
                  "rel": "full",
                  "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/9ebe6e3c-dd8f-4366-afff-fd817ad8d078/name/a8c4565b-50e5-4797-a0ce-91416aec1377?v=full"
                }
              ],
              "resourceVersion": "1.9"
            },
            "names": [
              {
                "display": "Improved",
                "uuid": "a8c4565b-50e5-4797-a0ce-91416aec1377",
                "name": "Improved",
                "locale": "en",
                "localePreferred": true,
                "conceptNameType": "FULLY_SPECIFIED",
                "links": [
                  {
                    "rel": "self",
                    "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/9ebe6e3c-dd8f-4366-afff-fd817ad8d078/name/a8c4565b-50e5-4797-a0ce-91416aec1377"
                  },
                  {
                    "rel": "full",
                    "uri": "http://product-qa09.mybahmni.local/openmrs/ws/rest/v1/concept/9ebe6e3c-dd8f-4366-afff-fd817ad8d078/name/a8c4565b-50e5-4797-a0ce-91416aec1377?v=full"
                  }
                ],
                "resourceVersion": "1.9"
              }
            ],
            "displayString": "Improved",
            "resourceVersion": "1.9"
          },
          "disabled": false,
          "voided": false
        }
      ],
      "comment": null,
      "isObservation": true,
      "conceptUIConfig": [],
      "uniqueId": "observation_14",
      "erroneousValue": null,
      "conceptSetName": "History and Examination",
      "voided": false
    }
  ],
  "encounterTypeUuid": "da7a4fe0-0a6a-11e3-939c-8c50edb4be99"
}"""))
      .asJson
  }
}