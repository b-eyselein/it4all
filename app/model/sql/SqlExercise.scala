package model.sql

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

final case class SqlExercise(id: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String, state: ExerciseState,
                             override val collectionId: Int, exerciseType: SqlExerciseType,
                             override val tags: Seq[SqlExTag], hint: Option[String], samples: Seq[SqlSample]) extends Exercise {

  override def baseValues: BaseValues = BaseValues(id, semanticVersion, title, author, text, state)

  // other methods

  override def preview: Html = // FIXME: move to toolMain!
    views.html.collectionExercises.sql.sqlExPreview(this)

}

// final case classes for db

final case class SqlScenario(id: Int, title: String, author: String, text: String, state: ExerciseState, shortName: String)
  extends ExerciseCollection {

  val imageUrl: String = shortName + ".png"

}

final case class SqlSample(id: Int, exerciseId: Int, exSemVer: SemanticVersion, override val collectionId: Int, sample: String)
  extends SampleSolution[String]

final case class SqlSolution(id: Int, username: String, exerciseId: Int, exSemVer: SemanticVersion,
                             override val collectionId: Int, part: SqlExPart,
                             solution: String, points: Points, maxPoints: Points) extends UserSolution[SqlExPart, String]

