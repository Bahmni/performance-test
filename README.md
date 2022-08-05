# Bahmni Performance Test

### [Documentation](https://bahmni.atlassian.net/wiki/spaces/BAH/pages/3038445574/Performance+Benchmarking+and+Capacity+Planning)
![Design](https://raw.githubusercontent.com/Bahmni/bahmni-diagrams/main/quality-gates/performance_test_design.png)

### Local Execution

Simulations can be run locally using Gradle. `./gradlew gatlingRun`
> Note: By default the simulation would run for Standard traffic. Please set `LOAD_SIMULATION_TYPE` env variable to `High` or `Peak`
 to simulate other traffic conditions

**Standard**
`export LOAD_SIMULATION_TYPE='Standard' && ./gradlew gatlingRun` \
**High** `export LOAD_SIMULATION_TYPE='High' && ./gradlew gatlingRun` \
**Peak** `export LOAD_SIMULATION_TYPE='Peak' && ./gradlew gatlingRun`

### Stack
* JDK 11
* Scala 2.13
* [Gatling](https://github.com/gatling/gatling/blob/master/LICENSE.txt) - _Async Scala-Akka-Netty based_ -
* Gradle Wrapper is already packaged, so you don't need to install Gradle (a JDK > 11 must be installed and $JAVA_HOME configured)
* Minimal `build.gradle` leveraging Gradle wrapper and `io.gatling.gradle` plugin

### Folder layout

> 📁 **api** 
- All HTTP requests responsible to make API calls to OMRS

> 📁 **configurations**
- `Possibility` : Base object to represent possible variants of various User flows
- `Protocol` : http protocol for running simulations
- `TrafficConfiguration` : Contains 3 configurable load configurations such as Standard, High and Peak
- `Feeders` : csv data Feeders

> 📁 **registries** 
- Domain object registry that represents various functions performed by the user

> 📁 **scenarios**
- Closed System for simulating constant traffic of concurrent users
```scala
// Simulate 10 concurrent users traffic for 5 minutes with initial ramp of 30 seconds 
rampConcurrentUsers(0).to(10).during(30 seconds),
constantConcurrentUsers(10).during(5 minutes)
```

> 📁 **simulations**

- Current suit supports **Bhamni Clinic** simulation
- Following `closed system` concurrent user configurations are defined in `TrafficConfiguration`

| Load Type | Concurrent Users | Duration   | Initial Ramp Duration |  
|-----------|------------------|------------|-----------------------|
| STANDARD  | 40               | 3 minutes  | 30 seconds            |
| HIGH      | 50               | 5 minutes  | 30 seconds            |
| PEAK      | 70               | 10 minutes | 30 seconds            |

To get an idea how this behaves, have a look at this visual representation of a capacity load test:
