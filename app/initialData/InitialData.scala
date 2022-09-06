package initialData

import better.files.File
import com.google.inject.AbstractModule
import initialData.InitialData.exerciseResourcesPath
import model._
import model.tools.{Tool, ToolList}
import play.api.Logger

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

  private val logger = Logger(classOf[StartUpService])

  private def newInsertInitialExercise(tool: Tool, collectionId: Int, exerciseId: Int)(initialExercise: InitialExercise[tool.ExContType]): Future[Unit] =
    tableDefs.futureExerciseExists(tool.id, collectionId, exerciseId).flatMap {
      case true => Future.successful(())
      case false =>
        val InitialExercise(title, _ /* authors */, text, topicsWithLevels, difficulty, content) = initialExercise

        val key = (tool.id, collectionId, exerciseId)

        for {
          _ /* exerciseInserted */ <- tableDefs
            .futureInsertExercise(
              Exercise(exerciseId, collectionId, tool.id, title, text, difficulty, content),
              tool.jsonFormats.exerciseContentFormat
            )
            .recover { logger.error(s"Error while inserting exercise $key", _) }

          _ /* exerciseTopicsInserted */ <- tableDefs
            .futureInsertTopicsForExercise(tool.id, collectionId, exerciseId, topicsWithLevels)
            .recover { logger.error("Error while inserting topics for exercise", _) }
        } yield ()
    }

  private def newInsertInitialCollection(tool: Tool, collectionId: Int)(initialCollection: InitialCollection[tool.ExContType]): Future[Unit] =
    tableDefs.futureCollectionById(tool.id, collectionId).map {
      case Some(_) => Future.successful(())
      case None =>
        val InitialCollection(title, initialExercises) = initialCollection

        val key = (tool.id, collectionId)

        for {
          _ /* collectionInserted */ <- tableDefs
            .futureInsertCollection(ExerciseCollection(tool.id, collectionId, title))
            .recover { logger.error(s"Error while inserting collection $key", _) }

          _ /* exercisesInserted */ <- Future
            .sequence {
              initialExercises.toSeq.map { case (exerciseId, initialExercise) => newInsertInitialExercise(tool, collectionId, exerciseId)(initialExercise) }
            }
            .recover { logger.error(s"Error while inserting exercises for collection $key", _) }
        } yield ()
    }

  // Insert all collections and exercises

  for (tool <- ToolList.tools) {
    for {
      existingTopics <- tableDefs.futureTopicsForTool(tool.id)

      topicsToInsert = tool.initialData.topics.filterNot { existingTopics.contains }

      _ /* topicsInserted */ <- tableDefs
        .futureInsertTopics(topicsToInsert)
        .recover { logger.error("Error while inserting topics", _) }

      _ /* collectionsInserted */ <- Future
        .sequence {
          tool.initialData.initialCollections.map { case (collectionId, initialCollection) =>
            newInsertInitialCollection(tool, collectionId)(initialCollection)
          }
        }
        .recover { logger.error(s"Error while inserting collections for tool ${tool.id}", _) }
    } yield ()
  }

}

class InitialDataModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[StartUpService]).asEagerSingleton()
  }

}
