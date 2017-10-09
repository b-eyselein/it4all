package model

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.util.{ Failure, Success, Try }

import org.openqa.selenium.{ By, SearchContext }

import model.result.SuccessType
import model.task.{ Condition, HtmlTask, JsWebTask, WebTask }
import org.openqa.selenium.WebElement

object WebCorrector {

  def evaluate(task: WebTask, searchContent: SearchContext): WebResult = task match {
    case task: HtmlTask  => evaluateHtmlTask(task, searchContent)
    case task: JsWebTask => evaluateJsTask(task, searchContent)
    case _               => null
  }

  def evaluateHtmlTask(task: HtmlTask, searchContext: SearchContext) = {
    val foundElements = searchContext.findElements(By.xpath(task.xpathQuery)).asScala.toList

    val foundElement = foundElements match {
      case Nil                           => None
      case foundElement :: Nil           => Some(foundElement)
      case foundElement :: otherElements => None
    }

    val (attrResults, textResult) = foundElements match {
      case Nil => (List.empty, None)

      case firstFoundElement :: Nil => (
        task.getAttributes.asScala.map(evaluateAttribute(_, firstFoundElement)).toList,
        Option(new TextContentResult(firstFoundElement.getText(), task.textContent))
      )

      case firstFoundElement :: otherElements => (List.empty, None)

    }

    new ElementResult(task, foundElement, attrResults, textResult)
  }

  def evaluateAttribute(attribute: Attribute, element: WebElement) = try {
    val foundValue = element.getAttribute(attribute.key)
    new AttributeResult(attribute, foundValue)
  } catch { // NOSONAR
    case e: NoSuchMethodError => new AttributeResult(attribute, null)
  }

  def evaluateConditions(context: SearchContext, conditions: List[Condition]) = conditions.map(testCondition(_, context))

  def evaluateJsTask(task: JsWebTask, searchContext: SearchContext) =
    new JsWebResult(
      task,
      evaluateConditions(searchContext, task.conditions.asScala.filter(_.isPrecondition).toList),
      task.action == null || task.action.perform(searchContext),
      evaluateConditions(searchContext, task.conditions.asScala.filter(_.isPostcondition).toList),
      List.empty
    )

  def testCondition(condition: Condition, searchContext: SearchContext) =
    Try(searchContext.findElement(By.xpath(condition.xpathQuery))) match {
      case Failure(_) =>
        new ConditionResult(SuccessType.NONE, condition, null)
      case Success(element) =>
        val gottenValue = element.getText
        val success = if (gottenValue.equals(condition.awaitedValue)) SuccessType.COMPLETE else SuccessType.NONE
        new ConditionResult(success, condition, gottenValue)

    }

}