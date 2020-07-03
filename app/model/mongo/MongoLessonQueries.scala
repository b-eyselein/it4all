package model.mongo

import model.{JsonProtocols, Lesson}
import play.api.libs.json.{JsObject, Json, OFormat}
import play.modules.reactivemongo.ReactiveMongoComponents
import reactivemongo.api.{Cursor, ReadConcern}
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}

trait MongoLessonQueries {
  self: ReactiveMongoComponents =>

  protected implicit val ec: ExecutionContext

  private implicit val lessonFormat: OFormat[Lesson] = JsonProtocols.lessonFormat

  protected def futureLessonsCollection: Future[JSONCollection] = reactiveMongoApi.database.map(_.collection("lessons"))

  protected def futureLessonCountForTool(toolId: String): Future[Long] =
    for {
      lessonsCollection <- futureLessonsCollection
      count             <- lessonsCollection.count(Some(Json.obj("toolId" -> toolId)), None, 0, None, ReadConcern.Local)
    } yield count

  protected def futureLessonsForTool(toolId: String): Future[Seq[Lesson]] =
    for {
      lessonsCollection <- futureLessonsCollection
      lessons <-
        lessonsCollection
          .find(Json.obj("toolId" -> toolId), Option.empty[JsObject])
          .cursor[Lesson]()
          .collect[Seq](-1, Cursor.FailOnError())
    } yield lessons

  protected def futureLessonById(toolId: String, lessonId: Int): Future[Option[Lesson]] =
    for {
      lessonsCollection <- futureLessonsCollection
      maybeLesson <-
        lessonsCollection
          .find(Json.obj("toolId" -> toolId, "lessonId" -> lessonId), Option.empty[JsObject])
          .one[Lesson]
    } yield maybeLesson

  protected def futureInsertLesson(lesson: Lesson): Future[Boolean] =
    for {
      lessonsCollection <- futureLessonsCollection
      insertResult      <- lessonsCollection.insert(true).one(lesson)
    } yield insertResult.ok

}
