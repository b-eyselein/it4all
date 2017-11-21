package model.core.tools

import java.nio.file.{Path, Paths}

import model.Enums.ToolState
import model.core.CoreConsts._
import model.core.FileUtils
import model.{CompleteEx, Consts, HasBaseValues}
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

  type CompEx <: CompleteEx[_]

  val pluralName: String = "Aufgaben"

  val rootDir: String = "data"

  val resourcesFolder: Path = Paths.get("conf", "resources")

  lazy val exerciseResourcesFolder: Path = resourcesFolder / exType

  lazy val exerciseRootDir: Path = Paths.get(rootDir, exType)

  lazy val sampleDir: Path = exerciseRootDir / SAMPLE_SUB_DIRECTORY

  lazy val solutionDir: Path = exerciseRootDir / SOLUTIONS_SUB_DIRECTORY

  def solDirForUser(username: String): Path = solutionDir / username

  def getSampleDirForExercise(exerciseType: String, exercise: HasBaseValues): Path = sampleDir / String.valueOf(exercise.id)

  def getSolDirForExercise(username: String, exercise: HasBaseValues): Path = solDirForUser(username) / String.valueOf(exercise.id)

  def getSolFileForExercise(username: String, ex: HasBaseValues, fileName: String, fileExt: String): Path = getSolDirForExercise(username, ex) / s"$fileName.$fileExt"

  // User

  def exerciseListRoute(page: Int): Call

  def exerciseRoutes(exercise: CompEx): List[(Call, String)]

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