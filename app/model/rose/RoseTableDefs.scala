package model.rose

import javax.inject.Inject
import model.SemanticVersion
import model.persistence.SingleExerciseTableDefs
import model.programming.ProgDataTypes.ProgDataType
import model.programming.{ProgDataTypes, ProgLanguage, ProgLanguages}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
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

  class RoseExercisesTable(tag: Tag) extends ExerciseTableDef(tag, "rose_exercises") {

    def fieldWidth = column[Int]("field_width")

    def fieldHeight = column[Int]("field_height")

    def isMultiplayer = column[Boolean]("is_mp")


    override def * = (id, semanticVersion, title, author, text, state, fieldWidth, fieldHeight, isMultiplayer).mapTo[RoseExercise]

  }

  class RoseInputTypesTable(tag: Tag) extends Table[RoseInputType](tag, "rose_inputs") {

    def id = column[Int]("id")

    def exerciseId = column[Int]("exercise_id")

    def exSemVer = column[SemanticVersion]("ex_sem_ver")

    def name = column[String]("input_name")

    def inputType = column[ProgDataType]("input_type")


    def pk = primaryKey("pk", (id, exerciseId, exSemVer))

    def exerciseFk = foreignKey("exercise_fk", (exerciseId, exSemVer), exTable)(ex => (ex.id, ex.semanticVersion))


    override def * = (id, exerciseId, exSemVer, name, inputType).mapTo[RoseInputType]

  }

  class RoseSampleSolutionsTable(tag: Tag) extends Table[RoseSampleSolution](tag, "rose_samples") {

    def exerciseId = column[Int]("exercise_id")

    def exSemVer = column[SemanticVersion]("ex_sem_ver")

    def language = column[ProgLanguage]("language")

    def solution = column[String]("solution")


    def pk = primaryKey("pk", (exerciseId, exSemVer, language))

    def exerciseFk = foreignKey("exercise_fk", (exerciseId, exSemVer), exTable)(ex => (ex.id, ex.semanticVersion))


    override def * = (exerciseId, exSemVer, language, solution).mapTo[RoseSampleSolution]

  }

  class RoseSolutionsTable(tag: Tag) extends PartSolutionsTable(tag, "rose_solutions") {

    def solution = column[String]("solution")


    override def * = (username, exerciseId, exSemVer, part, solution, points, maxPoints).mapTo[RoseSolution]

  }

}