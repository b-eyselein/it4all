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

case class SqlCompleteScenario(override val coll: SqlScenario, override val exercises: Seq[SqlCompleteEx]) extends CompleteCollection {

  override type Ex = SqlExercise

  override type CompEx = SqlCompleteEx

  override type Coll = SqlScenario

  override def exercisesWithFilter(filter: String): Seq[SqlCompleteEx] = SqlExerciseType.withNameInsensitiveOption(filter) map (exType => getExercisesByType(exType)) getOrElse Seq.empty

  def getExercisesByType(exType: SqlExerciseType): Seq[SqlCompleteEx] = exercises filter (_.ex.exerciseType == exType)

  override def renderRest: Html = new Html(
    s"""<div class="row">
       |  <div class="col-md-2"><b>Kurzname</b></div>
       |  <div class="col-md-4">${coll.shortName}</div>
       |</div>""".stripMargin)

}

case class SqlCompleteEx(ex: SqlExercise, samples: Seq[SqlSample]) extends CompleteExInColl[SqlExercise] {

  override def tags: Seq[SqlExTag] = (ex.tags split SqlConsts.tagJoinChar).toSeq flatMap SqlExTag.withNameInsensitiveOption

  override def preview: Html = // FIXME: move to toolMain!
    views.html.collectionExercises.sql.sqlExPreview(this)

}

// case classes for db

case class SqlScenario(id: Int, title: String, author: String, text: String, state: ExerciseState, shortName: String) extends ExerciseCollection[SqlExercise, SqlCompleteEx] {

  def this(baseValues: (Int, String, String, String, ExerciseState), shortName: String) =
    this(baseValues._1, baseValues._2, baseValues._3, baseValues._4, baseValues._5, shortName)

  val imageUrl: String = shortName + ".png"

}

case class SqlExercise(id: Int, title: String, author: String, text: String, state: ExerciseState,
                       collectionId: Int, exerciseType: SqlExerciseType, tags: String, hint: Option[String]) extends ExInColl {

  def this(baseValues: (Int, String, String, String, ExerciseState), collectionId: Int, exerciseType: SqlExerciseType, tags: String, hint: Option[String]) =
    this(baseValues._1, baseValues._2, baseValues._3, baseValues._4, baseValues._5, collectionId, exerciseType, tags, hint)

}

case class SqlSample(id: Int, exerciseId: Int, scenarioId: Int, sample: String)

case class SqlSolution(username: String, collectionId: Int, exerciseId: Int, solution: String, points: Double, maxPoints: Double) extends CollectionExSolution[String]

