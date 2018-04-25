package model.web

import model._
import org.openqa.selenium.{By, SearchContext}
import play.twirl.api.Html
import enumeratum.EnumEntry
import enumeratum.Enum

import scala.collection.immutable.IndexedSeq


sealed trait JsActionType extends EnumEntry

object JsActionType extends Enum[JsActionType] {

  override val values: IndexedSeq[JsActionType] = findValues

  case object CLICK extends JsActionType

  case object FILLOUT extends JsActionType

}

// Classes for use

case class WebCompleteEx(ex: WebExercise, htmlTasks: Seq[HtmlCompleteTask], jsTasks: Seq[JsCompleteTask], phpTasks: Seq[PHPCompleteTask] = Seq.empty)
  extends PartsCompleteEx[WebExercise, WebExPart] {

  override def preview: Html = // FIXME: move to toolMain!
    views.html.idExercises.web.webPreview(this)

  override def tags: Seq[WebExTag] = WebExParts.values map (part => new WebExTag(part.partName, hasPart(part)))

  override def hasPart(partType: WebExPart): Boolean = partType match {
    case HtmlPart => htmlTasks.nonEmpty
    case JsPart   => jsTasks.nonEmpty
    case PHPPart  => phpTasks.nonEmpty
  }

  def maxPoints(part: WebExPart): Double = part match {
    case HtmlPart => htmlTasks.map(_.maxPoints).sum
    case JsPart   => jsTasks.map(_.maxPoints).sum
    case PHPPart  => phpTasks.map(_.maxPoints).sum
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


case class WebExercise(override val id: Int, override val title: String, override val author: String, override val text: String, override val state: ExerciseState,
                       htmlText: Option[String], jsText: Option[String], phpText: Option[String]) extends Exercise

trait WebTask {
  val id        : Int
  val exerciseId: Int
  val text      : String
  val xpathQuery: String
}

case class HtmlTask(id: Int, exerciseId: Int, text: String, xpathQuery: String, textContent: Option[String]) extends WebTask

case class Attribute(key: String, taskId: Int, exerciseId: Int, value: String)

case class JsTask(id: Int, exerciseId: Int, text: String, xpathQuery: String, actionType: JsActionType, keysToSend: Option[String]) extends WebTask {

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
    case JsActionType.CLICK   => s"Klicke auf Element mit XPath Query $xpathQuery"
    case JsActionType.FILLOUT => s"Sende Keys '${keysToSend getOrElse ""}' an Element mit XPath Query $xpathQuery"
  }

}

case class JsCondition(id: Int, taskId: Int, exerciseId: Int, xpathQuery: String, isPrecondition: Boolean, awaitedValue: String) {

  def description = s"Element mit XPath '$xpathQuery' sollte den Inhalt '$awaitedValue' haben"

  def maxPoints: Double = 1

}

case class PHPTask(id: Int, exerciseId: Int, text: String, xpathQuery: String, textContent: Option[String]) extends WebTask

case class WebSolution(username: String, exerciseId: Int, part: WebExPart, solution: String) extends PartSolution {

  override type PartType = WebExPart

}
