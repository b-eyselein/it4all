package initialData

import better.files.File
import com.google.inject.AbstractModule
import initialData.InitialData.exerciseResourcesPath
import model._
import model.tools.{Tool, ToolList}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

abstract class InitialExerciseContainer(
  protected val toolId: String,
  protected val collectionId: Int,
  protected val exerciseId: Int
) {

  protected val exResPath: File = exerciseResourcesPath(toolId, collectionId, exerciseId)

}

final case class InitialExercise[EC <: ExerciseContent](
  title: String,
  authors: Seq[String],
  text: String,
  topicsWithLevels: Map[Topic, Level] = Map.empty,
  difficulty: Level,
  content: EC
)

final case class InitialCollection[EC <: ExerciseContent](
  title: String,
  initialExercises: Map[Int, InitialExercise[EC]] = Map.empty
)

final case class InitialData[EC <: ExerciseContent](
  topics: Seq[Topic] = Seq.empty,
  initialCollections: Map[Int, InitialCollection[EC]]
)

object InitialData {

  // TODO: type InitialData[EC <: ExerciseContent] = Map[Int, InitialCollection[EC]]

  private val baseResourcesPath = File.currentWorkingDirectory / "conf" / "resources"

  def exerciseResourcesPath(toolId: String, collectionId: Int, exerciseId: Int): File = baseResourcesPath / toolId / s"coll_$collectionId" / s"ex_$exerciseId"

  def loadTextFromFile(file: File): String = file.contentAsString

}

@Singleton
class StartUpService @Inject() (tableDefs: TableDefs)(implicit ec: ExecutionContext) {

  private def newInsertInitialExercise(tool: Tool, collectionId: Int, exerciseId: Int)(initialExercise: InitialExercise[tool.ExContentType]): Future[Unit] =
    tableDefs.futureExerciseExists(tool.id, collectionId, exerciseId).flatMap {
      case true => Future.successful(())
      case false =>
        initialExercise match {
          case InitialExercise(title, _ /* authors */, text, topicsWithLevels, difficulty, content) =>
            for {
              _ /* exerciseInserted */ <- tableDefs.futureInsertExercise(
                Exercise(exerciseId, collectionId, tool.id, title, text, difficulty, content),
                tool.jsonFormats.exerciseContentFormat
              )

              _ /* exerciseTopicsInserted */ <- tableDefs.futureInsertTopicsForExercise(tool.id, collectionId, exerciseId, topicsWithLevels)
            } yield ()
        }
    }

  private def newInsertInitialCollection(tool: Tool, collectionId: Int)(initialCollection: InitialCollection[tool.ExContentType]): Future[Unit] =
    tableDefs.futureCollectionById(tool.id, collectionId).map {
      case Some(_) => Future.successful(())
      case None =>
        initialCollection match {
          case InitialCollection(title, initialExercises) =>
            for {
              _ /* collectionInserted */ <- tableDefs.futureInsertCollection(ExerciseCollection(tool.id, collectionId, title))

              _ /* exercisesInserted */ <- Future.sequence {
                initialExercises.toSeq.map { case (exerciseId, initialExercise) => newInsertInitialExercise(tool, collectionId, exerciseId)(initialExercise) }
              }
            } yield ()
        }
    }

  // Insert all collections and exercises

  for (tool <- ToolList.tools; InitialData(topics, initialCollections) = tool.initialData) {
    for {
      _ /* topicsInserted */ <- tableDefs.futureInsertTopics(topics)

      _ /* collectionsInserted */ <- Future.sequence {
        tool.initialData.initialCollections.map { case (collectionId, initialCollection) => newInsertInitialCollection(tool, collectionId)(initialCollection) }
      }

    } yield ()
  }

}

class InitialDataModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[StartUpService]).asEagerSingleton()
  }

}
