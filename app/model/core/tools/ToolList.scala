package model.core.tools

import scala.collection.mutable

object ToolList {

  implicit val toolObjOrdering: Ordering[ToolObject] = Ordering.by(_.toolname)

  private var allTools: mutable.ListBuffer[ToolObject] = mutable.ListBuffer.empty

  def +=(toolObject: ToolObject): Unit = allTools += toolObject

  def allToolObjects: Seq[ToolObject] = allTools.sortBy(_.toolname)

  lazy val idExTools: Seq[ExToolObject] = allTools.flatMap {
    case toolObj: ExToolObject         => Some(toolObj)
    case _                             => None
  }.sortBy(_.toolname)

}
