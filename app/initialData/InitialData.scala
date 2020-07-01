package initialData

import better.files.File
import com.google.inject.AbstractModule
import javax.inject.{Inject, Singleton}
import model.mongo.MongoClientQueries
import model.tools.ToolList
import model._
import play.api.Logger
import play.api.libs.json.OFormat
import play.modules.reactivemongo.{ReactiveMongoApi, ReactiveMongoComponents}

import scala.concurrent.{ExecutionContext, Future}

trait InitialData[EC <: ExerciseContent] {

  protected val toolId: String

  val lessonData: Seq[(Lesson, Seq[LessonContent])] = Seq.empty

  val exerciseData: Seq[(ExerciseCollection, Seq[Exercise[EC]])]

}

object InitialData {

  private val baseResourcesPath = File.currentWorkingDirectory / "conf" / "resources"

  def lessonResourcesPath(toolId: String, lessonId: Int): File =
    baseResourcesPath / toolId / s"lesson_$lessonId"

  def exerciseResourcesPath(toolId: String, collectionId: Int, exerciseId: Int): File =
    baseResourcesPath / toolId / s"coll_$collectionId" / s"ex_$exerciseId"

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

    futureCollectionById(coll.toolId, coll.collectionId).flatMap {
      case Some(_) =>
        Future.successful(logger.info(s"Collection $key already exists."))
      case None =>
        futureInsertCollection(coll).map {
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

    futureExerciseById(ex.toolId, ex.collectionId, ex.exerciseId, exFormat).flatMap {
      case Some(_) =>
        Future.successful(logger.info(s"Exercise $key already exists."))
      case None =>
        futureInsertExercise(ex, exFormat).map {
          case false => logger.error(s"Exercise $key could not be inserted!")
          case true  => logger.info(s"Inserted exercise $key.")
        }
    }
  }

  private def insertInitialLesson(lesson: Lesson): Future[Unit] = {
    val key = s"(${lesson.toolId}, ${lesson.lessonId})"

    futureLessonById(lesson.toolId, lesson.lessonId).flatMap {
      case Some(_) => Future.successful(logger.info(s"Lesson $key already exists."))
      case None =>
        futureInsertLesson(lesson).map {
          case false => logger.error(s"Could not insert lesson $key")
          case true  => logger.info(s"Inserted lesson $key")
        }
    }
  }

  private def insertInitialLessonContent(content: LessonContent): Future[Unit] = {
    val key = s"(${content.toolId}, ${content.lessonId}, ${content.contentId})"

    futureLessonContentById(content.toolId, content.lessonId, content.contentId).flatMap {
      case Some(_) => Future.successful(logger.info(s"LessonContent $key already exists."))
      case None =>
        futureInsertLessonContent(content).map {
          case false => logger.error(s"Could not insert lesson content $key")
          case true  => logger.info(s"Insert lesson content $key")
        }
    }
  }

  ToolList.tools.foreach { tool =>
    // Insert all collections and exercises
    tool.initialData.exerciseData.foreach {
      case (coll, exes) =>
        insertInitialCollection(coll)

        exes.foreach(ex => insertInitialExercise(ex, tool.jsonFormats.exerciseFormat))
    }

    // Insert all lessons
    tool.initialData.lessonData.foreach {
      case (lesson, lessonContents) =>
        insertInitialLesson(lesson)

        lessonContents.foreach(lessonContent => insertInitialLessonContent(lessonContent))
    }
  }

}

class InitialDataModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[StartUpService]).asEagerSingleton()
  }

}
