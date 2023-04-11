# Bahmni Performance Test

### [Documentation](https://bahmni.atlassian.net/wiki/spaces/BAH/pages/3038445574/Performance+Benchmarking+and+Capacity+Planning)

![Design](https://github.com/Bahmni/bahmni-diagrams/blob/main/quality-gates/performance_design_plan.png)

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
 - Get the `ec2access.pem` key from the infra admin
 - use the following command to login `ssh -i "ec2access.pem" ubuntu@***********.ap-south-1.compute.amazonaws.com`
 - Copy the scripts 📁 into the EC2 machine
 - Run this to start the test `nohup  bash ./startPerformanceTest.sh dev 40 30 minutes &` in the scripts folder
 - To view execution progress  `tail -f nohup.out`
 - To download report `scp -i "ec2access.pem" -r ubuntu@***********.ap-south-1.compute.amazonaws.com:/home/ubuntu/Bahmni/scripts/performance-test/build/reports/gatling/* ./`
 - To stop the execution `kill -9 {JAVA_PID}`

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

- Trigger is set as Manual
- The `Run Performance Test` trigger could be utilised to generate performance test report based `CUSTOM_SIMULATION`
- The workflow performs the following steps :
  - Starting the EC2 Instance
  - Building the project
  - Running performance test in the EC2 instance based on the `LOAD_SIMULATION_TYPE` or `CUSTOM_SIMULATION`
  - To start the custom simulation provide the following as inputs
      - Number of concurrent users to test - Example: 50,70,100
      - Duration of test - Example: 5,10,24
      - Units for duration - Hours or Minutes
      - Valid reportname (optional) - Example: bahmniclinic-20230330090426833
      - Enter number of patients to be created (optional) - Example: 50,100 (These patients will be created and same will be replaced in registrations.csv)
  - By default the startup script which runs before the test closes all the exisiting active patients visits  and create 60% of Number of concurrent users as new patients followed by starting the visit for them
  - Follow the steps in `AWS EC2 Execution` to view the progress and download the report

### Note 
   - `Stop Performance Test EC2 instance` this workflow can be used to stop the EC2 instance
   - `Execution in GH Machine` this workflow can be used to run the test in Github provided machine which is not recommanded due to hardware restrictions

    
  
