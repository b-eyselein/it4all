package initialData

import better.files.File
import com.google.inject.AbstractModule
import javax.inject.{Inject, Singleton}
import model._
import model.mongo.MongoClientQueries
import model.tools.ToolList
import play.api.Logger
import play.api.libs.json.{Json, OFormat}
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

  def loadTextFromFile(file: File): String = file.contentAsString

}

@Singleton
class StartUpService @Inject() (override val reactiveMongoApi: ReactiveMongoApi)
    extends ReactiveMongoComponents
    with MongoClientQueries {

  private val logger = Logger(classOf[StartUpService])

  override protected implicit val ec: ExecutionContext = ExecutionContext.global

  private def insertInitialCollection(coll: ExerciseCollection): Future[Unit] =
    futureCollectionById(coll.toolId, coll.collectionId)
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

  private def insertInitialExercise[EC <: ExerciseContent](
    ex: Exercise[EC],
    exFormat: OFormat[Exercise[EC]]
  ): Future[Unit] =
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

  private def upsertInitialLesson(lesson: Lesson): Future[Unit] = {
    val key = Json.obj("toolId" -> lesson.toolId, "lessonId" -> lesson.lessonId)

    futureUpsertLesson(lesson)
      .map {
        case false => logger.error(s"Could not insert lesson $key")
        case true  => logger.debug(s"Inserted lesson $key")
      }
      .recover { case e =>
        logger.error("Error while inserting lesson", e)
      }
  }

  private def upsertInitialLessonContent(lessonContent: LessonContent): Future[Unit] = {
    val key = Json.obj(
      "toolId"    -> lessonContent.toolId,
      "lessonId"  -> lessonContent.lessonId,
      "contentId" -> lessonContent.contentId
    )

    futureUpsertLessonContent(lessonContent)
      .map {
        case false => logger.error(s"Could not insert lesson content $key")
        case true  => logger.debug(s"Insert lesson content $key")
      }
      .recover { case e =>
        logger.error("Error while inserting lesson", e)
      }
  }

  ToolList.tools.foreach { tool =>
    // Insert all collections and exercises
    tool.initialData.exerciseData.foreach { case (coll, exes) =>
      insertInitialCollection(coll)

      exes.foreach(ex => insertInitialExercise(ex, tool.jsonFormats.exerciseFormat))
    }

    // Insert all lessons
    tool.initialData.lessonData.foreach { case (lesson, lessonContents) =>
      upsertInitialLesson(lesson)

      lessonContents.foreach(lessonContent => upsertInitialLessonContent(lessonContent))
    }
  }

}

class InitialDataModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[StartUpService]).asEagerSingleton()
  }

}
