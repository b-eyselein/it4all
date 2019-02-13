package model.web

import enumeratum.{EnumEntry, PlayEnum}
import model._
import org.openqa.selenium.{By, SearchContext}
import play.twirl.api.Html

import scala.collection.immutable.IndexedSeq
import scala.language.postfixOps

sealed trait JsActionType extends EnumEntry

object JsActionType extends PlayEnum[JsActionType] {

  override val values: IndexedSeq[JsActionType] = findValues

  case object CLICK extends JsActionType

  case object FILLOUT extends JsActionType

}

// Classes for use

final case class WebCompleteEx(ex: WebExercise, htmlTasks: Seq[HtmlCompleteTask], jsTasks: Seq[JsCompleteTask],
                               sampleSolutions: Seq[WebSampleSolution]) extends SingleCompleteEx[WebExPart] {

  override type E = WebExercise

  // remaining fields from WebExercise

  def htmlText: Option[String] = ex.htmlText

  def jsText: Option[String] = ex.jsText

  // other methods

  override def preview: Html = // FIXME: move to toolMain!
    views.html.idExercises.web.webPreview(this)

  override def tags: Seq[WebExTag] = WebExParts.values map (part => new WebExTag(part.partName, hasPart(part)))

  override def hasPart(partType: WebExPart): Boolean = partType match {
    case WebExParts.HtmlPart => htmlTasks.nonEmpty
    case WebExParts.JsPart   => jsTasks.nonEmpty
  }

  def maxPoints(part: WebExPart): Points = part match {
    case WebExParts.HtmlPart => addUp(htmlTasks.map(_.maxPoints))
    case WebExParts.JsPart   => addUp(jsTasks.map(_.maxPoints))
  }

  def tasksForPart(part: WebExPart): Seq[WebCompleteTask] = part match {
    case WebExParts.HtmlPart => htmlTasks
    case WebExParts.JsPart   => jsTasks
  }

}

trait WebCompleteTask {

  val task: WebTask

  def maxPoints: Points

}

final case class HtmlCompleteTask(task: HtmlTask, attributes: Seq[Attribute]) extends WebCompleteTask {

  override def maxPoints: Points = (1 point) + (task.textContent map (_ => 1 point) getOrElse (0 points)) + (attributes.size points)

}

final case class JsCompleteTask(task: JsTask, conditions: Seq[JsCondition]) extends WebCompleteTask {
  override def maxPoints: Points = (1 point) + (conditions.size points)
}

class WebExTag(part: String, hasExes: Boolean) extends ExTag {

  override def cssClass: String = if (hasExes) "label label-primary" else "label label-default"

  override def buttonContent: String = part

  override def title: String = s"Diese Aufgabe besitzt ${if (!hasExes) "k" else ""}einen $part-Teil"

}

// Database classes

final case class WebExercise(id: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String, state: ExerciseState,
                             htmlText: Option[String], jsText: Option[String]) extends Exercise

trait WebTask {
  val id        : Int
  val exerciseId: Int
  val exSemVer  : SemanticVersion
  val text      : String
  val xpathQuery: String
}

final case class HtmlTask(id: Int, exerciseId: Int, exSemVer: SemanticVersion, text: String, xpathQuery: String,
                          textContent: Option[String]) extends WebTask

final case class Attribute(key: String, taskId: Int, exerciseId: Int, exSemVer: SemanticVersion, value: String)

final case class JsTask(id: Int, exerciseId: Int, exSemVer: SemanticVersion, text: String, xpathQuery: String,
                        actionType: JsActionType, keysToSend: Option[String]) extends WebTask {

  def perform(context: SearchContext): Boolean = Option(context findElement By.xpath(xpathQuery)) match {
    case None => false

    case Some(element) => actionType match {
      case JsActionType.CLICK   =>
        element.click()
        true
      case JsActionType.FILLOUT =>
        element.clear()
        element.sendKeys(keysToSend getOrElse "")
        // click on other element to fire the onchange event...
        context.findElement(By.xpath("//body")).click()
        true
      case _                    => false
    }
  }

  def actionDescription: String = actionType match {
    case JsActionType.CLICK   => s"Klicke auf Element mit XPath Query <code>$xpathQuery</code>"
    case JsActionType.FILLOUT => s"Sende Keys '${keysToSend getOrElse ""}' an Element mit XPath Query $xpathQuery"
  }

}

final case class JsCondition(id: Int, taskId: Int, exerciseId: Int, exSemVer: SemanticVersion, xpathQuery: String,
                             isPrecondition: Boolean, awaitedValue: String) {

  def description: String = s"""Element mit XPath <code>$xpathQuery</code> sollte den Inhalt <code>$awaitedValue</code> haben"""

  def maxPoints: Points = 1 point

}

final case class WebSampleSolution(id: Int, exerciseId: Int, exSemVer: SemanticVersion,
                                   htmlSample: Option[String], jsSample: Option[String])
  extends SampleSolution[(Option[String], Option[String])] {

  def sample: (Option[String], Option[String]) = (htmlSample, jsSample)

}

final case class WebSolution(id: Int, username: String, exerciseId: Int, exSemVer: SemanticVersion, part: WebExPart,
                             solution: String, points: Points, maxPoints: Points) extends DBPartSolution[WebExPart, String]

final case class WebExerciseReview(username: String, exerciseId: Int, exerciseSemVer: SemanticVersion, exercisePart: WebExPart,
                                   difficulty: Difficulty, maybeDuration: Option[Int]) extends ExerciseReview[WebExPart]