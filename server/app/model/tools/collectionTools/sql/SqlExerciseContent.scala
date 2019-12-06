package model.tools.collectionTools.sql

import enumeratum.{EnumEntry, PlayEnum}
import model.tools.collectionTools.{SampleSolution, StringExerciseContent}

import scala.collection.immutable.IndexedSeq

sealed trait SqlExerciseType extends EnumEntry

object SqlExerciseType extends PlayEnum[SqlExerciseType] {

  override def values: IndexedSeq[SqlExerciseType] = findValues

  case object SELECT extends SqlExerciseType

  case object CREATE extends SqlExerciseType

  case object UPDATE extends SqlExerciseType

  case object INSERT extends SqlExerciseType

  case object DELETE extends SqlExerciseType

}

// Classes for use

final case class SqlExerciseContent(
  exerciseType: SqlExerciseType,
  //  override val tags: Seq[SqlExerciseTag],
  hint: Option[String],
  sampleSolutions: Seq[SampleSolution[String]]
) extends StringExerciseContent
