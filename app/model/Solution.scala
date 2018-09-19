package model

trait Solution[SolType] {

  val username: String

  val exerciseId: Int

  val exSemVer: SemanticVersion

  val points: Points

  val maxPoints: Points

  val solution: SolType

}

trait DBPartSolution[PartType <: ExPart, SolType] extends Solution[SolType] {

  val part: PartType

}

trait CollectionExSolution[SolType] extends Solution[SolType] {

  val collectionId: Int

  val collSemVer: SemanticVersion

}