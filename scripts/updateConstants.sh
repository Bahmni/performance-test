#!/bin/bash

# Check if the correct number of arguments is provided
if [ "$#" -ne 10 ]; then
    echo "Usage: $0 <url> <provider_name> <password> <login_location_uuid> <visit_location_uuid> <patient_document_encounter_type_uuid> <radiology_encounter_type_uuid> <registration_encounter_type_uuid> <consultation_encounter_type_uuid> <provider_uuid>"
    exit 1
fi

# Assign arguments to variables
URL=$1
PROVIDER_NAME=$2
PASSWORD=$3
LOGIN_LOCATION_UUID=$4
VISIT_LOCATION_UUID=$5
PATIENT_DOCUMENT_ENCOUNTER_TYPE_UUID=$6
RADIOLOGY_ENCOUNTER_TYPE_UUID=$7
REGISTRATION_ENCOUNTER_TYPE_UUID=$8
CONSULTATION_ENCOUNTER_TYPE_UUID=$9
PROVIDER_UUID=${10}

# Path to the constants.scala file
SCALA_CONSTANTS_PATH="./../src/gatling/scala/api/Constants.scala"

# Read the contents of the constants.scala file
SCALA_CONSTANTS=$(cat $SCALA_CONSTANTS_PATH)

# Update the constants.scala file with the new values
UPDATED_SCALA_CONSTANTS=$(echo "$SCALA_CONSTANTS" |
    sed "s|val BASE_URL = \".*\"|val BASE_URL = \"$URL\"|" |
    sed "s|val LOGIN_USER = \".*\"|val LOGIN_USER = \"$PROVIDER_NAME\"|" |
    sed "s|val PASSWORD = \".*\"|val PASSWORD = \"$PASSWORD\"|" |
    sed "s|val LOGIN_LOCATION_UUID = \".*\"|val LOGIN_LOCATION_UUID = \"$LOGIN_LOCATION_UUID\"|" |
    sed "s|val VISIT_LOCATION_UUID = \".*\"|val VISIT_LOCATION_UUID = \"$VISIT_LOCATION_UUID\"|" |
    sed "s|val PATIENT_DOCUMENT_ENCOUNTER_TYPE_UUID = \".*\"|val PATIENT_DOCUMENT_ENCOUNTER_TYPE_UUID = \"$PATIENT_DOCUMENT_ENCOUNTER_TYPE_UUID\"|" |
    sed "s|val RADIOLOGY_ENCOUNTER_TYPE_UUID = \".*\"|val RADIOLOGY_ENCOUNTER_TYPE_UUID = \"$RADIOLOGY_ENCOUNTER_TYPE_UUID\"|" |
    sed "s|val REGISTRATION_ENCOUNTER_TYPE_UUID = \".*\"|val REGISTRATION_ENCOUNTER_TYPE_UUID = \"$REGISTRATION_ENCOUNTER_TYPE_UUID\"|" |
    sed "s|val CONSULTATION_ENCOUNTER_TYPE_UUID = \".*\"|val CONSULTATION_ENCOUNTER_TYPE_UUID = \"$CONSULTATION_ENCOUNTER_TYPE_UUID\"|" |
    sed "s|val PROVIDER_UUID = \".*\"|val PROVIDER_UUID = \"$PROVIDER_UUID\"|")

# Write the updated content back to the constants.scala file
echo "$UPDATED_SCALA_CONSTANTS" > $SCALA_CONSTANTS_PATH

echo "constants.scala file has been updated successfully."
