package model.blanks

import javax.inject.Inject
import model.SemanticVersion
import model.persistence.SingleExerciseTableDefs
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

// Table definitions

class BlanksTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with SingleExerciseTableDefs[BlanksExercise, BlanksCompleteExercise, Seq[BlanksAnswer], BlanksSolution, BlanksExPart] {

  import profile.api._

  // Abstract types

  override protected type ExTableDef = BlanksExercisesTable

  override protected type SolTableDef = BlanksSolutionsTable

  // Table queries

  override protected val exTable  = TableQuery[BlanksExercisesTable]
  override protected val solTable = TableQuery[BlanksSolutionsTable]

  private val blanksSamples = TableQuery[BlanksSampleAnswersTable]

  //Reading

  override def completeExForEx(ex: BlanksExercise): Future[BlanksCompleteExercise] = samplesForExercise(ex) map (samples => BlanksCompleteExercise(ex, samples))

  private def samplesForExercise(ex: BlanksExercise): Future[Seq[BlanksAnswer]] = db.run(blanksSamples.filter(_.exerciseId === ex.id).result)

  // Saving

  override protected def saveExerciseRest(compEx: BlanksCompleteExercise): Future[Boolean] = saveSeq[BlanksAnswer](compEx.samples, a => db.run(blanksSamples += a))

  // Column types

  override protected implicit val partTypeColumnType: BaseColumnType[BlanksExPart] =
    MappedColumnType.base[BlanksExPart, String](_.entryName, BlanksExParts.withNameInsensitive)

  private implicit val solutionTypeColumnType: BaseColumnType[Seq[BlanksAnswer]] =
    MappedColumnType.base[Seq[BlanksAnswer], String](_.mkString, _ => Seq.empty)

  // Table defs

  class BlanksExercisesTable(tag: Tag) extends ExerciseTableDef(tag, "blanks_exercises") {

    def blanksText = column[String]("blanks_text")

    def rawBlanksText = column[String]("raw_blanks_text")


    override def * = (id, semanticVersion, title, author, text, state, rawBlanksText, blanksText).mapTo[BlanksExercise]

  }

  private class BlanksSampleAnswersTable(tag: Tag) extends Table[BlanksAnswer](tag, "blanks_samples") {

    def id = column[Int]("id")

    def exerciseId = column[Int]("exercise_id")

    def exSemVer = column[SemanticVersion]("ex_sem_ver")

    def sample = column[String]("solution")


    def pk = primaryKey("pk", (id, exerciseId, exSemVer))

    def exerciseFk = foreignKey("exercise_fk", (exerciseId, exSemVer), exTable)(ex => (ex.id, ex.semanticVersion))


    override def * = (id, exerciseId, exSemVer, sample).mapTo[BlanksAnswer]

  }


  class BlanksSolutionsTable(tag: Tag) extends PartSolutionsTable(tag, "blanks_answers") {

    def solution = column[Seq[BlanksAnswer]]("answers")


    override def * = (username, exerciseId, exSemVer, part, solution, points, maxPoints).mapTo[BlanksSolution]

  }

}
