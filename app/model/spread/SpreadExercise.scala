package model.spread

import model.Enums.ExerciseState
import model._
import play.api.db.slick.HasDatabaseConfigProvider
import play.twirl.api.Html
import slick.jdbc.JdbcProfile


object SpreadExercise {

  def tupled(tuple: (Int, String, String, String, ExerciseState, String, String)): SpreadExercise =
    SpreadExercise(BaseValues(tuple._1, tuple._2, tuple._3, tuple._4, tuple._5), tuple._6, tuple._7)

  def apply(id: Int, title: String, author: String, text: String, state: ExerciseState, sampleFile: String, templateFile: String) =
    new SpreadExercise(BaseValues(id, title, author, text, state), sampleFile, templateFile)

  def unapply(arg: SpreadExercise): Option[(Int, String, String, String, ExerciseState, String, String)] =
    Some((arg.id, arg.title, arg.author, arg.text, arg.state, arg.sampleFileName, arg.templateFilename))

}

case class SpreadExercise(bv: BaseValues, sampleFileName: String, templateFilename: String) extends Exercise(bv) with CompleteEx[SpreadExercise] {

  override def ex: SpreadExercise = this

  override def renderRest: Html = new Html(
    s"""<td>$sampleFileName</td>
       |<td>$templateFilename</td>""".stripMargin)

}

trait SpreadExercises extends TableDefs {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  val spreadExercises = TableQuery[SpreadExerciseTable]

  class SpreadExerciseTable(tag: Tag) extends HasBaseValuesTable[SpreadExercise](tag, "spread_exercises") {

    def sampleFilename = column[String]("sample_filename")

    def templateFilename = column[String]("template_filename")

    def * = (id, title, author, text, state, sampleFilename, templateFilename) <> (SpreadExercise.tupled, SpreadExercise.unapply)

  }

}