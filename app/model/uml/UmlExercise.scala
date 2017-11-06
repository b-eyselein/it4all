package model.uml

import com.fasterxml.jackson.annotation.{JsonGetter, JsonIgnore, JsonProperty}
import model.Enums.ExerciseState
import model.core.StringConsts._
import model.{DbExercise, TableDefs}
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{JsPath, Reads}
import play.twirl.api.{Html, HtmlFormat}
import slick.jdbc.JdbcProfile

object UmlExerciseReads {
  implicit def umlExReads: Reads[UmlExercise] = (
    (JsPath \ ID_NAME).read[Int] and
      (JsPath \ TITLE_NAME).read[String] and
      (JsPath \ AUTHOR_NAME).read[String] and
      (JsPath \ TEXT_NAME).read[List[String]] and
      (JsPath \ STATE_NAME).read[String]
    ) ((i, ti, a, te, s) => UmlExercise(i, ti, a, te.mkString, ExerciseState.valueOf(s), "", "", "", "", ""))
}

case class UmlExercise(i: Int, ti: String, a: String, te: String, s: ExerciseState,
                       @JsonIgnore classSelText: String, @JsonIgnore diagDrawText: String,
                       @JsonProperty(required = true) solution: String,
                       @JsonProperty(required = true) mappings: String, @JsonProperty(required = true) ignoreWords: String
                      ) extends DbExercise(i, ti, a, te, s) {

  override def renderRest(/*fileResults: List[Try[Path]]*/): Html = new Html(
    s"""<td>${views.html.core.helperTemplates.modal.render("Klassenwahltext...", new Html(classSelText + "<hr>" + HtmlFormat.escape(classSelText)), "Klassenwahltext")}</td>
       |<td>${views.html.core.helperTemplates.modal.render("Diagrammzeichnentext...", new Html(diagDrawText + "<hr>" + HtmlFormat.escape(diagDrawText)), "Diagrammzeichnentext")}</td>""".stripMargin)

  @JsonIgnore
  def getClassesForDiagDrawingHelp: String = UmlSolution.getClassesForDiagDrawingHelp(getSolution.classes)

  //  @JsonProperty(value = "mappings", required = true)
  //  def getMappingsForJson: java.util.Set[java.util.Map.Entry[String, String]] = mappings.entrySet

  @JsonIgnore
  def getSolution: UmlSolution = UmlSolution.fromJson(solution).get

  @JsonGetter("solution")
  def getSolutionAsJson: Object = {
    // Setter only for generation of json schema...
    solution
  }

}

trait UmlExercises extends TableDefs {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  class UmlExerciseTable(tag: Tag) extends HasBaseValuesTable[UmlExercise](tag, "uml_exercises") {

    def classSelText = column[String]("class_sel_text")

    def diagDrawText = column[String]("diag_draw_text")

    def solution = column[String]("solution")

    def mappings = column[String]("mappings")

    def ignoreWords = column[String]("ignore_words")


    def * = (id, title, author, text, state, classSelText, diagDrawText, solution, mappings, ignoreWords) <> (UmlExercise.tupled, UmlExercise.unapply)
  }

  lazy val umlExercises = TableQuery[UmlExerciseTable]

}