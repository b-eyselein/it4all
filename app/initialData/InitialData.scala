package initialData

import better.files.File
import com.google.inject.AbstractModule
import initialData.InitialData.exerciseResourcesPath
import model._
import model.mongo.MongoClientQueries
import model.tools.ToolList
import play.api.Logger
import play.api.libs.json.OFormat
import play.modules.reactivemongo.{ReactiveMongoApi, ReactiveMongoComponents}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

abstract class InitialExercise(protected val toolId: String, protected val collectionId: Int, protected val exerciseId: Int) {

  protected val exResPath: File = exerciseResourcesPath(toolId, collectionId, exerciseId)

}

trait InitialData[EC <: ExerciseContent] {

  protected val toolId: String

  val exerciseData: Seq[(ExerciseCollection, Seq[Exercise[EC]])]

}

object InitialData {

  private val baseResourcesPath = File.currentWorkingDirectory / "conf" / "resources"

  def lessonResourcesPath(toolId: String, lessonId: Int): File = baseResourcesPath / toolId / s"lesson_$lessonId"

  def exerciseResourcesPath(toolId: String, collectionId: Int, exerciseId: Int): File = baseResourcesPath / toolId / s"coll_$collectionId" / s"ex_$exerciseId"

  def loadTextFromFile(file: File): String = file.contentAsString

}

@Singleton
class StartUpService @Inject() (override val reactiveMongoApi: ReactiveMongoApi) extends ReactiveMongoComponents with MongoClientQueries {

  private val logger = Logger(classOf[StartUpService])

  override protected implicit val ec: ExecutionContext = ExecutionContext.global

  private def insertInitialCollection(coll: ExerciseCollection): Future[Unit] = futureCollectionById(coll.toolId, coll.collectionId)
    .flatMap {
      case Some(_) => Future.successful(())
      case None =>
        val key = s"(${coll.toolId}, ${coll.collectionId})"

        futureInsertCollection(coll)
          .map {
            case false => logger.error(s"Could not insert collection $key!")
            case true  => logger.debug(s"Inserted collection $key.")
          }
          .recover { case e =>
            logger.error("Error while inserting collection", e)
          }
    }

  private def insertInitialExercise[EC <: ExerciseContent](ex: Exercise[EC], exFormat: OFormat[Exercise[EC]]): Future[Unit] =
    futureExerciseExists(ex.toolId, ex.collectionId, ex.exerciseId)
      .flatMap {
        case true => Future.successful(())
        case false =>
          val key = s"(${ex.toolId}, ${ex.collectionId}, ${ex.exerciseId})"

          futureInsertExercise(ex, exFormat)
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
    tool.initialData.exerciseData.foreach { case (coll, exes) =>
      insertInitialCollection(coll)

      exes.foreach(ex => insertInitialExercise(ex, tool.jsonFormats.exerciseFormat))
    }
  }

}

class InitialDataModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[StartUpService]).asEagerSingleton()
  }

}
