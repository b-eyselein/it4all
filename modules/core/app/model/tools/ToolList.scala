package model.tools

import scala.collection.mutable.MutableList

import play.mvc.Call

case class Tool(name: String, state: ToolState, index: Call, decoration: String = null)

case class ToolGroup(name: String, tools: List[Tool])

object ToolList {

  var toolList: MutableList[ToolObject] = MutableList.empty

  def register(tool: ToolObject) = toolList += tool

}
