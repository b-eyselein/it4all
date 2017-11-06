package model.core.tools

import java.nio.file.{Path, Paths}

import model.DbExercise
import model.Enums.ToolState
import model.core.HasBaseValues
import play.api.mvc.Call

case class ExerciseOptions(tool: String, aceMode: String, minLines: Int, maxLines: Int, updatePrev: Boolean)

abstract sealed class ToolObject(val exType: String, val toolname: String, val state: ToolState) {

  ToolList.allTools += this

  def indexCall: Call

}

// Random ex: Bool, Nary, ...

abstract class RandomExToolObject(e: String, t: String, s: ToolState) extends ToolObject(e, t, s)

// Exercises with single id: xml, spread, mindmap, ...

abstract class ExToolObject(e: String, t: String, s: ToolState, val pluralName: String)
  extends ToolObject(e, t, s) {

  // Methods for files...

  val rootDir: String = "data"

  val SAMPLE_SUB_DIRECTORY    = "samples"
  val SOLUTIONS_SUB_DIRECTORY = "solutions"

  val resourcesFolder: Path = Paths.get("conf", "resources", exType)

  val sampleDir: Path = Paths.get(rootDir, exType, SAMPLE_SUB_DIRECTORY)

  val solutionDir: Path = Paths.get(rootDir, exType, SOLUTIONS_SUB_DIRECTORY)

  def solDirForUser(username: String): Path = Paths.get(solutionDir.toString, username)

  def getSampleDirForExercise(exerciseType: String, exercise: DbExercise): Path =
    Paths.get(sampleDir.toString, String.valueOf(exercise.id))

  def getSolDirForExercise(username: String, exercise: DbExercise): Path =
    Paths.get(solDirForUser(username).toString, exType, String.valueOf(exercise.id))

  def getSolFileForExercise(username: String, ex: DbExercise, fileName: String, fileExt: String): Path =
    Paths.get(getSolDirForExercise(username, ex).toString, s"$fileName.$fileExt")

  // User

  def exesListRoute(page: Int): Call

  def exerciseRoutes(exercise: HasBaseValues): List[(Call, String)]

  // Admin

  def restHeaders: List[String]

  def adminIndexRoute: Call

  def adminExesListRoute: Call

  def newExFormRoute: Call

  //  def exportExesRoute: Call

  //  def importExesRoute: Call

  //  def jsonSchemaRoute: Call

  //  def uploadFileRoute: Call

  def changeExStateRoute(exercise: HasBaseValues): Call

  def editExerciseFormRoute(exercise: HasBaseValues): Call

  def editExerciseRoute(exercise: HasBaseValues): Call

  def deleteExerciseRoute(exercise: HasBaseValues): Call

}

abstract class IdExToolObject(e: String, t: String, s: ToolState = ToolState.ALPHA, p: String = "Aufgaben")
  extends ExToolObject(e, t, s, p) {

  def exerciseRoute(exercise: HasBaseValues): Call

  override def exerciseRoutes(exercise: HasBaseValues): List[(Call, String)] = List((exerciseRoute(exercise), "Aufgabe bearbeiten"))

  def correctLiveRoute(exercise: HasBaseValues): Call

  def correctRoute(exercise: HasBaseValues): Call

}

// TODO: Exercise statt HasBaseValues...
abstract class IdPartExToolObject(e: String, t: String, s: ToolState = ToolState.ALPHA, d: String = null, p: String = "Aufgaben")
  extends ExToolObject(e, t, s, p) {

  def exerciseRoute(exercise: HasBaseValues, part: String): Call

  def correctLiveRoute(exercise: HasBaseValues, part: String): Call

  def correctRoute(exercise: HasBaseValues, part: String): Call

}