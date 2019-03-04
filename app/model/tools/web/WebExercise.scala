package model.tools.web

import enumeratum.{EnumEntry, PlayEnum}
import model._
import model.points._
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

final case class WebCollection(id: Int, title: String, author: String, text: String, state: ExerciseState, shortName: String)
  extends ExerciseCollection

final case class WebExercise(id: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String, state: ExerciseState,
                             htmlText: Option[String], jsText: Option[String],
                             htmlTasks: Seq[HtmlTask], jsTasks: Seq[JsTask],
                             sampleSolutions: Seq[WebSampleSolution]) extends Exercise {

  override def baseValues: BaseValues = BaseValues(id, semanticVersion, title, author, text, state)

  // other methods

  override def preview: Html = // FIXME: move to toolMain!
    views.html.toolViews.web.webPreview(this)

  def maxPoints(part: WebExPart): Points = part match {
    case WebExParts.HtmlPart => addUp(htmlTasks.map(_.maxPoints))
    case WebExParts.JsPart   => addUp(jsTasks.map(_.maxPoints))
  }

  def tasksForPart(part: WebExPart): Seq[WebTask] = part match {
    case WebExParts.HtmlPart => htmlTasks
    case WebExParts.JsPart   => jsTasks
  }

}

sealed trait WebTask {

  val id        : Int
  val text      : String
  val xpathQuery: String

  def maxPoints: Points

}

final case class HtmlTask(id: Int, text: String, xpathQuery: String, textContent: Option[String], attributes: Seq[HtmlAttribute]) extends WebTask {

  override def maxPoints: Points = (1 point) + (textContent map (_ => 1 point) getOrElse (0 points)) + (attributes.size points)

}

final case class JsTask(id: Int, text: String, xpathQuery: String, actionType: JsActionType, keysToSend: Option[String], conditions: Seq[JsCondition]) extends WebTask {

  override def maxPoints: Points = (1 point) + (conditions.size points)

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

class WebExTag(part: String, hasExes: Boolean) extends ExTag {

  override def cssClass: String = if (hasExes) "label label-primary" else "label label-default"

  override def buttonContent: String = part

  override def title: String = s"Diese Aufgabe besitzt ${if (!hasExes) "k" else ""}einen $part-Teil"

}


final case class HtmlAttribute(key: String, value: String)

final case class JsCondition(id: Int, xpathQuery: String, isPrecondition: Boolean, awaitedValue: String) {

  def description: String = s"""Element mit XPath <code>$xpathQuery</code> sollte den Inhalt <code>$awaitedValue</code> haben"""

  def maxPoints: Points = 1 point

}

final case class WebSolution(htmlSolution: String, jsSolution: Option[String])

final case class WebSampleSolution(id: Int, sample: WebSolution)
  extends SampleSolution[WebSolution]

final case class WebUserSolution(id: Int, part: WebExPart, solution: WebSolution, points: Points, maxPoints: Points)
  extends UserSolution[WebExPart, WebSolution]

final case class WebExerciseReview(difficulty: Difficulty, maybeDuration: Option[Int]) extends ExerciseReview
