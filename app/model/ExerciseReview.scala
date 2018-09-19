package model

trait ExerciseReview[PartType <: ExPart] {

  val username: String

  val exerciseId: Int

  val exerciseSemVer: SemanticVersion

  val exercisePart: PartType

  val difficulty: Difficulty

  val maybeDuration: Option[Int]

}
