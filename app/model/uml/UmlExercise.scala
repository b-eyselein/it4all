package model.uml

import model.Enums.ExerciseState
import model.core.CompleteEx
import model.uml.UmlExYamlProtocol._
import model.{Exercise, TableDefs}
import net.jcazevedo.moultingyaml._
import play.api.db.slick.HasDatabaseConfigProvider
import play.twirl.api.{Html, HtmlFormat}
import slick.jdbc.JdbcProfile

import scala.language.implicitConversions

case class UmlCompleteEx(ex: UmlExercise) extends CompleteEx[UmlExercise] {

  import views.html.core.helperTemplates.modal

  override def renderRest: Html = new Html(
    s"""<td>${modal.render("Klassenwahltext", new Html(ex.classSelText + "<hr>" + HtmlFormat.escape(ex.classSelText)), "Klassenwahltext")}
       |</td>
       |<td>${modal.render("Diagrammzeichnentext", new Html(ex.diagDrawText + "<hr>" + HtmlFormat.escape(ex.diagDrawText)), "Diagrammzeichnentext")}
       |</td>
       |<td>${modal.render("Lösung", new Html(ex.solution.toYaml.prettyPrint), "Lösung")}
       |</td>""".stripMargin)

}

case class UmlExercise(i: Int, ti: String, a: String, te: String, s: ExerciseState,
                       classSelText: String, diagDrawText: String, solution: String, mappings: String, ignoreWords: String)
  extends Exercise(i, ti, a, te, s) {

  def getClassesForDiagDrawingHelp: String = UmlSolution.getClassesForDiagDrawingHelp(getSolution.classes)

  def getSolution: UmlSolution = UmlSolution.fromJson(solution).get

}

case class UmlMusterClass(name: String, exerciseId: Int, attributes: List[String])

case class UmlMapping(exerciseId: Int, key: String, value: String)

case class UmlIgnore(exerciseId: Int, toIgnore: String)

trait UmlExercises extends TableDefs {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  val umlExercises = TableQuery[UmlExerciseTable]

  val umlMappings = TableQuery[UmlMappingsTable]

  val umlToIgnore = TableQuery[UmlIgnoreTable]

  class UmlExerciseTable(tag: Tag) extends HasBaseValuesTable[UmlExercise](tag, "uml_exercises") {

    def classSelText = column[String]("class_sel_text")

    def diagDrawText = column[String]("diag_draw_text")

    def solution = column[String]("solution")

    def mappings = column[String]("mappings")

    def ignoreWords = column[String]("ignore_words")


    def * = (id, title, author, text, state, classSelText, diagDrawText, solution, mappings, ignoreWords) <> (UmlExercise.tupled, UmlExercise.unapply)
  }

  class UmlMappingsTable(tag: Tag) extends Table[UmlMapping](tag, "uml_mappings") {

    def exerciseId = column[Int]("exercise_id")

    def mappedKey = column[String]("mapped_key")

    def mappedValue = column[String]("mapped_value")


    def pk = primaryKey("pk", (exerciseId, mappedKey))

    def exerciseFk = foreignKey("exercise_fk", exerciseId, umlExercises)(_.id)


    def * = (exerciseId, mappedKey, mappedValue) <> (UmlMapping.tupled, UmlMapping.unapply)

  }

  class UmlIgnoreTable(tag: Tag) extends Table[UmlIgnore](tag, "uml_ignore") {

    def exerciseId = column[Int]("exercise_id")

    def toIgnore = column[String]("to_ignore")


    def pk = primaryKey("pk", (exerciseId, toIgnore))

    def exerciseFk = foreignKey("exercise_fk", exerciseId, umlExercises)(_.id)


    def * = (exerciseId, toIgnore) <> (UmlIgnore.tupled, UmlIgnore.unapply)

  }

}