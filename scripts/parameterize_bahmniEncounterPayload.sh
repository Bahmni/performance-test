#!/bin/bash

# Function to update JSON values
update_json() {
  local json_file=$1
  local encounter_type_uuid=$2
  local location_uuid=$3
  local patient_uuid=$4
  local provider_uuid=$5

  if [ ! -f "$json_file" ]; then
    echo "Error: File '$json_file' does not exist."
    return 1
  fi

  # Read JSON file
  json=$(cat "$json_file")

  # Update JSON values
  updated_json=$(echo "$json" | jq --arg encounterTypeUuid "$encounter_type_uuid" \
                                   --arg locationUuid "$location_uuid" \
                                   --arg patientUuid "$patient_uuid" \
                                   --arg providerUuid "$provider_uuid" \
                                   '.encounterTypeUuid = $encounterTypeUuid |
                                    .locationUuid = $locationUuid |
                                    .patientUuid = $patientUuid |
                                    .providers[0].uuid = $providerUuid')

  # Save updated JSON back to the same file
  echo "$updated_json" > "$json_file"
  echo "Updated JSON has been saved to $json_file"
}

# Parameters
JSON_FILE_EXISTING_PATIENT="./../src/gatling/resources/bodies/encounter_existing_patient_new_visit.json"
JSON_FILE_NEW_PATIENT="./../src/gatling/resources/bodies/encounter_new_patient_new_visit.json"
ENCOUNTER_TYPE_UUID="#{encounterTypeUuid}"
LOCATION_UUID="#{locationUuid}"
PATIENT_UUID="#{opdPatientId}"
PROVIDER_UUID="#{providerUuid}"

# Call the function and overwrite the original JSON file
update_json "$JSON_FILE_EXISTING_PATIENT" "$ENCOUNTER_TYPE_UUID" "$LOCATION_UUID" "$PATIENT_UUID" "$PROVIDER_UUID"
update_json "$JSON_FILE_NEW_PATIENT" "$ENCOUNTER_TYPE_UUID" "$LOCATION_UUID" "$PATIENT_UUID" "$PROVIDER_UUID"
