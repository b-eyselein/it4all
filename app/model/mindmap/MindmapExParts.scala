package model.mindmap

import model.core.ExPart

sealed abstract class MindmapExPart(val partName: String, val urlName: String) extends ExPart

case object MindManagerPart extends MindmapExPart("mmap", "MindJet MindManager")

object MindmapExParts {

  val values: Seq[MindmapExPart] = Seq(MindManagerPart)

}
