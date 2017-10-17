package model.tools

import java.nio.file.{Path, Paths}

import model.exercise.Exercise
import play.api.mvc.Call

abstract sealed class ToolObject(val exType: String, val toolname: String, val state: ToolState, val decoration: String) {

  ToolList.register(this)

  def indexCall: Call

}

// Random ex: Bool, Nary, ...

abstract class RandomExToolObject(e: String, t: String, s: ToolState, d: String = null)
  extends ToolObject(e, t, s, d)

// Exercises with single id: xml, spread, mindmap, ...

abstract class IdExToolObject
(e: String, t: String, s: ToolState = ToolState.ALPHA, d: String = null, val pluralName: String = "Aufgaben")
  extends ToolObject(e, t, s, d) {

  // Methods for files...

  val rootDir: String = "data"

  val SAMPLE_SUB_DIRECTORY = "samples"
  val SOLUTIONS_SUB_DIRECTORY = "solutions"

  val resourcesFolder: Path = Paths.get("conf", "resources", exType)

  val sampleDir: Path = Paths.get(rootDir, exType, SAMPLE_SUB_DIRECTORY)

  val solutionDir: Path = Paths.get(rootDir, exType, SOLUTIONS_SUB_DIRECTORY)

  def solDirForUser(username: String): Path = Paths.get(solutionDir.toString, username)

  def getSampleDirForExercise(exerciseType: String, exercise: Exercise): Path =
    Paths.get(sampleDir.toString, String.valueOf(exercise.getId))

  def getSolDirForExercise(username: String, exercise: Exercise): Path =
    Paths.get(solDirForUser(username).toString, exType, String.valueOf(exercise.id))

  def getSolFileForExercise(username: String, ex: Exercise, fileName: String, fileExt: String): Path =
    Paths.get(getSolDirForExercise(username, ex).toString, s"$fileName.$fileExt")

  // User

  def exerciseRoute(id: Int): Call

  def exerciseRoutes(id: Int): List[(Call, String)] = List((exerciseRoute(id), "Aufgabe bearbeiten"))

  def exesListRoute(id: Int): Call

  def correctLiveRoute(id: Int): Call

  def correctRoute(id: Int): Call

  // Admin

  def restHeaders: List[String]

  def adminIndexRoute: Call

  def exercisesRoute: Call

  def newExFormRoute: Call

  def exportExesRoute: Call

  def importExesRoute: Call

  def jsonSchemaRoute: Call

  def uploadFileRoute: Call

  def changeExStateRoute(id: Int): Call

  def editExerciseFormRoute(id: Int): Call

  def editExerciseRoute(id: Int): Call

  def deleteExerciseRoute(id: Int): Call

}
