package initialData

import better.files.File
import com.google.inject.AbstractModule
import initialData.InitialData.exerciseResourcesPath
import model._
import model.tools.ToolList
import play.api.Logger
import play.api.libs.json.OFormat

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

abstract class InitialExerciseContainer(
  protected val toolId: String,
  protected val collectionId: Int,
  protected val exerciseId: Int
) {

  protected val exResPath: File = exerciseResourcesPath(toolId, collectionId, exerciseId)

}

case class InitialExercise[EC <: ExerciseContent](
  title: String,
  authors: Seq[String],
  text: String,
  topicsWithLevels: Map[Topic, Level] = Map.empty,
  difficulty: Int,
  content: EC
)

final case class InitialCollection[EC <: ExerciseContent](
  title: String,
  initialExercises: Map[Int, InitialExercise[EC]] = Map.empty
)

trait InitialData[EC <: ExerciseContent] {

  val initialData: Map[Int, InitialCollection[EC]]

}

object InitialData {

  private val baseResourcesPath = File.currentWorkingDirectory / "conf" / "resources"

  def exerciseResourcesPath(toolId: String, collectionId: Int, exerciseId: Int): File = baseResourcesPath / toolId / s"coll_$collectionId" / s"ex_$exerciseId"

  def loadTextFromFile(file: File): String = file.contentAsString

}

@Singleton
class StartUpService @Inject() (tableDefs: TableDefs)(implicit ec: ExecutionContext) {

  private val logger = Logger(classOf[StartUpService])

  private def insertInitialCollection(coll: ExerciseCollection): Future[Unit] = for {
    maybeCollection <- tableDefs.futureCollectionById(coll.toolId, coll.collectionId)

    inserted <- maybeCollection match {
      case Some(_) => Future.successful(())
      case None =>
        val key = s"(${coll.toolId}, ${coll.collectionId})"

        tableDefs
          .futureInsertCollection(coll)
          .map {
            case false => logger.error(s"Could not insert collection $key!")
            case true  => logger.debug(s"Inserted collection $key.")
          }
          .recover { case e =>
            logger.error(s"Error while inserting collection $key", e)
          }
    }
  } yield inserted

  private def insertInitialExercise[EC <: ExerciseContent](ex: Exercise[EC], exContentFormat: OFormat[EC]): Future[Unit] = for {
    exerciseExists <- tableDefs.futureExerciseExists(ex.toolId, ex.collectionId, ex.exerciseId)

    inserted <-
      if (exerciseExists) {
        Future.successful(())
      } else {
        val key = s"(${ex.toolId}, ${ex.collectionId}, ${ex.exerciseId})"

        tableDefs
          .futureInsertExercise(ex, exContentFormat)
          .map {
            case false => logger.error(s"Exercise $key could not be inserted!")
            case true  => logger.debug(s"Inserted exercise $key.")
          }
          .recover { case e =>
            logger.error(s"Error while inserting exercise $key", e)
          }
      }
  } yield inserted

  ToolList.tools.foreach { tool =>
    // Insert all collections and exercises

    tool.initialData.initialData.foreach { case (collectionId, InitialCollection(title, initialExercises)) =>
      for {
        _ <- insertInitialCollection(ExerciseCollection(tool.id, collectionId, title))

        _ = initialExercises.foreach { case (exerciseId, InitialExercise(title, authors, text, topicsWithLevelsMap, difficulty, content)) =>
          val topicsWithLevels = topicsWithLevelsMap.map { case (topic, level) => TopicWithLevel(topic, level) }.toSeq

          val exercise = Exercise(exerciseId, collectionId, tool.id, title, authors, text, topicsWithLevels, difficulty, content)

          insertInitialExercise(exercise, tool.jsonFormats.exerciseContentFormat)
        }
      } yield ()
    }
  }

}

class InitialDataModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[StartUpService]).asEagerSingleton()
  }

}
