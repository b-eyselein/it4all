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


final case class StringSampleSolution(id: Int, sample: String) extends SampleSolution[String]

final case class StringUserSolution[PartType <: ExPart](id: Int, part: PartType, solution: String, points: Points, maxPoints: Points)
  extends UserSolution[PartType, String]


final case class FilesSampleSolution(id: Int, sample: Seq[ExerciseFile]) extends SampleSolution[Seq[ExerciseFile]]

final case class FilesUserSolution[PartType <: ExPart](id: Int, part: PartType, solution: Seq[ExerciseFile], points: Points, maxPoints: Points)
  extends UserSolution[PartType, Seq[ExerciseFile]]
