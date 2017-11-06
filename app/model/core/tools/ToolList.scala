package model.core.tools

import scala.collection.mutable

object ToolList {

  implicit val toolObjOrdering: Ordering[ToolObject] = Ordering.by(_.toolname)

  var allTools: mutable.SortedSet[ToolObject] = mutable.SortedSet.empty

  lazy val idExTools: Seq[ExToolObject] = allTools.flatMap {
    case toolObj: ExToolObject => Some(toolObj)
    case _                     => None
  }.toSeq.sortBy(_.toolname)

}
