package initialData

import better.files.File
import com.google.inject.AbstractModule
import javax.inject.{Inject, Singleton}
import model.lesson.Lesson
import model.tools.ToolList
import model.{Exercise, ExerciseCollection, ExerciseContent, MongoClientQueries}
import play.api.Logger
import play.api.libs.json.OFormat
import play.modules.reactivemongo.{ReactiveMongoApi, ReactiveMongoComponents}

import scala.concurrent.{ExecutionContext, Future}

trait InitialData[EC <: ExerciseContent] {

  protected val toolId: String

  val lessons: Seq[Lesson] = Seq.empty

  val data: Seq[(ExerciseCollection, Seq[Exercise[EC]])]

}

object InitialData {

  def exerciseResourcesPath(toolId: String, collectionId: Int, exerciseId: Int): File =
    File.currentWorkingDirectory / "conf" / "resources" / toolId / s"coll_$collectionId" / s"ex_$exerciseId"

  @deprecated
  def ex_resources_path(toolId: String, collectionId: Int, exerciseId: Int): File =
    exerciseResourcesPath(toolId, collectionId, exerciseId)

  def loadTextFromFile(file: File): String = file.contentAsString

  @deprecated
  def load_text_from_file(file: File): String = loadTextFromFile(file)

}

@Singleton
class StartUpService @Inject() (override val reactiveMongoApi: ReactiveMongoApi)
    extends ReactiveMongoComponents
    with MongoClientQueries {

  private val logger = Logger(classOf[StartUpService])

  override protected implicit val ec: ExecutionContext = ExecutionContext.global

  private def insertInitialCollection(coll: ExerciseCollection): Future[Unit] = {

    val key = s"(${coll.toolId}, ${coll.collectionId})"

    getExerciseCollection(coll.toolId, coll.collectionId).flatMap {
      case Some(_) =>
        Future.successful(logger.info(s"Collection $key already exists."))
      case None =>
        insertCollection(coll).map {
          case false => logger.error(s"Could not insert collection $key!")
          case true  => logger.info(s"Inserted collection $key.")
        }
    }
  }

  private def insertInitialExercise[EC <: ExerciseContent](
    ex: Exercise[EC],
    exFormat: OFormat[Exercise[EC]]
  ): Future[Unit] = {

    val key = s"(${ex.toolId}, ${ex.collectionId}, ${ex.exerciseId})"

    getExercise(ex.toolId, ex.collectionId, ex.exerciseId, exFormat).flatMap {
      case Some(_) =>
        Future.successful(logger.info(s"Exercise $key already exists."))
      case None =>
        insertExercise(ex, exFormat).map {
          case false => logger.error(s"Exercise $key could not be inserted!")
          case true  => logger.info(s"Inserted exercise $key.")
        }
    }
  }

  ToolList.tools.foreach { tool =>
    // Insert all collections and exercises
    tool.initialData.data.foreach {
      case (coll, exes) =>
        insertInitialCollection(coll)

        exes.foreach(ex => insertInitialExercise(ex, tool.jsonFormats.exerciseFormat))
    }

    // Insert all lessons
    tool.initialData.lessons.foreach { lesson =>
      println(lesson)
    }
  }

}

class InitialDataModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[StartUpService]).asEagerSingleton()
  }

}
