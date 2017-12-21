package model.blanks

import controllers.exes.idExes.BlanksToolObject
import model.Enums.ExerciseState
import model.{BaseValues, CompleteEx, Exercise, TableDefs}
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.mvc.Call
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

case class BlanksCompleteExercise(ex: BlanksExercise, samples: Seq[BlanksAnswer]) extends CompleteEx[BlanksExercise] {

  override def preview: Html = views.html.blanks.blanksPreview(this)

  override def exerciseRoutes: Map[Call, String] = BlanksToolObject.exerciseRoutes(this)

}

object BlanksExercise {

  def tupled(t: (Int, String, String, String, ExerciseState, String)): BlanksExercise = BlanksExercise(t._1, t._2, t._3, t._4, t._5, t._6)

  def apply(id: Int, title: String, author: String, text: String, state: ExerciseState, blanksText: String): BlanksExercise =
    new BlanksExercise(BaseValues(id, title, author, text, state), blanksText)

  def unapply(arg: BlanksExercise): Option[(Int, String, String, String, ExerciseState, String)] =
    Some((arg.id, arg.title, arg.author, arg.text, arg.state, arg.blanksText))

}

case class BlanksExercise(override val baseValues: BaseValues, blanksText: String) extends Exercise

case class BlanksAnswer(id: Int, exerciseId: Int, solution: String)

// Table definitions

trait BlanksTableDefs extends TableDefs {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  val blanksExercises = TableQuery[BlanksExercisesTable]

  val blanksSamples = TableQuery[BlanksSampleAnswersTable]

  // Reading

  def blanksCompleteExById(id: Int)(implicit ec: ExecutionContext): Future[Option[BlanksCompleteExercise]] =
    db.run(blanksExercises.filter(_.id === id).result.headOption) flatMap {
      case Some(ex) => samplesForExercise(ex) map (samples => Some(BlanksCompleteExercise(ex, samples)))
      case None     => Future(None)
    }

  def blanksCompleteExes(implicit ec: ExecutionContext): Future[Seq[BlanksCompleteExercise]] =
    db.run(blanksExercises.result) flatMap { exes =>
      Future.sequence(exes map {
        ex => samplesForExercise(ex) map (s => BlanksCompleteExercise(ex, s))
      })
    }

  private def samplesForExercise(ex: BlanksExercise)(implicit ec: ExecutionContext): Future[Seq[BlanksAnswer]] =
    db.run(blanksSamples.filter(_.exerciseId === ex.id).result)

  // Saving

  def saveBlanksExercise(ex: BlanksCompleteExercise)(implicit ec: ExecutionContext): Future[Boolean] = db.run(blanksExercises.filter(_.id === ex.id).delete) flatMap {
    _ => db.run(blanksExercises += ex.ex) flatMap { _ => Future.sequence(ex.samples map saveBlanksSample) map (_.forall(identity)) }
  }

  private def saveBlanksSample(sample: BlanksAnswer)(implicit ec: ExecutionContext): Future[Boolean] =
    db.run(blanksSamples += sample) map (_ => true) recover { case _: Exception => false }

  // Table defs

  class BlanksExercisesTable(tag: Tag) extends HasBaseValuesTable[BlanksExercise](tag, "blanks_exercises") {

    def blanksText = column[String]("blanks_text")


    def pk = primaryKey("pk", id)


    def * = (id, title, author, text, state, blanksText) <> (BlanksExercise.tupled, BlanksExercise.unapply)

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


