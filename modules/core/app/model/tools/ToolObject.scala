package model.tools

import java.nio.file.{Path, Paths}

import play.api.Configuration
import play.api.mvc.Call

abstract sealed class ToolObject(c: Configuration, exType: String, val toolname: String, val state: ToolState, val decoration: String) {

  ToolList.register(this)

  val rootDir: String = c.get[String]("datafolder")

  def indexCall: Call

  // Methods for files...

  val SAMPLE_SUB_DIRECTORY = "samples"
  val SOLUTIONS_SUB_DIRECTORY = "solutions"

  val resourcesFolder: Path = Paths.get("conf", "resources", exType)

  val sampleDir: Path = Paths.get(rootDir, exType, SAMPLE_SUB_DIRECTORY)

  val solutionDir: Path = Paths.get(rootDir, exType, SOLUTIONS_SUB_DIRECTORY)

  def solDirForUser(username: String): Path = Paths.get(solutionDir.toString, username)


  //  def getSampleDirForExercise(exerciseType: String, exercise: Exercise) =
  //    Paths.get(getSampleDir(exerciseType).toString(), String.valueOf(exercise.getId()))

  //  def getSolDirForExercise(username: String, exerciseType: String, exercise: Exercise) =
  //    Paths.get(getSolDirForUser(username).toString(), exerciseType, String.valueOf(exercise.getId()))

  //  def getSolFileForExercise(username: String, exType: String, ex: Exercise, fileName: String, fileExtension: String) =
  //    Paths.get(getSolDirForExercise(username, exType, ex).toString, s"$fileName.$fileExtension")

}

// Random ex: Bool, Nary, ...

abstract class RandomExToolObject(c: Configuration, e: String, t: String, s: ToolState, d: String = null)
  extends ToolObject(c, e, t, s, d)

// Exercises with single id: xml, spread, mindmap, ...

abstract class IdExToolObject
(c: Configuration, e: String, t: String, s: ToolState = ToolState.ALPHA, d: String = null, val pluralName: String = "Aufgaben")
  extends ToolObject(c, e, t, s, d) {

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

// Exercises with double id

abstract class DoubleIdExToolObject
(c: Configuration, e: String, t: String, s: ToolState = ToolState.ALPHA, d: String = "", val pluralName: String = "Aufgaben")
  extends ToolObject(c, e, t, s, d) {

  // User

  def exerciseRoute(id: Int, part: String): Call

  def exerciseRoutes(id: Int, part: String): List[(Call, String)] = List((exerciseRoute(id, part), "Aufgabe bearbeiten"))

  def exesListRoute(id: Int, part: String): Call

  def correctLiveRoute(id: Int, part: String): Call

  def correctRoute(id: Int, part: String): Call

}
