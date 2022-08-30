package scenarios

import configurations.{Load, MaximumResponseTimes, Possibility}
import io.gatling.core.Predef._
import io.gatling.core.structure.PopulationBuilder
import registries.Common._
import registries.Doctor._
import scenarios.BaseScenario.setupScenario

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object Consultation {
  private val possibilities = List(
    Possibility(doctor_Consultation_Average_Patient, 100)
  )

  def scenario(loadSharePercentage: Int): PopulationBuilder =
    setupScenario("Consultation", Load.getTrafficShareConfiguration(loadSharePercentage), possibilities)

  def doctor_Consultation_Average_Patient(expectedResTimes: MaximumResponseTimes) ={
    exec(login)
      .exec(goToHomePage)
      .pause(10 seconds, 20 seconds)
      .exec(goToClinicalApp)
      .exec(goToClinicalSearch())
      .exec(goToDashboard("#{opdPatientId}"))
      .exec(setSession())
      .exec(setVisit("#{opdPatientId}"))
  }

}
