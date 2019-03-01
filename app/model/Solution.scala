package model

import model.points.Points

trait Solution[SolType] {

  val id: Int

}

trait SampleSolution[SolType] extends Solution[SolType] {

  def sample: SolType

}

trait UserSolution[PartType <: ExPart, SolType] extends Solution[SolType] {

  val part: PartType

  val points: Points

  val maxPoints: Points

  val solution: SolType

}
