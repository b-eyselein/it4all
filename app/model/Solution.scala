package model

trait SampleSolution[SolType] {

  val id: Int

  val exerciseId: Int

  val exSemVer: SemanticVersion

  def sample: SolType

}

trait UserSolution[PartType <: ExPart, SolType] {

  val id: Int

  val username: String

  val exerciseId: Int

  val exSemVer: SemanticVersion

  val collectionId: Int = -1

  val collSemVer: SemanticVersion = SemanticVersionHelper.DEFAULT

  val part: PartType

  val points: Points

  val maxPoints: Points

  val solution: SolType

}
