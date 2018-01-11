package model.blanks

import javax.inject.Inject

import controllers.exes.idExes.BlanksToolObject
import model.Enums.ExerciseState
import model._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.Call
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

case class BlanksCompleteExercise(ex: BlanksExercise, samples: Seq[BlanksAnswer]) extends CompleteEx[BlanksExercise] {

  override def preview: Html = views.html.blanks.blanksPreview(this)

  override def exerciseRoutes: Map[Call, String] = BlanksToolObject.exerciseRoutes(this)

}

object BlanksExercise {

  def tupled(t: (Int, String, String, String, ExerciseState, String, String)): BlanksExercise = BlanksExercise(t._1, t._2, t._3, t._4, t._5, t._6, t._7)

  def apply(id: Int, title: String, author: String, text: String, state: ExerciseState, rawBlanksText: String, blanksText: String): BlanksExercise =
    new BlanksExercise(BaseValues(id, title, author, text, state), rawBlanksText, blanksText)

  def unapply(arg: BlanksExercise): Option[(Int, String, String, String, ExerciseState, String, String)] =
    Some((arg.id, arg.title, arg.author, arg.text, arg.state, arg.rawBlanksText, arg.blanksText))

}

case class BlanksExercise(override val baseValues: BaseValues, rawBlanksText: String, blanksText: String) extends Exercise

case class BlanksAnswer(id: Int, exerciseId: Int, solution: String)

// Table definitions

class BlanksTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] with ExerciseTableDefs[BlanksExercise, BlanksCompleteExercise] {

  import profile.api._

  val blanksExercises = TableQuery[BlanksExercisesTable]

  val blanksSamples = TableQuery[BlanksSampleAnswersTable]

  override type ExTableDef = BlanksExercisesTable

  override val exTable = blanksExercises

  override def completeExForEx(ex: BlanksExercise)(implicit ec: ExecutionContext): Future[BlanksCompleteExercise] =
    samplesForExercise(ex) map (samples => BlanksCompleteExercise(ex, samples))

  override protected def saveExerciseRest(compEx: BlanksCompleteExercise)(implicit ec: ExecutionContext): Future[Boolean] = saveSeq[BlanksAnswer](compEx.samples, a => db.run(blanksSamples += a))

  // Reading

  private def samplesForExercise(ex: BlanksExercise)(implicit ec: ExecutionContext): Future[Seq[BlanksAnswer]] = db.run(blanksSamples.filter(_.exerciseId === ex.id).result)

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

    def exerciseFk = foreignKey("exercise_fk", exerciseId, blanksExercises)(_.id)


    def * = (id, exerciseId, sample) <> (BlanksAnswer.tupled, BlanksAnswer.unapply)

  }

}


