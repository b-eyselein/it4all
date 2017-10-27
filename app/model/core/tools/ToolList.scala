package model.core.tools

import scala.collection.mutable

object ToolList {

  val toolObjOrdering: Ordering[ToolObject] = Ordering.by(_.toolname)

  var allTools: mutable.SortedSet[ToolObject] = mutable.SortedSet.empty(toolObjOrdering)

  lazy val idExTools: mutable.Set[ExToolObject] = allTools.flatMap {
    case toolObj: ExToolObject => Some(toolObj)
    case _                     => None
  }

}
