package model.core.tools

import model.Consts
import model.Enums.ToolState
import play.api.mvc.Call

case class ExerciseOptions(tool: String, aceMode: String, minLines: Int, maxLines: Int, updatePrev: Boolean)

trait ToolObject {

  val hasTags: Boolean = false
  val toolname: String
  val exType  : String
  val consts  : Consts


  val toolState: ToolState = ToolState.LIVE

  ToolList += this

  def indexCall: Call

}

trait RandomExToolObject extends ToolObject
