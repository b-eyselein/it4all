package model

import model.result.SuccessType
import model.task.{Condition, HtmlTask, JsWebTask, WebTask}
import org.openqa.selenium.{By, SearchContext, WebElement}

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.util.{Failure, Success, Try}

object WebCorrector {

  def evaluate(task: WebTask, searchContent: SearchContext): WebResult = task match {
    case task: HtmlTask => evaluateHtmlTask(task, searchContent)
    case task: JsWebTask => evaluateJsTask(task, searchContent)
  }

  def evaluateHtmlTask(task: HtmlTask, searchContext: SearchContext): ElementResult = {
    val foundElements = searchContext.findElements(By.xpath(task.xpathQuery)).asScala.toList

    val foundElement = foundElements match {
      case Nil => None
      case foundEl :: Nil => Some(foundEl)
      case foundEl :: _ => None
    }

    val (attrResults, textResult) = foundElements match {
      case Nil => (List.empty, None)

      case firstFoundElement :: Nil => (
        task.getAttributes.asScala.map(evaluateAttribute(_, firstFoundElement)).toList,
        Option(TextContentResult(firstFoundElement.getText, task.textContent))
      )

      case firstFoundElement :: _ => (List.empty, None)
    }

    new ElementResult(task, foundElement, attrResults, textResult)
  }

  def evaluateAttribute(attribute: Attribute, element: WebElement): AttributeResult =
    AttributeResult(attribute, Try(element.getAttribute(attribute.key)))

  def evaluateConditions(context: SearchContext, conditions: List[Condition]): List[ConditionResult] = conditions.map(testCondition(_, context))

  def evaluateJsTask(task: JsWebTask, searchContext: SearchContext) =
    new JsWebResult(
      task,
      evaluateConditions(searchContext, task.conditions.asScala.filter(_.isPrecondition).toList),
      task.action == null || task.action.perform(searchContext),
      evaluateConditions(searchContext, task.conditions.asScala.filter(_.isPostcondition).toList),
      List.empty
    )

  def testCondition(condition: Condition, searchContext: SearchContext): ConditionResult = Try(searchContext.findElement(By.xpath(condition.xpathQuery))) match {
    case Failure(_) => ConditionResult(SuccessType.NONE, condition, null)
    case Success(element) =>
      val gottenValue = element.getText
      val success = if (gottenValue.equals(condition.awaitedValue)) SuccessType.COMPLETE else SuccessType.NONE
      ConditionResult(success, condition, gottenValue)

  }

}