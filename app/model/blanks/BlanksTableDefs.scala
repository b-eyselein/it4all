package model.blanks

import javax.inject.Inject
import model.Enums.ExerciseState
import model._
import model.blanks.BlanksExParts.{BlankExSinglePart, BlanksExPart}
import model.persistence.SingleExerciseTableDefs
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

// Classes for user

case class BlanksCompleteExercise(ex: BlanksExercise, samples: Seq[BlanksAnswer]) extends PartsCompleteEx[BlanksExercise, BlanksExPart] {

  override def preview: Html = views.html.blanks.blanksPreview(this)

  override def hasPart(partType: BlanksExPart): Boolean = true

}


case class BlanksExercise(override val id: Int, override val title: String, override val author: String, override val text: String, override val state: ExerciseState,
                          rawBlanksText: String, blanksText: String) extends Exercise {

  def this(baseValues: (Int, String, String, String, ExerciseState), rawBlanksText: String, blanksText: String) =
    this(baseValues._1, baseValues._2, baseValues._3, baseValues._4, baseValues._5, rawBlanksText, blanksText)

}

case class BlanksAnswer(id: Int, exerciseId: Int, solution: String)

case class BlanksSolution(username: String, exerciseId: Int, part: BlanksExPart, answers: Seq[BlanksAnswer]) extends PartSolution {

  override type PartType = BlanksExPart

}

// Table definitions

class BlanksTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] with SingleExerciseTableDefs[BlanksExercise, BlanksCompleteExercise, BlanksSolution, BlanksExPart] {

  import profile.api._

  // Abstract types

  override protected type ExTableDef = BlanksExercisesTable

  override protected type SolTableDef = BlanksSolutionsTable

  override protected val exTable = TableQuery[BlanksExercisesTable]

  override protected val solTable = TableQuery[BlanksSolutionsTable]

  val blanksSamples = TableQuery[BlanksSampleAnswersTable]

  override def completeExForEx(ex: BlanksExercise)(implicit ec: ExecutionContext): Future[BlanksCompleteExercise] =
    samplesForExercise(ex) map (samples => BlanksCompleteExercise(ex, samples))

  override protected def saveExerciseRest(compEx: BlanksCompleteExercise)(implicit ec: ExecutionContext): Future[Boolean] = saveSeq[BlanksAnswer](compEx.samples, a => db.run(blanksSamples += a))

  // Reading

  private def samplesForExercise(ex: BlanksExercise)(implicit ec: ExecutionContext): Future[Seq[BlanksAnswer]] = db.run(blanksSamples.filter(_.exerciseId === ex.id).result)

  // Column types

  implicit val givenAnswerColumnType: BaseColumnType[Seq[BlanksAnswer]] =
    MappedColumnType.base[Seq[BlanksAnswer], String](_.mkString, _ => Seq.empty)

  override protected implicit val partTypeColumnType: BaseColumnType[BlanksExPart] =
    MappedColumnType.base[BlanksExPart, String](_.urlName, str => BlanksExParts.values.find(_.urlName == str) getOrElse BlankExSinglePart)

  // Table defs

  class BlanksExercisesTable(tag: Tag) extends HasBaseValuesTable[BlanksExercise](tag, "blanks_exercises") {

    def blanksText = column[String]("blanks_text")

    def rawBlanksText = column[String]("raw_blanks_text")


    def pk = primaryKey("pk", id)


    def * = (id, title, author, text, state, rawBlanksText, blanksText) <> (BlanksExercise.tupled, BlanksExercise.unapply)

  }

  class BlanksSampleAnswersTable(tag: Tag) extends Table[BlanksAnswer](tag, "blanks_samples") {

    def id = column[Int]("id")

    def exerciseId = column[Int]("exercise_id")

    def sample = column[String]("solution")


    def pk = primaryKey("pk", (id, exerciseId))

    def exerciseFk = foreignKey("exercise_fk", exerciseId, exTable)(_.id)


    def * = (id, exerciseId, sample) <> (BlanksAnswer.tupled, BlanksAnswer.unapply)

  }

  class BlanksSolutionsTable(tag: Tag) extends PartSolutionsTable[BlanksSolution](tag, "blanks_answers") {

    def answers = column[Seq[BlanksAnswer]]("answers")


    override def * = (username, exerciseId, part, answers) <> (BlanksSolution.tupled, BlanksSolution.unapply)
  }

}


