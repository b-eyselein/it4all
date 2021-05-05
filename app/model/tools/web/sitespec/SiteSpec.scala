package model.tools.web.sitespec

import enumeratum.{EnumEntry, PlayEnum}

sealed trait HtmlElementSpec {
  val id: Int
  val xpathQuery: String
  val awaitedTagName: String
  val awaitedTextContent: Option[String]
  val attributes: Map[String, String]
}

sealed trait WebTask {
  val id: Int
  val text: String
}

// HtmlTask

final case class HtmlTask(
  id: Int,
  text: String,
  xpathQuery: String,
  awaitedTagName: String,
  awaitedTextContent: Option[String] = None,
  attributes: Map[String, String] = Map.empty
) extends WebTask
    with HtmlElementSpec

// JsTask

sealed trait JsActionType extends EnumEntry

object JsActionType extends PlayEnum[JsActionType] {

  override val values: IndexedSeq[JsActionType] = findValues

  case object Click extends JsActionType

  case object FillOut extends JsActionType

}

final case class JsAction(xpathQuery: String, actionType: JsActionType, keysToSend: Option[String])

final case class JsHtmlElementSpec(
  id: Int,
  xpathQuery: String,
  awaitedTagName: String,
  awaitedTextContent: Option[String] = None,
  attributes: Map[String, String] = Map.empty
) extends HtmlElementSpec

final case class JsTask(
  id: Int,
  text: String,
  preConditions: Seq[JsHtmlElementSpec],
  action: JsAction,
  postConditions: Seq[JsHtmlElementSpec]
) extends WebTask

// SiteSpec

final case class SiteSpec(
  fileName: String,
  htmlTasks: Seq[HtmlTask],
  jsTasks: Seq[JsTask] = Seq[JsTask]()
)
