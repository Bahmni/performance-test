Performance Test
===============

This repository is for Bahmni performance test.

1. Clone the performance test repo.
2. Setup appropriate scenario and timings in UserSimulation.scala.The given scenario in the simulation files are for examples only and should not be used right away.
According to the test type you need to comment out the test scenarios which are not needed. Keep the scenarios uncommented which are intended for the tests.
<br>
Update injection values such as duration, maxDuration, rampUsers, incrementUsersPerSec etc. according to spike or load you want to create.
3. To run the test from command line, execute "mvn gatling:test" command.
4. To run from Intellij IDE, run the Engine.scala file.

Setup
=========

* Install JDK 8.
* Scala version need to set at 2.12 in IDE.