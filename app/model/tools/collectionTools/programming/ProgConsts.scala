package model.tools.collectionTools.programming

import model.tools.{ToolConsts, ToolState}

object ProgConsts extends ToolConsts {

  override val toolName: String     = "Programmierung"
  override val toolId: String       = "programming"
  override val toolState: ToolState = ToolState.ALPHA

  val baseDataName: String = "baseData"
  val testDataName: String = "testData"

}
