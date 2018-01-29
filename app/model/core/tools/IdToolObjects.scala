package model.core.tools

import java.nio.file.{Path, Paths}

import controllers.exes.idPartExes.{ExPart, ExParts}
import model._
import model.core.CoreConsts._
import model.core.FileUtils
import play.api.mvc.Call

import scala.language.postfixOps

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

  def correctRoute(exercise: HasBaseValues): Call

  def correctLiveRoute(exercise: HasBaseValues): Call

  // Admin

  def adminIndexRoute: Call

  def adminExesListRoute: Call

  def newExFormRoute: Call

  def createNewExRoute: Call

  def exportExesRoute: Call

  def exportExesAsFileRoute: Call

  def importExesRoute: Call

  def changeExStateRoute(exercise: HasBaseValues): Call

  def editExerciseFormRoute(exercise: HasBaseValues): Call

  def editExerciseRoute(exercise: HasBaseValues): Call

  def deleteExerciseRoute(exercise: HasBaseValues): Call

}

trait IdExToolObject extends ExToolObject {

  def exerciseRoute(exercise: HasBaseValues): Call

  override def exerciseRoutes(exercise: CompEx): Map[Call, String] = Map(exerciseRoute(exercise.ex) -> "Aufgabe bearbeiten")

}

trait IdPartExToolObject[Part <: ExPart] extends ExToolObject {

  override type CompEx <: PartsCompleteEx[_ <: Exercise, Part]

  def exParts: Seq[Part]

  def exerciseRoute(exercise: HasBaseValues, part: String): Call

  override def exerciseRoutes(exercise: CompEx): Map[Call, String] = exParts.flatMap { exPart =>
    if (exercise.hasPart(exPart)) {
      Some((exerciseRoute(exercise.ex, exPart.urlName), exPart.partName))
    } else None
  } toMap

  //  override def exerciseRoutes(exercise: CompEx): Map[Call, String] =
  //    exParts map (exPart => (exerciseRoute(exercise.ex, exPart._1.toLowerCase), exPart._2))

}

trait CollectionToolObject extends model.core.tools.ExToolObject with FileUtils {

  override type CompEx <: CompleteEx[_ <: ExerciseInCollection]

  val collectionSingularName: String

  val collectionPluralName: String

  def exerciseRoute(collectionId: Int, exerciseId: Int): Call

  def collectionRoute(id: Int, page: Int = 1): Call

  def filteredCollectionRoute(id: Int, filter: String, page: Int = 1): Call

  override def exerciseRoutes(exercise: CompEx): Map[Call, String] = Map(exerciseRoute(exercise.ex.collectionId, exercise.id) -> "Aufgabe bearbeiten")

}

trait FileExToolObject extends ExToolObject {

  override type CompEx <: FileCompleteEx[_ <: Exercise]

  val fileTypes: Map[String, String]

  def exerciseRoute(exercise: HasBaseValues, fileExtension: String): Call

  override def exerciseRoutes(exercise: CompEx): Map[Call, String] =
    fileTypes filter (ft => exercise.available(ft._1)) map (ft => (exerciseRoute(exercise.ex, ft._1), s"Mit ${ft._2} bearbeiten"))

  def uploadSolutionRoute(exercise: HasBaseValues, fileExtension: String): Call

  def downloadCorrectedRoute(exercise: HasBaseValues, fileExtension: String): Call

}