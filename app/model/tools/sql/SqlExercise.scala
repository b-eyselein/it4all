package model.tools.sql

import enumeratum.{EnumEntry, PlayEnum}
import model._
import model.points.Points
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

final case class SqlExercise(id: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String, state: ExerciseState,
                             exerciseType: SqlExerciseType, override val tags: Seq[SqlExTag], hint: Option[String], samples: Seq[SqlSampleSolution]) extends Exercise {

  override def baseValues: BaseValues = BaseValues(id, semanticVersion, title, author, text, state)

  // other methods

  override def preview: Html = // FIXME: move to toolMain!
    views.html.toolViews.sql.sqlExPreview(this)

}

// final case classes for db

final case class SqlScenario(id: Int, title: String, author: String, text: String, state: ExerciseState, shortName: String)
  extends ExerciseCollection {

  val imageUrl: String = shortName + ".png"

}

final case class SqlSampleSolution(id: Int, sample: String)
  extends SampleSolution[String]

final case class SqlUserSolution(id: Int, part: SqlExPart, solution: String, points: Points, maxPoints: Points)
  extends UserSolution[SqlExPart, String]


final case class SqlExerciseReview(difficulty: Difficulty, maybeDuration: Option[Int]) extends ExerciseReview
