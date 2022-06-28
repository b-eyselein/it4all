package initialData

import better.files.File
import com.google.inject.AbstractModule
import initialData.InitialData.exerciseResourcesPath
import model._
import model.mongo.MongoClientQueries
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
  topicsWithLevels: Seq[TopicWithLevel] = Seq.empty,
  difficulty: Int,
  content: EC
)

final case class InitialCollection[EC <: ExerciseContent](
  title: String,
  authors: Seq[String],
  initialExercises: Map[Int, InitialExercise[EC]] = Map.empty
)

trait InitialData[EC <: ExerciseContent] {

  type InitialEx = InitialExercise[EC]

  val initialData: Map[Int, InitialCollection[EC]]

}

object InitialData {

  private val baseResourcesPath = File.currentWorkingDirectory / "conf" / "resources"

  def exerciseResourcesPath(toolId: String, collectionId: Int, exerciseId: Int): File = baseResourcesPath / toolId / s"coll_$collectionId" / s"ex_$exerciseId"

  def loadTextFromFile(file: File): String = file.contentAsString

}

@Singleton
class StartUpService @Inject() (mongoQueries: MongoClientQueries)(implicit ec: ExecutionContext) {

  private val logger = Logger(classOf[StartUpService])

  private def insertInitialCollection(coll: ExerciseCollection): Future[Unit] = mongoQueries
    .futureCollectionById(coll.toolId, coll.collectionId)
    .flatMap {
      case Some(_) => Future.successful(())
      case None =>
        val key = s"(${coll.toolId}, ${coll.collectionId})"

        mongoQueries
          .futureInsertCollection(coll)
          .map {
            case false => logger.error(s"Could not insert collection $key!")
            case true  => logger.debug(s"Inserted collection $key.")
          }
          .recover { case e =>
            logger.error("Error while inserting collection", e)
          }
    }

  private def insertInitialExercise[EC <: ExerciseContent](ex: Exercise[EC], exFormat: OFormat[Exercise[EC]]): Future[Unit] =
    mongoQueries
      .futureExerciseExists(ex.toolId, ex.collectionId, ex.exerciseId)
      .flatMap {
        case true => Future.successful(())
        case false =>
          val key = s"(${ex.toolId}, ${ex.collectionId}, ${ex.exerciseId})"

          mongoQueries
            .futureInsertExercise(ex, exFormat)
            .map {
              case false => logger.error(s"Exercise $key could not be inserted!")
              case true  => logger.debug(s"Inserted exercise $key.")
            }
            .recover { case e =>
              logger.error("Error while inserting exercise", e)
            }
      }

  ToolList.tools.foreach { tool =>
    // Insert all collections and exercises
    tool.initialData.initialData.foreach { case (collectionId, InitialCollection(title, authors, initialExercises)) =>
      insertInitialCollection(ExerciseCollection(collectionId, tool.id, title, authors))

      initialExercises.foreach { case (exerciseId, InitialExercise(title, authors, text, topicsWithLevels, difficulty, content)) =>
        val exercise = Exercise(exerciseId, collectionId, tool.id, title, authors, text, topicsWithLevels, difficulty, content)

        insertInitialExercise(exercise, tool.jsonFormats.exerciseFormat)
      }
    }
  }

}

class InitialDataModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[StartUpService]).asEagerSingleton()
  }

}
