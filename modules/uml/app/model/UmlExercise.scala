package model

import java.nio.file.Path
import javax.persistence.{Column, Entity}

import com.fasterxml.jackson.annotation.{JsonGetter, JsonIgnore, JsonProperty, JsonSetter}
import io.ebean.Finder
import io.ebean.annotation.DbJson
import model.exercise.Exercise
import play.twirl.api.{Html, HtmlFormat}

import scala.util.Try

@Entity
class UmlExercise extends Exercise {

  @Column(columnDefinition = "text")
  @JsonIgnore
  var classSelText: String = _

  @Column(columnDefinition = "text")
  @JsonIgnore
  var diagDrawText: String = _

  @Column(columnDefinition = "text")
  @JsonProperty(required = true)
  var solution: String = _

  @DbJson
  var mappings: java.util.Map[String, String] = _

  @DbJson
  @JsonProperty(value = "ignoreWords", required = true)
  var ignoreWords: java.util.List[String] = _

  override def renderRest(fileResults: List[Try[Path]]): Html = new Html(
    s"""<td>${views.html.helperTemplates.modal.render("Klassenwahltext...", new Html(classSelText + "<hr>" + HtmlFormat.escape(classSelText)), "Klassenwahltext")}</td>
       |<td>${views.html.helperTemplates.modal.render("Diagrammzeichnentext...", new Html(diagDrawText + "<hr>" + HtmlFormat.escape(diagDrawText)), "Diagrammzeichnentext")}</td>""".stripMargin)

  @JsonIgnore
  def getClassesForDiagDrawingHelp: String = UmlSolution.getClassesForDiagDrawingHelp(getSolution.classes)

  //  @JsonGetter("mappings")
  @JsonProperty(value = "mappings", required = true)
  def getMappingsForJson: java.util.Set[java.util.Map.Entry[String, String]] = mappings.entrySet

  @JsonIgnore
  def getSolution: UmlSolution = UmlSolution.fromJson(solution).get

  @JsonGetter("solution")
  def getSolutionAsJson: Object = {
    // Setter only for generation of json schema...
    solution
  }

  @JsonSetter("solution")
  def setSolution(theSolution: Object) {
    // Getter only for generation of json schema...
    solution = theSolution.toString
  }

}

object UmlExercise {

  val finder: Finder[Integer, UmlExercise] = new Finder(classOf[UmlExercise])
}