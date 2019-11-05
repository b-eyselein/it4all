package model.tools.sql

import enumeratum.{EnumEntry, PlayEnum}
import model._
import play.twirl.api.Html

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
  id: Int, collId: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String, state: ExerciseState,
  exerciseType: SqlExerciseType, override val tags: Seq[SqlExTag], hint: Option[String], samples: Seq[StringSampleSolution]
) extends Exercise {

  override def preview: Html = // FIXME: move to toolMain!
    views.html.toolViews.sql.sqlExPreview(this)

}

// final case classes for db

final case class SqlExerciseReview(difficulty: Difficulty, maybeDuration: Option[Int]) extends ExerciseReview
