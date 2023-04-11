# How to use script:
# ./${script_name} <simulationType> <activeUsers> <duration> <units>

simulationType="$1"
users="$2"
duration="$3"
units="$4"
now=$(date)
echo "Execution started at : "$now
if [[ $simulationType -eq "dev" ]] 
then
    export LOAD_SIMULATION_TYPE=$simulationType
    export ACTIVE_USERS=$users
    export DURATION=$duration
    export UNITS=$units 
else
    export LOAD_SIMULATION_TYPE=$simulationType
fi

echo "ENV VARIABLES"
echo "Simulation type : "$LOAD_SIMULATION_TYPE
echo "Active users : "$ACTIVE_USERS
echo "Duration : "$DURATION
echo "Units : "$UNITS

patientsCount=$((ACTIVE_USERS*60/100))
echo "<---------Close Patient Visits Started--------->"
newman run -e env.json closeVisits.json
echo "<---------Close Patient Visits Ended--------->"
echo "<---------Create Patient and Start Visit Started--------->"
newman run -e env.json startVisits.json --env-var patientsCount=$patientsCount
echo "<---------Create Patient and Start Visit Ended--------->"
DIR="performance-test"
if [ -d "$DIR" ] && [ ! -L "$DIR" ]; then
   echo "Source code is present"
   cd $DIR && git switch master && git pull -r
else
    echo "Source code is not present"
    git clone https://github.com/Bahmni/performance-test.git
    cd $DIR && git switch master
fi

git status

./gradlew gatlingRun --no-build-cache
