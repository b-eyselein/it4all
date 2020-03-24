package model.tools

import better.files._

abstract class AToolMain(consts: ToolConsts) {

  // Other members

  final val toolName: String     = consts.toolName
  final val toolState: ToolState = consts.toolState

  /**
    * @deprecated: move to id!
    */
  final val urlPart: String = consts.toolId

  val hasTags: Boolean       = false
  val hasPlayground: Boolean = false

  def id: String = urlPart

  // Folders

  // protected val exerciseResourcesFolder: File = File.currentWorkingDirectory / "conf" / "resources" / urlPart

  def solutionDirForExercise(username: String, collId: Int, exId: Int): File =
    File.currentWorkingDirectory / "data" / urlPart / "solutions" / username / String.valueOf(collId) / String.valueOf(
      exId
    )

}
