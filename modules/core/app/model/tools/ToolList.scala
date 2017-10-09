package model.tools

import scala.collection.mutable.MutableList

import play.mvc.Call

case class Tool(name: String, state: ToolState, index: Call, decoration: String = null)

case class ToolGroup(name: String, tools: List[Tool])

object ToolList {

  var randomExTools: MutableList[RandomExToolObject] = MutableList.empty

  var idExTools: MutableList[IdExToolObject] = MutableList.empty

  def register(tool: ToolObject) = tool match {
    case r: RandomExToolObject => randomExTools += r
    case i: IdExToolObject     => idExTools += i
    case _                     => println(s"Error while registering tool object $tool")
  }

  def allTools = randomExTools ++ idExTools

}
