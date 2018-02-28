package model.rose

import javax.inject.Inject
import model.Enums.ExerciseState
import model._
import model.persistence.SingleExerciseTableDefs
import model.programming.ProgDataTypes.ProgDataType
import model.programming.{ProgDataTypes, ProgLanguage}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.language.{implicitConversions, postfixOps}

// Wrapper classes

class RoseCompleteExWrapper(override val compEx: RoseCompleteEx) extends CompleteExWrapper {

  override type Ex = RoseExercise

  override type CompEx = RoseCompleteEx

}

// Classes for user

case class RoseCompleteEx(ex: RoseExercise, inputType: Seq[RoseInputType], sampleSolution: RoseSampleSolution) extends PartsCompleteEx[RoseExercise, RoseExPart] {

  // FIXME: only one solution?

  val NewLine = "\n"

  override def preview: Html = views.html.rose.rosePreview.render(this)

  override def hasPart(partType: RoseExPart): Boolean = true

  def declaration(forUser: Boolean): String = {
    val className = if (forUser) "UserRobot" else "SampleRobot"
    val (actorClass, methodName, returnType) = if (ex.isMultiplayer) ("MultiPlayerActor", "act", "Action") else ("SinglePlayerActor", "run", "None")

    val parameters = inputType match {
      case Nil   => ""
      case other => ", " + (other map (it => it.name + ": " + it.inputType.typeName) mkString ", ")
    }

    s"""class $className(Robot, $actorClass):
       |  def $methodName(self$parameters) -> $returnType:
       |    pass""".stripMargin
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

  def buildSampleSolution: String = declaration(false) + NewLine + sampleSolution.solution.split(NewLine).map(" " * 4 + _).mkString(NewLine)

  override def wrapped: CompleteExWrapper = new RoseCompleteExWrapper(this)

}

// Case classes for tables

case class RoseExercise(id: Int, title: String, author: String, text: String, state: ExerciseState, isMultiplayer: Boolean) extends Exercise {

  def this(baseValues: (Int, String, String, String, ExerciseState), isMultiplayer: Boolean) =
    this(baseValues._1, baseValues._2, baseValues._3, baseValues._4, baseValues._5, isMultiplayer)

}

case class RoseInputType(id: Int, exerciseId: Int, name: String, inputType: ProgDataType)

case class RoseSampleSolution(exerciseId: Int, language: ProgLanguage, solution: String)

// Dependent on users and roseExercises

case class RoseSolution(username: String, exerciseId: Int, solution: String) extends Solution

// Tables

class RoseTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] with SingleExerciseTableDefs[RoseExercise, RoseCompleteEx, RoseSolution, RoseExPart] {

  import profile.api._

  // Abstract types

  override type ExTableDef = RoseExercisesTable

  override type SolTableDef = RoseSolutionsTable

  // Table Queries

  override val exTable = TableQuery[RoseExercisesTable]

  override val solTable = TableQuery[RoseSolutionsTable]

  val roseInputs = TableQuery[RoseInputTypesTable]

  val roseSamples = TableQuery[RoseSampleSolutionsTable]

  val roseSolutions = TableQuery[RoseSolutionsTable]

  // Queries

  override protected def completeExForEx(ex: RoseExercise)(implicit ec: ExecutionContext): Future[RoseCompleteEx] = {
    val values: Future[(Seq[RoseInputType], RoseSampleSolution)] = for {
      inputTypes <- db.run(roseInputs.filter(_.exerciseId === ex.id).result)
      samples <- db.run(roseSamples.filter(_.exerciseId === ex.id).result.head)
    } yield (inputTypes, samples)

    values map {
      case (inputTypes, samples) => RoseCompleteEx(ex, inputTypes, samples)
    }
  }

  override protected def saveExerciseRest(compEx: RoseCompleteEx)(implicit ec: ExecutionContext): Future[Boolean] = for {
    inputsSaved <- saveSeq[RoseInputType](compEx.inputType, it => db.run(roseInputs insertOrUpdate it))
    sampleSaved <- db.run(roseSamples insertOrUpdate compEx.sampleSolution) map (_ => true) recover { case _ => false }
  } yield inputsSaved && sampleSaved

  def saveSolution(solution: RoseSolution)(implicit ec: ExecutionContext): Future[Boolean] =
    db.run(roseSolutions insertOrUpdate solution) map (_ => true) recover { case _ => false }

  def loadSolution(username: String, exerciseId: Int): Future[Option[String]] =
    db.run(roseSolutions.filter(sol => sol.username === username && sol.exerciseId === exerciseId).map(_.solution).result.headOption)

  // Implicit column types

  implicit val ProgLanguageColumnType: BaseColumnType[ProgLanguage] =
    MappedColumnType.base[ProgLanguage, String](_.name, str => ProgLanguage.valueOf(str) getOrElse ProgLanguage.STANDARD_LANG)

  implicit val ProgDataTypesColumnType: BaseColumnType[ProgDataType] =
    MappedColumnType.base[ProgDataType, String](_.typeName, str => ProgDataTypes.byName(str) getOrElse ProgDataTypes.STRING)

  // Tables

  class RoseExercisesTable(tag: Tag) extends HasBaseValuesTable[RoseExercise](tag, "rose_exercises") {

    def isMultiplayer = column[Boolean]("is_mp")


    def pk = primaryKey("pk", id)


    def * = (id, title, author, text, state, isMultiplayer) <> (RoseExercise.tupled, RoseExercise.unapply)

  }

  class RoseInputTypesTable(tag: Tag) extends Table[RoseInputType](tag, "rose_inputs") {

    def id = column[Int]("id")

    def exerciseId = column[Int]("exercise_id")

    def name = column[String]("input_name")

    def inputType = column[ProgDataType]("input_type")


    def pk = primaryKey("pk", (id, exerciseId))

    def exerciseFk = foreignKey("exercise_fk", exerciseId, exTable)(_.id)


    def * = (id, exerciseId, name, inputType) <> (RoseInputType.tupled, RoseInputType.unapply)

  }

  class RoseSampleSolutionsTable(tag: Tag) extends Table[RoseSampleSolution](tag, "rose_samples") {

    def exerciseId = column[Int]("exercise_id")

    def language = column[ProgLanguage]("language")

    def solution = column[String]("solution")


    def pk = primaryKey("pk", (exerciseId, language))

    def exerciseFk = foreignKey("exercise_fk", exerciseId, exTable)(_.id)


    def * = (exerciseId, language, solution) <> (RoseSampleSolution.tupled, RoseSampleSolution.unapply)

  }


  // Solutions of users

  class RoseSolutionsTable(tag: Tag) extends SolutionsTable[RoseSolution](tag, "rose_solutions") {

    def solution = column[String]("solution")


    def * = (username, exerciseId, solution) <> (RoseSolution.tupled, RoseSolution.unapply)

  }

}