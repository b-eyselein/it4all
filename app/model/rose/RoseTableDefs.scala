package model.rose

import javax.inject.Inject
import model.persistence.SingleExerciseTableDefs
import model.programming.ProgDataTypes.ProgDataType
import model.programming.{ProgDataTypes, ProgLanguage}
import model.{ExerciseState, _}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.language.{implicitConversions, postfixOps}

// Classes for use

case class RoseCompleteEx(ex: RoseExercise, inputType: Seq[RoseInputType], sampleSolution: Seq[RoseSampleSolution]) extends PartsCompleteEx[RoseExercise, RoseExPart] {

  val NewLine = "\n"

  override def preview: Html = // FIXME: move to toolMain!
    views.html.idExercises.rose.rosePreview.render(this)

  override def hasPart(partType: RoseExPart): Boolean = true

  def declaration(forUser: Boolean): String = {
    val className = if (forUser) "UserRobot" else "SampleRobot"
    val (actorClass, methodName, returnType) = if (ex.isMultiplayer) ("MultiPlayerActor", "act", "Action") else ("SinglePlayerActor", "run", "None")

    val parameters = inputType match {
      case Nil => ""
      case other => ", " + (other map (it => it.name + ": " + it.inputType.typeName) mkString ", ")
    }

    s"""class $className(Robot, $actorClass):
       |  def $methodName(self$parameters) -> $returnType:""".stripMargin
  }

  def imports: String = if (ex.isMultiplayer) {
    """from typing import Dict
      |from base.actors import MultiPlayerActor
      |from base.actions import *
      |from base.robot import Robot""".stripMargin
  } else {
    """from typing import Dict
      |from base.actors import SinglePlayerActor
      |from base.actions import *
      |from base.robot import Robot""".stripMargin
  }

  def buildSampleSolution(language: ProgLanguage): String = {
    val sampleSol = sampleSolution.find(_.language == language) map (_.solution) getOrElse ""

    declaration(false) + NewLine + sampleSol.split(NewLine).map(" " * 4 + _).mkString(NewLine)
  }

}

// Case classes for tables

case class RoseExercise(id: Int, title: String, author: String, text: String, state: ExerciseState, fieldWidth: Int, fieldHeight: Int, isMultiplayer: Boolean) extends Exercise {

  def this(baseValues: (Int, String, String, String, ExerciseState), fieldWidth: Int, fieldHeight: Int, isMultiplayer: Boolean) =
    this(baseValues._1, baseValues._2, baseValues._3, baseValues._4, baseValues._5, fieldWidth, fieldHeight, isMultiplayer)

}

case class RoseInputType(id: Int, exerciseId: Int, name: String, inputType: ProgDataType)

case class RoseSampleSolution(exerciseId: Int, language: ProgLanguage, solution: String)

case class RoseSolution(username: String, exerciseId: Int, part: RoseExPart, solution: String) extends PartSolution[RoseExPart]


// Tables

class RoseTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with SingleExerciseTableDefs[RoseExercise, RoseCompleteEx, RoseSolution, RoseExPart] {

  import profile.api._

  // Abstract types

  override protected type ExTableDef = RoseExercisesTable

  override protected type SolTableDef = RoseSolutionsTable

  // Table Queries

  override protected val exTable = TableQuery[RoseExercisesTable]

  override protected val solTable = TableQuery[RoseSolutionsTable]

  val roseInputs = TableQuery[RoseInputTypesTable]

  val roseSamples = TableQuery[RoseSampleSolutionsTable]

  val roseSolutions = TableQuery[RoseSolutionsTable]

  // Queries

  override protected def completeExForEx(ex: RoseExercise): Future[RoseCompleteEx] = for {
    inputTypes <- db.run(roseInputs.filter(_.exerciseId === ex.id).result)
    samples <- db.run(roseSamples.filter(_.exerciseId === ex.id).result)
  } yield RoseCompleteEx(ex, inputTypes, samples)

  override protected def saveExerciseRest(compEx: RoseCompleteEx): Future[Boolean] = for {
    inputsSaved <- saveSeq[RoseInputType](compEx.inputType, it => db.run(roseInputs insertOrUpdate it))
    samplesSaved <- saveSeq[RoseSampleSolution](compEx.sampleSolution, rss => db.run(roseSamples insertOrUpdate rss))
  } yield inputsSaved && samplesSaved

  // Implicit column types

  implicit val ProgLanguageColumnType: BaseColumnType[ProgLanguage] =
    MappedColumnType.base[ProgLanguage, String](_.name, str => ProgLanguage.valueOf(str) getOrElse ProgLanguage.STANDARD_LANG)

  implicit val ProgDataTypesColumnType: BaseColumnType[ProgDataType] =
    MappedColumnType.base[ProgDataType, String](_.typeName, str => ProgDataTypes.byName(str) getOrElse ProgDataTypes.STRING)

  override protected implicit val partTypeColumnType: BaseColumnType[RoseExPart] =
    MappedColumnType.base[RoseExPart, String](_.entryName, RoseExParts.withNameInsensitive)

  // Tables

  class RoseExercisesTable(tag: Tag) extends HasBaseValuesTable[RoseExercise](tag, "rose_exercises") {

    def fieldWidth = column[Int]("field_width")

    def fieldHeight = column[Int]("field_height")

    def isMultiplayer = column[Boolean]("is_mp")


    def pk = primaryKey("pk", id)


    override def * = (id, title, author, text, state, fieldWidth, fieldHeight, isMultiplayer) <> (RoseExercise.tupled, RoseExercise.unapply)

  }

  class RoseInputTypesTable(tag: Tag) extends Table[RoseInputType](tag, "rose_inputs") {

    def id = column[Int]("id")

    def exerciseId = column[Int]("exercise_id")

    def name = column[String]("input_name")

    def inputType = column[ProgDataType]("input_type")


    def pk = primaryKey("pk", (id, exerciseId))

    def exerciseFk = foreignKey("exercise_fk", exerciseId, exTable)(_.id)


    override def * = (id, exerciseId, name, inputType) <> (RoseInputType.tupled, RoseInputType.unapply)

  }

  class RoseSampleSolutionsTable(tag: Tag) extends Table[RoseSampleSolution](tag, "rose_samples") {

    def exerciseId = column[Int]("exercise_id")

    def language = column[ProgLanguage]("language")

    def solution = column[String]("solution")


    def pk = primaryKey("pk", (exerciseId, language))

    def exerciseFk = foreignKey("exercise_fk", exerciseId, exTable)(_.id)


    override def * = (exerciseId, language, solution) <> (RoseSampleSolution.tupled, RoseSampleSolution.unapply)

  }

  // Solutions of users

  class RoseSolutionsTable(tag: Tag) extends PartSolutionsTable[RoseSolution](tag, "rose_solutions") {

    def solution = column[String]("solution")


    override def * = (username, exerciseId, part, solution) <> (RoseSolution.tupled, RoseSolution.unapply)

  }

}