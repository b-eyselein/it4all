package model.tools.collectionTools.web

import model.tools.{ToolConsts, ToolState}

object WebConsts extends ToolConsts {

  override val toolName : String    = "Web"
  override val toolId   : String    = "web"
  override val toolState: ToolState = ToolState.LIVE

  val attributeResultsName: String = "attributeResults"

  val elementFoundName: String = "elementFound"

  val htmlResultsName: String = "htmlResults"

  val jsResultsName: String = "jsResults"

  val textContentResultName: String = "textContentResult"

}
