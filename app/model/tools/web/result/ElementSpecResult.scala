package model.tools.web.result

import model.tools.web.sitespec.{JsAction, WebElementSpec}
import org.openqa.selenium.WebElement

// Text content and attributes

sealed abstract class TextResult {

  val maybeFoundContent: Option[String]

  val awaitedContent: String

}

final case class TextContentResult(
  awaitedContent: String,
  maybeFoundContent: Option[String]
) extends TextResult

final case class AttributeResult(
  key: String,
  awaitedContent: String,
  maybeFoundContent: Option[String]
) extends TextResult

// Single element

sealed trait ElementSpecResult {

  val elementSpec: WebElementSpec

}

final case class NoElementFoundElementSpecResult(elementSpec: WebElementSpec) extends ElementSpecResult

final case class TooManyElementsFoundElementSpecResult(elementSpec: WebElementSpec, count: Int) extends ElementSpecResult

final case class ElementFoundElementSpecResult(
  elementSpec: WebElementSpec,
  foundElement: WebElement,
  textContentResult: Option[TextContentResult],
  attributeResults: Seq[AttributeResult]
) extends ElementSpecResult

// Js Action

sealed trait JsActionResult {

  val jsAction: JsAction

}

final case class NoElementFoundJsActionResult(
  jsAction: JsAction
) extends JsActionResult

final case class TooManyElementsFoundJsActionResult(
  jsAction: JsAction,
  count: Int
) extends JsActionResult

final case class ElementFoundJsActionResult(
  jsAction: JsAction,
  foundElement: WebElement,
  actionPerformed: Boolean
) extends JsActionResult
