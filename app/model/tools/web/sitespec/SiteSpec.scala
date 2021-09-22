package model.tools.web.sitespec

import enumeratum.{EnumEntry, PlayEnum}

// HtmlTask

final case class WebElementSpec(
  xpathQuery: String,
  awaitedTagName: String,
  awaitedTextContent: Option[String] = None,
  attributes: Map[String, String] = Map.empty
)

final case class HtmlTask(
  id: Int,
  text: String,
  elementSpec: WebElementSpec
)

// JsTask

sealed trait JsActionType extends EnumEntry

object JsActionType extends PlayEnum[JsActionType] {

  override val values: IndexedSeq[JsActionType] = findValues

  case object Click extends JsActionType

  case object FillOut extends JsActionType

}

final case class JsAction(xpathQuery: String, actionType: JsActionType, keysToSend: Option[String])

final case class JsTask(
  id: Int,
  text: String,
  preConditions: Seq[WebElementSpec],
  action: JsAction,
  postConditions: Seq[WebElementSpec]
)

// SiteSpec

final case class SiteSpec(
  fileName: String,
  htmlTasks: Seq[HtmlTask],
  jsTasks: Seq[JsTask] = Seq[JsTask]()
)
