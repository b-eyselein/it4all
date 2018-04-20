package model.toolMains

import java.nio.file.{Path, Paths}

import model.Enums.ToolState
import model._
import model.core.CoreConsts._
import model.core._
import play.api.mvc.Call

abstract class AToolMain(val urlPart: String) extends FileUtils {

  // Abstract types

  type R <: EvaluationResult

  // Save this ToolMain

  ToolList.addTool(this)

  // Other members

  val toolname: String

  val consts: Consts

  val hasTags: Boolean = false

  val toolState: ToolState = ToolState.ALPHA

  val pluralName: String = "Aufgaben"

  // Learning paths for this module

  val learningPaths: Seq[CompleteLearningPath] = Seq.empty

  // Folders

  private val rootDir: String = "data"

  private val resourcesFolder: Path = Paths.get("conf", "resources")

  lazy val exerciseResourcesFolder: Path = resourcesFolder / urlPart

  protected lazy val exerciseRootDir: Path = Paths.get(rootDir, urlPart)

  def solutionDirForExercise(username: String, id: Int): Path = exerciseRootDir / solutionsSubDir / username / String.valueOf(id)

  // Calls

  def indexCall: Call

}
