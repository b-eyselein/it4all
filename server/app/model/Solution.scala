package model

import model.tools.collectionTools.{ExPart, ExerciseFile}

@deprecated
trait UserSolution[SolType] {

  // FIXME: make case class!

  val id: Int

  val part: ExPart

  val solution: SolType

}

//final case class StringSampleSolution(
//  id: Int,
//  sample: String
//) extends SampleSolution[String]

final case class StringUserSolution(
  id: Int,
  part: ExPart,
  solution: String,
) extends UserSolution[String]


//final case class FilesSampleSolution(
//  id: Int,
//  sample: Seq[ExerciseFile]
//) extends SampleSolution[Seq[ExerciseFile]]

final case class FilesUserSolution(
  id: Int,
  part: ExPart,
  solution: Seq[ExerciseFile],
) extends UserSolution[Seq[ExerciseFile]]

