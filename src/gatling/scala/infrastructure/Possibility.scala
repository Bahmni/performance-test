package infrastructure

import io.gatling.core.structure.ChainBuilder

import scala.language.implicitConversions

case class Possibility(
    createChain: MaximumResponseTimes => ChainBuilder,
    weight: Double
)

case class PossibilityList(possibilities: List[Possibility])
object PossibilityList {
  implicit def fromList(list: List[Possibility]): PossibilityList =
    PossibilityList(list)
}

object Possibilities {
  def balance(
      load: TrafficLoadConfiguration,
      list: PossibilityList
  ): List[(Double, ChainBuilder)] = {

    val (totalWeight, possibilities) = list.possibilities
      .foldLeft[(Double, List[Possibility])](0, Nil) { (result, chain) =>
        (result._1 + chain.weight, result._2 :+ chain)
      }

    val balancedPossibilities = possibilities.map(possibility => {
      val weight =
        if (totalWeight == 0) possibility.weight
        else possibility.weight / totalWeight * 100
      (weight, possibility.createChain(load.responseTimes))
    })

    balancedPossibilities
  }
}
