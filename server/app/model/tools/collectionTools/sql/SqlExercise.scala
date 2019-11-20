package model.tools.collectionTools.sql

import enumeratum.{EnumEntry, PlayEnum}
import model._
import model.persistence.ADbExercise

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

final case class SqlExercise(
  id: Int, collectionId: Int, toolId: String = SqlConsts.toolId, semanticVersion: SemanticVersion,
  title: String, author: String, text: LongText, state: ExerciseState,
  exerciseType: SqlExerciseType,
  override val tags: Seq[SqlExerciseTag],
  hint: Option[String],
  sampleSolutions: Seq[StringSampleSolution]
) extends Exercise
  with ADbExercise {

  override protected type SolutionType = String
  override protected type SampleSolutionType = StringSampleSolution

}

// final case classes for db

final case class SqlExerciseReview(difficulty: Difficulty, maybeDuration: Option[Int]) extends ExerciseReview
