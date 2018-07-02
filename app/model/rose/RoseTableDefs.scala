package model.rose

import javax.inject.Inject
import model.persistence.SingleExerciseTableDefs
import model.programming.ProgDataTypes.ProgDataType
import model.programming.{ProgDataTypes, ProgLanguage, ProgLanguages}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.ast.{ScalaBaseType, TypedType}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.language.{implicitConversions, postfixOps}

class RoseTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with SingleExerciseTableDefs[RoseExercise, RoseCompleteEx, String, RoseSolution, RoseExPart] {

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
    MappedColumnType.base[ProgLanguage, String](_.entryName, ProgLanguages.withNameInsensitive)

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

  override protected implicit val solutionTypeColumnType: TypedType[String] = ScalaBaseType.stringType

  class RoseSolutionsTable(tag: Tag) extends PartSolutionsTable(tag, "rose_solutions") {

    //    def solution = column[String]("solution")


    override def * = (username, exerciseId, part, solution, points, maxPoints).mapTo[RoseSolution]

  }

}