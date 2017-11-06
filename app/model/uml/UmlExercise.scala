package model.uml

import model.Enums.ExerciseState
import model.core.StringConsts._
import model.{Exercise, TableDefs}
import net.jcazevedo.moultingyaml._
import play.api.db.slick.HasDatabaseConfigProvider
import play.twirl.api.{Html, HtmlFormat}
import slick.jdbc.JdbcProfile

import scala.language.implicitConversions

object UmlExYamlProtocol extends DefaultYamlProtocol {

  implicit def string2YamlString(str: String): YamlString = YamlString(str)

  implicit object UmlExYamlFormat extends YamlFormat[UmlExercise] {
    override def write(ex: UmlExercise): YamlValue = YamlObject(
      YamlString(ID_NAME) -> YamlNumber(ex.id),
      YamlString(TITLE_NAME) -> YamlString(ex.title),
      YamlString(AUTHOR_NAME) -> YamlString(ex.author),
      YamlString(TEXT_NAME) -> YamlString(ex.text),
      YamlString(STATE_NAME) -> YamlString(ex.state.name),

      // Exercise specific values
      // TODO: solution: String, mappings: Map[String, String], ignoreWords: List[String]
      YamlString(SOLUTION_NAME) -> YamlString(ex.solution)
      //      YamlString(MAPPINGS_NAME) -> YamlObject(ex.mappings),
      //      YamlString(IGNORE_WORDS_NAME) -> YamlString(ex.refernenceFile)
    )

    override def read(yaml: YamlValue): UmlExercise =
      yaml.asYamlObject.getFields(ID_NAME, TITLE_NAME, AUTHOR_NAME, TEXT_NAME, STATE_NAME, SOLUTION_NAME, MAPPINGS_NAME, IGNORE_WORDS_NAME) match {
        case Seq(YamlNumber(id), YamlString(title), YamlString(author), YamlString(text), YamlString(state), YamlObject(solution), YamlObject(mappings), YamlArray(toIngore)) =>
          val textParse = new UmlExTextParser(text, mappings.map(yamlVals => (yamlVals._1.toString, yamlVals._2.toString)), toIngore.toList.map(_.toString))
          UmlExercise(id.intValue, title, author, text, ExerciseState.valueOf(state),
            textParse.parseTextForClassSel, textParse.parseTextForDiagDrawing, solution.mkString, mappings.mkString, toIngore.toList.map(_.toString).mkString)

        case other => /* FIXME: Fehlerbehandlung... */
          other.foreach(value => println(value + "\n"))
          deserializationError("UmlExercise expected!")
      }
  }
}

case class UmlExercise(i: Int, ti: String, a: String, te: String, s: ExerciseState,
                       classSelText: String, diagDrawText: String, solution: String, mappings: String, ignoreWords: String)
  extends Exercise(i, ti, a, te, s) {

  override def renderRest: Html = new Html(
    s"""<td>${views.html.core.helperTemplates.modal.render("Klassenwahltext...", new Html(classSelText + "<hr>" + HtmlFormat.escape(classSelText)), "Klassenwahltext")}</td>
       |<td>${views.html.core.helperTemplates.modal.render("Diagrammzeichnentext...", new Html(diagDrawText + "<hr>" + HtmlFormat.escape(diagDrawText)), "Diagrammzeichnentext")}</td>""".stripMargin)

  def getClassesForDiagDrawingHelp: String = UmlSolution.getClassesForDiagDrawingHelp(getSolution.classes)

  def getSolution: UmlSolution = UmlSolution.fromJson(solution).get

}

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