package model.web

import com.fasterxml.jackson.annotation.JsonProperty
import model.Enums.ExerciseState
import model._
import model.core.HasBaseValues._
import model.core.StringConsts._
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{JsPath, Reads}
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

object WebExerciseReads {
  implicit def webExReads: Reads[WebExercise] = (
    (JsPath \ ID_NAME).read[Int] and
      (JsPath \ TITLE_NAME).read[String] and
      (JsPath \ AUTHOR_NAME).read[String] and
      (JsPath \ TEXT_NAME).read[List[String]] and
      (JsPath \ STATE_NAME).read[String] and
      (JsPath \ HTML_TEXT_NAME).read[List[String]] and
      (JsPath \ JS_TEXT_NAME).read[List[String]]
    ) ((i, ti, a, te, s, htmlText, jsText) => WebExercise(i, ti, a, te.mkString, ExerciseState.valueOf(s), htmlText.mkString, jsText.mkString))
}

case class WebExercise(i: Int, ti: String, a: String, te: String, s: ExerciseState,
                       htmlText: String, jsText: String)
  extends Exercise(i, ti, a, te, s) {

  def htmlTasks: List[HtmlTask] = List.empty

  def jsTasks: List[JsTask] = List.empty

  @JsonProperty("htmlText")
  def htmlTextForJson: java.util.List[String] = SPLITTER.splitToList(htmlText)

  @JsonProperty("jsText")
  def jsTextForJson: java.util.List[String] = SPLITTER.splitToList(jsText)

  override def renderEditRest(isCreation: Boolean): Html = views.html.web.editWebExRest.render(this, isCreation)

  override def renderRest: Html = views.html.web.webExTableRest.render(this)

  override def getTags: List[WebExTag] = List(new WebExTag("Html", htmlTasks.nonEmpty), new WebExTag("Js", jsTasks.nonEmpty))

}

trait WebExercises extends TableDefs {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  class WebExerciseTable(tag: Tag) extends HasBaseValuesTable[WebExercise](tag, "web_exercises") {

    def htmlText = column[String]("html_text")

    def jsText = column[String]("js_text")

    def * = (id, title, author, text, state, htmlText, jsText) <> (WebExercise.tupled, WebExercise.unapply)
  }

  lazy val webExercises = TableQuery[WebExerciseTable]

}