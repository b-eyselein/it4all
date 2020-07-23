package model.mongo

import model.{JsonProtocols, LessonContent}
import play.api.libs.json.{JsObject, Json, OFormat}
import play.modules.reactivemongo.ReactiveMongoComponents
import reactivemongo.api.{Cursor, ReadConcern}
import reactivemongo.play.json.compat._
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}

trait MongoLessonContentQueries {
  self: ReactiveMongoComponents =>

  protected implicit val ec: ExecutionContext

  private implicit val lessonContentFormat: OFormat[LessonContent] = JsonProtocols.lessonContentFormat

  private def futureLessonContentsCollection: Future[JSONCollection] =
    reactiveMongoApi.database.map(_.collection("lessonContents"))

  protected def futureLessonContentCountForLesson(toolId: String, lessonId: Int): Future[Long] =
    for {
      lessonContentsCollection <- futureLessonContentsCollection
      lessonContentCount <- lessonContentsCollection.count(
        Some(Json.obj("toolId" -> toolId, "lessonId" -> lessonId)),
        None,
        0,
        None,
        ReadConcern.Local
      )
    } yield lessonContentCount

  protected def futureLessonContentsForLesson(toolId: String, lessonId: Int): Future[Seq[LessonContent]] = {
    for {
      lessonContentsCollection <- futureLessonContentsCollection
      lessonContents <-
        lessonContentsCollection
          .find(Json.obj("toolId" -> toolId, "lessonId" -> lessonId), Option.empty[JsObject])
          .sort(Json.obj("contentId" -> 1))
          .cursor[LessonContent]()
          .collect[Seq](-1, Cursor.FailOnError())
    } yield lessonContents
  }

  protected def futureLessonContentById(toolId: String, lessonId: Int, contentId: Int): Future[Option[LessonContent]] =
    for {
      lessonContentsCollection <- futureLessonContentsCollection
      maybeLessonContent <-
        lessonContentsCollection
          .find(Json.obj("toolId" -> toolId, "lessonId" -> lessonId, "contentId" -> contentId), Option.empty[JsObject])
          .one[LessonContent]
    } yield maybeLessonContent

  protected def futureInsertLessonContent(lessonContent: LessonContent): Future[Boolean] =
    for {
      lessonContentCollection <- futureLessonContentsCollection
      insertResult            <- lessonContentCollection.insert(true).one(lessonContent)
    } yield insertResult.ok

  protected def futureUpsertLessonContent(lessonContent: LessonContent): Future[Boolean] = {
    val key = Json.obj(
      "toolId"    -> lessonContent.toolId,
      "lessonId"  -> lessonContent.lessonId,
      "contentId" -> lessonContent.contentId
    )

    for {
      lessonContentCollection <- futureLessonContentsCollection
      insertResult            <- lessonContentCollection.update(true).one(key, lessonContent, upsert = true)
    } yield insertResult.ok
  }

}
