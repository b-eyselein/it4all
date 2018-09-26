package model

trait SampleSolution[SolType] {

  val id: Int

  val exerciseId: Int

  val exSemVer: SemanticVersion

  def sample: SolType

}

trait UserSolution[SolType] {

  val username: String

  val exerciseId: Int

  val exSemVer: SemanticVersion

  val points: Points

  val maxPoints: Points

  val solution: SolType

}

trait DBPartSolution[PartType <: ExPart, SolType] extends UserSolution[SolType] {

  val part: PartType

}

trait CollectionExSolution[SolType] extends UserSolution[SolType] {

  val collectionId: Int

  val collSemVer: SemanticVersion

}