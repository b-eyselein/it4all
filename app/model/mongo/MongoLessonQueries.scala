package model.mongo

import model.{JsonProtocols, Lesson}
import play.api.libs.json.OFormat
import play.modules.reactivemongo.ReactiveMongoComponents
import reactivemongo.api.Cursor
import reactivemongo.api.bson.BSONDocument
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.play.json.compat.json2bson._

import scala.concurrent.{ExecutionContext, Future}

trait MongoLessonQueries {
  self: ReactiveMongoComponents =>

  protected implicit val ec: ExecutionContext

  private implicit val lessonFormat: OFormat[Lesson] = JsonProtocols.lessonFormat

  private def futureLessonsCollection: Future[BSONCollection] = reactiveMongoApi.database.map(_.collection("lessons"))

  protected def futureLessonCountForTool(toolId: String): Future[Long] =
    for {
      lessonsCollection <- futureLessonsCollection
      count             <- lessonsCollection.count(Some(BSONDocument("toolId" -> toolId)))
    } yield count

  protected def futureLessonsForTool(toolId: String): Future[Seq[Lesson]] =
    for {
      lessonsCollection <- futureLessonsCollection
      lessons <-
        lessonsCollection
          .find(BSONDocument("toolId" -> toolId), Option.empty[BSONDocument])
          .sort(BSONDocument("lessonId" -> 1))
          .cursor[Lesson]()
          .collect[Seq](-1, Cursor.FailOnError())
    } yield lessons

  protected def futureLessonById(toolId: String, lessonId: Int): Future[Option[Lesson]] =
    for {
      lessonsCollection <- futureLessonsCollection
      maybeLesson <-
        lessonsCollection
          .find(BSONDocument("toolId" -> toolId, "lessonId" -> lessonId), Option.empty[BSONDocument])
          .one[Lesson]
    } yield maybeLesson

  protected def futureInsertLesson(lesson: Lesson): Future[Boolean] =
    for {
      lessonsCollection <- futureLessonsCollection
      insertResult      <- lessonsCollection.insert(true).one(lesson)
    } yield insertResult.writeErrors.isEmpty

  protected def futureUpsertLesson(lesson: Lesson): Future[Boolean] = {

    val key = BSONDocument("toolId" -> lesson.toolId, "lessonId" -> lesson.lessonId)

    for {
      lessonsCollection <- futureLessonsCollection
      insertResult      <- lessonsCollection.update(true).one(key, lesson, upsert = true)
    } yield insertResult.writeErrors.isEmpty
  }

}
