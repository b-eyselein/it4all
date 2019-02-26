package model

trait Solution[SolType] {

  val id: Int

  val exerciseId: Int

  val exSemVer: SemanticVersion

  val collectionId: Int = -1

  val collSemVer: SemanticVersion = SemanticVersionHelper.DEFAULT

}

trait SampleSolution[SolType] extends Solution[SolType] {

  def sample: SolType

}

trait UserSolution[PartType <: ExPart, SolType] extends Solution[SolType] {

  val username: String

  val part: PartType

  val points: Points

  val maxPoints: Points

  val solution: SolType

}
