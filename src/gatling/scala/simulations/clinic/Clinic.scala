package simulations.clinic

import infrastructure.Possibility
import registries.Explore

object Clinic {
  val possibilities: Seq[Possibility] = List(
    Possibility(Explore.search, 80),
    Possibility(Explore.browse, 20)
  )
}
