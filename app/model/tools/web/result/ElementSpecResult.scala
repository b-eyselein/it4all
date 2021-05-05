package model.tools.web.result

import model.tools.web.sitespec.{HtmlElementSpec, JsAction}
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

sealed trait ElementSpecResult[ES <: HtmlElementSpec] {

  val elementSpec: ES

}

final case class NoElementFoundElementSpecResult[ES <: HtmlElementSpec](elementSpec: ES) extends ElementSpecResult[ES]

final case class TooManyElementsFoundElementSpecResult[ES <: HtmlElementSpec](elementSpec: ES, count: Int)
    extends ElementSpecResult[ES]

final case class ElementFoundElementSpecResult[ES <: HtmlElementSpec](
  elementSpec: ES,
  foundElement: WebElement,
  textContentResult: Option[TextContentResult],
  attributeResults: Seq[AttributeResult]
) extends ElementSpecResult[ES]

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
