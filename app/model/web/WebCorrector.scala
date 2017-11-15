package model.web

import model.Enums.SuccessType._
import org.openqa.selenium.{By, SearchContext, WebElement}

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.util.{Failure, Success, Try}

object WebCorrector {

  def evaluate(task: WebCompleteTask, searchContent: SearchContext): WebResult = task match {
    case task: HtmlCompleteTask => evaluateHtmlTask(task, searchContent)
    case task: JsCompleteTask   => evaluateJsTask(task, searchContent)
  }

  def evaluateHtmlTask(completeHtmlTask: HtmlCompleteTask, searchContext: SearchContext): ElementResult = {
    val foundElements = searchContext.findElements(By.xpath(completeHtmlTask.task.xpathQuery)).asScala.toList

    val foundElement = foundElements match {
      case Nil            => None
      case foundEl :: Nil => Some(foundEl)
      case _ :: _         => None
    }

    val (attrResults, textResult) = foundElements match {
      case Nil                      => (Seq.empty, None)
      case firstFoundElement :: Nil => (completeHtmlTask.attributes map (evaluateAttribute(_, firstFoundElement)),
                                         completeHtmlTask.task.textContent map (TextContentResult(firstFoundElement.getText, _)))
      case _ :: _                   => (Seq.empty, None)
    }

    new ElementResult(completeHtmlTask, foundElement, attrResults, textResult)
  }

  def evaluateAttribute(attribute: Attribute, element: WebElement): AttributeResult =
    AttributeResult(attribute, Try(element getAttribute attribute.key))

  def evaluateConditions(context: SearchContext, conditions: Seq[JsCondition]): Seq[ConditionResult] = conditions map (testCondition(_, context))

  def evaluateJsTask(completeJsTask: JsCompleteTask, searchContext: SearchContext) =
    new JsWebResult(
      completeJsTask,
      evaluateConditions(searchContext, completeJsTask.conditions filter (_.isPrecondition)),
      completeJsTask.task.perform(searchContext),
      evaluateConditions(searchContext, completeJsTask.conditions filter (!_.isPrecondition)),
      Seq.empty)

  def testCondition(condition: JsCondition, searchContext: SearchContext): ConditionResult = Try(searchContext findElement By.xpath(condition.xpathQuery)) match {
    case Failure(_)       => ConditionResult(NONE, condition, null)
    case Success(element) =>
      val gottenValue = element.getText
      val success = if (gottenValue == condition.awaitedValue) COMPLETE else NONE
      ConditionResult(success, condition, gottenValue)
  }

}