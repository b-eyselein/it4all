package model.web

import enumeratum.{Enum, EnumEntry}
import model._
import org.openqa.selenium.{By, SearchContext}
import play.twirl.api.Html

import scala.collection.immutable.IndexedSeq

sealed trait JsActionType extends EnumEntry

object JsActionType extends Enum[JsActionType] {

  override val values: IndexedSeq[JsActionType] = findValues

  case object CLICK extends JsActionType

  case object FILLOUT extends JsActionType

}

// Classes for use

case class WebCompleteEx(ex: WebExercise, htmlTasks: Seq[HtmlCompleteTask], jsTasks: Seq[JsCompleteTask], phpTasks: Seq[PHPCompleteTask] = Seq.empty)
  extends SingleCompleteEx[WebExercise, WebExPart] {

  override def preview: Html = // FIXME: move to toolMain!
    views.html.idExercises.web.webPreview(this)

  override def tags: Seq[WebExTag] = WebExParts.values map (part => new WebExTag(part.partName, hasPart(part)))

  override def hasPart(partType: WebExPart): Boolean = partType match {
    case WebExParts.HtmlPart => htmlTasks.nonEmpty
    case WebExParts.JsPart   => jsTasks.nonEmpty
    case WebExParts.PHPPart  => phpTasks.nonEmpty
  }

  def maxPoints(part: WebExPart): Double = part match {
    case WebExParts.HtmlPart => htmlTasks.map(_.maxPoints).sum
    case WebExParts.JsPart   => jsTasks.map(_.maxPoints).sum
    case WebExParts.PHPPart  => phpTasks.map(_.maxPoints).sum
  }

  def tasksForPart(part: WebExPart): Seq[WebCompleteTask] = part match {
    case WebExParts.HtmlPart => htmlTasks
    case WebExParts.JsPart   => jsTasks
    case WebExParts.PHPPart  => phpTasks
  }

}

trait WebCompleteTask {

  val task: WebTask

  def maxPoints: Double

}

case class HtmlCompleteTask(task: HtmlTask, attributes: Seq[Attribute]) extends WebCompleteTask {
  override def maxPoints: Double = 1 + task.textContent.map(_ => 1d).getOrElse(0d) + attributes.size
}

case class JsCompleteTask(task: JsTask, conditions: Seq[JsCondition]) extends WebCompleteTask {
  override def maxPoints: Double = 1 + conditions.size
}

case class PHPCompleteTask(task: PHPTask) extends WebCompleteTask {
  override def maxPoints: Double = -1
}

class WebExTag(part: String, hasExes: Boolean) extends ExTag {

  override def cssClass: String = if (hasExes) "label label-primary" else "label label-default"

  override def buttonContent: String = part

  override def title = s"Diese Aufgabe besitzt ${if (!hasExes) "k" else ""}einen $part-Teil"

}

// Database classes

case class WebExercise(id: Int, semanticVersion: SemanticVersion,title: String, author: String, text: String, state: ExerciseState,
                       htmlText: Option[String], jsText: Option[String], phpText: Option[String]) extends Exercise

trait WebTask {
  val id        : Int
  val exerciseId: Int
  val exSemVer  : SemanticVersion
  val text      : String
  val xpathQuery: String
}

case class HtmlTask(id: Int, exerciseId: Int, exSemVer: SemanticVersion, text: String, xpathQuery: String,
                    textContent: Option[String]) extends WebTask

case class Attribute(key: String, taskId: Int, exerciseId: Int, exSemVer: SemanticVersion, value: String)

case class JsTask(id: Int, exerciseId: Int, exSemVer: SemanticVersion, text: String, xpathQuery: String,
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

case class JsCondition(id: Int, taskId: Int, exerciseId: Int, exSemVer: SemanticVersion, xpathQuery: String,
                       isPrecondition: Boolean, awaitedValue: String) {

  def description = s"""Element mit XPath <code>$xpathQuery</code> sollte den Inhalt <code>$awaitedValue</code> haben"""

  def maxPoints: Double = 1

}

case class PHPTask(id: Int, exerciseId: Int, exSemVer: SemanticVersion, text: String, xpathQuery: String,
                   textContent: Option[String]) extends WebTask

case class WebSolution(username: String, exerciseId: Int, exSemVer: SemanticVersion, part: WebExPart, solution: String,
                       points: Double, maxPoints: Double) extends DBPartSolution[WebExPart, String]