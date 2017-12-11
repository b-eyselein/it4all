package model.core.tools

import java.nio.file.{Path, Paths}

import model.Enums.ToolState
import model.core.CoreConsts._
import model.core.FileUtils
import model.{CompleteEx, Consts, Exercise, HasBaseValues}
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

trait ExToolObject extends ToolObject with FileUtils {

  // Methods for files...
  // Important: exType is initialized later ...

  type CompEx <: CompleteEx[_ <: Exercise]

  val pluralName: String = "Aufgaben"

  val rootDir: String = "data"

  val resourcesFolder: Path = Paths.get("conf", "resources")

  lazy val exerciseResourcesFolder: Path = resourcesFolder / exType

  lazy val exerciseRootDir: Path = Paths.get(rootDir, exType)

  val hasExType: Boolean = false


  def sampleDirForExercise(exercise: HasBaseValues): Path = exerciseRootDir / SAMPLE_SUB_DIRECTORY / String.valueOf(exercise.id)

  def templateDirForExercise(exercise: HasBaseValues): Path = exerciseRootDir / TEMPLATE_SUB_DIRECTORY / String.valueOf(exercise.id)

  def solutionDirForExercise(username: String, exercise: HasBaseValues): Path = exerciseRootDir / SOLUTIONS_SUB_DIRECTORY / username / String.valueOf(exercise.id)

  // User

  def exerciseListRoute(page: Int): Call

  def exerciseRoutes(exercise: CompEx): Map[Call, String]

  // Admin

  def restHeaders: List[String]

  def adminIndexRoute: Call

  def adminExesListRoute: Call

  def newExFormRoute: Call

  def exportExesRoute: Call

  def exportExesAsFileRoute: Call

  def importExesRoute: Call

  def changeExStateRoute(exercise: HasBaseValues): Call

  def editExerciseFormRoute(exercise: HasBaseValues): Call

  def editExerciseRoute(exercise: HasBaseValues): Call

  def deleteExerciseRoute(exercise: HasBaseValues): Call

}