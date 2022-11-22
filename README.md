# Bahmni Performance Test

### [Documentation](https://bahmni.atlassian.net/wiki/spaces/BAH/pages/3038445574/Performance+Benchmarking+and+Capacity+Planning)

![Design](https://raw.githubusercontent.com/Bahmni/bahmni-diagrams/main/quality-gates/performance_test_design.png)

### Local Execution

Simulations can be run locally using Gradle. `./gradlew gatlingRun`

> Note: By default the simulation would run for Standard traffic. Please set `LOAD_SIMULATION_TYPE` env variable to `High` or `Peak` to simulate other traffic conditions and to `dev` to run with dynamic load conditions

**Pre-requisites**
 - Ensure at-least 50 patients are active in OPD
 - Clone the repository and navigate to root directory
 
**IDE Execution**
`export LOAD_SIMULATION_TYPE='Standard' && ./gradlew gatlingRun` \
**High** `export LOAD_SIMULATION_TYPE='High' && ./gradlew gatlingRun` \
**Peak** `export LOAD_SIMULATION_TYPE='Peak' && ./gradlew gatlingRun` \
**dev**  `export LOAD_SIMULATION_TYPE=dev ACTIVE_USERS=40 DURATION=10 UNITS=minutes && ./gradlew gatlingRun`

**AWS EC2 Execution**
 - `nohup  bash -c 'export LOAD_SIMULATION_TYPE=dev ACTIVE_USERS=40 DURATION=10 UNITS=minutes && ./gradlew gatlingRun' &`
 - save the PID
 - To stop the execution `kill -9 PID`

### Stack

- JDK 11
- Scala 2.13
- [Gatling](https://github.com/gatling/gatling/blob/master/LICENSE.txt) - _Async Scala-Akka-Netty based_ -
- Gradle Wrapper is already packaged, so you don't need to install Gradle (a JDK > 11 must be installed and $JAVA_HOME configured)
- Minimal `build.gradle` leveraging Gradle wrapper and `io.gatling.gradle` plugin

### Folder layout

> 📁 **api**

- All HTTP requests responsible to make API calls to OMRS

> 📁 **configurations**

- `Possibility` : Base object to represent possible variants of various User flows
- `Protocol` : http protocol for running simulations
- `TrafficConfiguration` : Contains 4 configurable load configurations such as Standard, High , Peak and dev
- `Feeders` : csv data Feeders

> 📁 **registries**

- Domain object registry that represents various functions performed by the user

> 📁 **scenarios**

- Patient count will be calculated based on pace for each user and total duration
- workload will be created for each user based on patient count

- Closed System for simulating constant traffic of concurrent users

```scala
// Simulate 10 concurrent users traffic for 5 minutes with initial ramp of 30 seconds
rampConcurrentUsers(0).to(10).during(30 seconds),
constantConcurrentUsers(10).during(5 minutes)
```

> 📁 **simulations**

- Current suit supports **Bhamni Clinic** simulation
- Following `closed system` concurrent user configurations are defined in `TrafficConfiguration`

| Load Type | Concurrent Users | Duration     | Initial Ramp Duration |
| --------- | ---------------- | ----------   | --------------------- |
| STANDARD  | 40               | 1 hour       | 60 seconds            |
| HIGH      | 50               | 1 hour       | 60 seconds            |
| PEAK      | 70               | 1 hour       | 60 seconds            |
| dev       | env variable     | env variable | 10% of Duration       |

To get an idea how this behaves, have a look at this visual representation of a capacity load test:

### Workflow

- Gets triggered on push to master branch.
- The `workflow-dispatch` trigger could be utilised to generate performance test report based on suitable `LOAD_SIMULATION_TYPE`.
- The workflow performs the following steps :
  - Building the project
  - Running performance test based on the `LOAD_SIMULATION_TYPE`
  - Removing older reports (keeps hold of latest 10 reports)
  - Publishing reports
  - Uploading the artifact (latest performance report) and
  - Posting Slack notification containing Success/Failure message and link to the published report on successful workflow.
