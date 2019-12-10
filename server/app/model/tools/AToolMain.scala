package model.tools

import better.files._
import model.core.CoreConsts._

abstract class AToolMain(consts: ToolConsts) {

  // Other members

  final val toolName : String    = consts.toolName
  final val urlPart  : String    = consts.toolId
  final val toolState: ToolState = consts.toolState

  val hasTags      : Boolean = false
  val hasPlayground: Boolean = false

  // Folders

  protected val exerciseResourcesFolder: File = File.currentWorkingDirectory / "conf" / "resources" / urlPart

  def solutionDirForExercise(username: String, collId: Int, exId: Int): File =
    File.currentWorkingDirectory / "data" / urlPart / solutionsSubDir / username / String.valueOf(collId) / String.valueOf(exId)

}
