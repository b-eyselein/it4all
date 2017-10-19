package model.tools

object ToolList {

  var randomExTools: scala.collection.mutable.MutableList[RandomExToolObject] = scala.collection.mutable.MutableList.empty

  var idExTools: scala.collection.mutable.MutableList[ExToolObject] = scala.collection.mutable.MutableList.empty

  def register(tool: ToolObject): Unit = tool match {
    case r: RandomExToolObject => randomExTools += r
    case i: ExToolObject => idExTools += i
    case _ => println(s"Error while registering tool object $tool")
  }

  def allTools: scala.collection.mutable.MutableList[ToolObject] = randomExTools ++ idExTools

}
