package model.spread

import javax.inject.Inject
import model.Enums.ExerciseState
import model._
import model.core.tools.FileExToolObject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.Call
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}

object SpreadExercise {

  def tupled(tuple: (Int, String, String, String, ExerciseState, String, String)): SpreadExercise =
    SpreadExercise(BaseValues(tuple._1, tuple._2, tuple._3, tuple._4, tuple._5), tuple._6, tuple._7)

  def apply(id: Int, title: String, author: String, text: String, state: ExerciseState, sampleFile: String, templateFile: String) =
    new SpreadExercise(BaseValues(id, title, author, text, state), sampleFile, templateFile)

  def unapply(arg: SpreadExercise): Option[(Int, String, String, String, ExerciseState, String, String)] =
    Some((arg.id, arg.title, arg.author, arg.text, arg.state, arg.sampleFilename, arg.templateFilename))

}

case class SpreadExercise(override val baseValues: BaseValues, sampleFilename: String, templateFilename: String) extends Exercise with FileCompleteEx[SpreadExercise] {

  override def ex: SpreadExercise = this

  override def preview: Html = views.html.spread.spreadPreview.render(this)

}

class SpreadTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[slick.jdbc.JdbcProfile] with ExerciseTableDefs[SpreadExercise, SpreadExercise] {

  import profile.api._

  override def completeExForEx(ex: SpreadExercise)(implicit ec: ExecutionContext): Future[SpreadExercise] = Future(ex)

  override def saveExerciseRest(compEx: SpreadExercise)(implicit ec: ExecutionContext): Future[Boolean] = Future(true)

  val spreadExercises = TableQuery[SpreadExerciseTable]

  override type ExTableDef = SpreadExerciseTable

  override val exTable = spreadExercises

  class SpreadExerciseTable(tag: Tag) extends HasBaseValuesTable[SpreadExercise](tag, "spread_exercises") {

    def sampleFilename = column[String]("sample_filename")

    def templateFilename = column[String]("template_filename")

    def * = (id, title, author, text, state, sampleFilename, templateFilename) <> (SpreadExercise.tupled, SpreadExercise.unapply)

  }

}