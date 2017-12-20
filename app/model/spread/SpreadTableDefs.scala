package model.spread

import controllers.exes.fileExes.{FileExToolObject, SpreadToolObject}
import model.Enums.ExerciseState
import model.{BaseValues, Exercise, FileCompleteEx, TableDefs}
import play.api.mvc.Call
import play.twirl.api.Html

object SpreadExercise {

  def tupled(tuple: (Int, String, String, String, ExerciseState, String, String)): SpreadExercise =
    SpreadExercise(BaseValues(tuple._1, tuple._2, tuple._3, tuple._4, tuple._5), tuple._6, tuple._7)

  def apply(id: Int, title: String, author: String, text: String, state: ExerciseState, sampleFile: String, templateFile: String) =
    new SpreadExercise(BaseValues(id, title, author, text, state), sampleFile, templateFile)

  def unapply(arg: SpreadExercise): Option[(Int, String, String, String, ExerciseState, String, String)] =
    Some((arg.id, arg.title, arg.author, arg.text, arg.state, arg.sampleFilename, arg.templateFilename))

}

case class SpreadExercise(override val baseValues: BaseValues, sampleFilename: String, templateFilename: String) extends Exercise with FileCompleteEx[SpreadExercise] {

  override val toolObject: FileExToolObject = SpreadToolObject

  override def ex: SpreadExercise = this

  override def preview: Html = views.html.spread.spreadPreview.render(this)

  override def exerciseRoutes: Map[Call, String] = SpreadToolObject.exerciseRoutes(this)

}

trait SpreadTableDefs extends TableDefs {
  self: play.api.db.slick.HasDatabaseConfigProvider[slick.jdbc.JdbcProfile] =>

  import profile.api._

  val spreadExercises = TableQuery[SpreadExerciseTable]

  class SpreadExerciseTable(tag: Tag) extends HasBaseValuesTable[SpreadExercise](tag, "spread_exercises") {

    def sampleFilename = column[String]("sample_filename")

    def templateFilename = column[String]("template_filename")

    def * = (id, title, author, text, state, sampleFilename, templateFilename) <> (SpreadExercise.tupled, SpreadExercise.unapply)

  }

}