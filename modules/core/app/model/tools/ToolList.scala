package model.tools

import scala.collection.mutable.MutableList

object ToolList {

  var randomExTools: MutableList[RandomExToolObject] = MutableList.empty

  var idExTools: MutableList[IdExToolObject] = MutableList.empty

  def register(tool: ToolObject): Unit = tool match {
    case r: RandomExToolObject => randomExTools += r
    case i: IdExToolObject => idExTools += i
    case _ => println(s"Error while registering tool object $tool")
  }

  def allTools: MutableList[ToolObject] = randomExTools ++ idExTools

}
