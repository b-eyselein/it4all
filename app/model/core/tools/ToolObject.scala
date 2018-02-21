package model.core.tools

import model.Enums.ToolState
import model.{Consts, ExTag}
import play.api.mvc.Call

case class ExerciseOptions(tool: String, aceMode: String, minLines: Int, maxLines: Int, updatePrev: Boolean)

trait ToolObject {

  val hasTags : Boolean
  val toolname: String
  val exType  : String
  val consts  : Consts


  val toolState: ToolState = ToolState.LIVE

  ToolList += this

  def indexCall: Call

}

trait RandomExToolObject extends ToolObject
