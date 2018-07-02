package model.blanks

import javax.inject.Inject
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

  //  private implicit val givenAnswerColumnType: BaseColumnType[Seq[BlanksAnswer]] =
  //    MappedColumnType.base[Seq[BlanksAnswer], String](_.mkString, _ => Seq.empty)

  override protected implicit val partTypeColumnType: BaseColumnType[BlanksExPart] =
    MappedColumnType.base[BlanksExPart, String](_.entryName, BlanksExParts.withNameInsensitive)

  // Table defs

  class BlanksExercisesTable(tag: Tag) extends HasBaseValuesTable[BlanksExercise](tag, "blanks_exercises") {

    def blanksText = column[String]("blanks_text")

    def rawBlanksText = column[String]("raw_blanks_text")


    def pk = primaryKey("pk", id)


    override def * = (id, title, author, text, state, rawBlanksText, blanksText) <> (BlanksExercise.tupled, BlanksExercise.unapply)

  }

  private class BlanksSampleAnswersTable(tag: Tag) extends Table[BlanksAnswer](tag, "blanks_samples") {

    def id = column[Int]("id")

    def exerciseId = column[Int]("exercise_id")

    def sample = column[String]("solution")


    def pk = primaryKey("pk", (id, exerciseId))

    def exerciseFk = foreignKey("exercise_fk", exerciseId, exTable)(_.id)


    override def * = (id, exerciseId, sample) <> (BlanksAnswer.tupled, BlanksAnswer.unapply)

  }


  override protected implicit val solutionTypeColumnType: BaseColumnType[Seq[BlanksAnswer]] =
    MappedColumnType.base[Seq[BlanksAnswer], String](_.mkString, _ => Seq.empty)


  class BlanksSolutionsTable(tag: Tag) extends PartSolutionsTable(tag, "blanks_answers") {

//    def solution = column[Seq[BlanksAnswer]]("answers")


    override def * = (username, exerciseId, part, solution, points, maxPoints).mapTo[BlanksSolution]
  }

}
