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

final case class SqlCompleteScenario(override val coll: SqlScenario, override val exercises: Seq[SqlExercise]) extends CompleteCollection {

  override type CompEx = SqlExercise

  override type Coll = SqlScenario

  override def exercisesWithFilter(filter: String): Seq[SqlExercise] = SqlExerciseType.withNameInsensitiveOption(filter) map (exType => getExercisesByType(exType)) getOrElse Seq[SqlExercise]()

  def getExercisesByType(exType: SqlExerciseType): Seq[SqlExercise] = exercises filter (_.exerciseType == exType)

  override def renderRest: Html = new Html(
    s"""<div class="row">
       |  <div class="col-md-2"><b>Kurzname</b></div>
       |  <div class="col-md-4">${coll.shortName}</div>
       |</div>""".stripMargin)

}

final case class SqlExercise(id: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String, state: ExerciseState,
                             collectionId: Int, collSemVer: SemanticVersion, exerciseType: SqlExerciseType, override val tags: Seq[SqlExTag],
                             hint: Option[String], samples: Seq[SqlSample]) extends CollectionExercise {

  override def baseValues: BaseValues = BaseValues(id, semanticVersion, title, author, text, state)

  // other methods

  override def preview: Html = // FIXME: move to toolMain!
    views.html.collectionExercises.sql.sqlExPreview(this)

}

// final case classes for db

final case class SqlScenario(id: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String, state: ExerciseState,
                             shortName: String) extends ExerciseCollection[SqlExercise] {

  val imageUrl: String = shortName + ".png"

}

final case class SqlSample(id: Int, exerciseId: Int, exSemVer: SemanticVersion, collId: Int, collSemVer: SemanticVersion, sample: String)

final case class SqlSolution(id: Int, username: String, exerciseId: Int, exSemVer: SemanticVersion, collectionId: Int, collSemVer: SemanticVersion,
                             solution: String, points: Points, maxPoints: Points) extends CollectionExSolution[String]

