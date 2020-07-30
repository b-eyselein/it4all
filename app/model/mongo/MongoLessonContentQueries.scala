package model.mongo

import model.{JsonProtocols, LessonContent}
import play.api.libs.json.OFormat
import play.modules.reactivemongo.ReactiveMongoComponents
import reactivemongo.api.Cursor
import reactivemongo.api.bson.BSONDocument
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.play.json.compat.json2bson._

import scala.concurrent.{ExecutionContext, Future}

trait MongoLessonContentQueries {
  self: ReactiveMongoComponents =>

  protected implicit val ec: ExecutionContext

  private implicit val lessonContentFormat: OFormat[LessonContent] = JsonProtocols.lessonContentFormat

  private def futureLessonContentsCollection: Future[BSONCollection] =
    reactiveMongoApi.database.map(_.collection("lessonContents"))

  protected def futureLessonContentCountForLesson(toolId: String, lessonId: Int): Future[Long] =
    for {
      lessonContentsCollection <- futureLessonContentsCollection
      lessonContentCount <-
        lessonContentsCollection.count(Some(BSONDocument("toolId" -> toolId, "lessonId" -> lessonId)))
    } yield lessonContentCount

  protected def futureLessonContentsForLesson(toolId: String, lessonId: Int): Future[Seq[LessonContent]] = {
    for {
      lessonContentsCollection <- futureLessonContentsCollection
      lessonContents <-
        lessonContentsCollection
          .find(BSONDocument("toolId" -> toolId, "lessonId" -> lessonId), Option.empty[BSONDocument])
          .sort(BSONDocument("contentId" -> 1))
          .cursor[LessonContent]()
          .collect[Seq](-1, Cursor.FailOnError())
    } yield lessonContents
  }

  protected def futureLessonContentById(toolId: String, lessonId: Int, contentId: Int): Future[Option[LessonContent]] =
    for {
      lessonContentsCollection <- futureLessonContentsCollection
      maybeLessonContent <-
        lessonContentsCollection
          .find(
            BSONDocument("toolId" -> toolId, "lessonId" -> lessonId, "contentId" -> contentId),
            Option.empty[BSONDocument]
          )
          .one[LessonContent]
    } yield maybeLessonContent

  protected def futureInsertLessonContent(lessonContent: LessonContent): Future[Boolean] =
    for {
      lessonContentCollection <- futureLessonContentsCollection
      insertResult            <- lessonContentCollection.insert(true).one(lessonContent)
    } yield insertResult.writeErrors.isEmpty

  protected def futureUpsertLessonContent(lessonContent: LessonContent): Future[Boolean] = {
    val key = BSONDocument(
      "toolId"    -> lessonContent.toolId,
      "lessonId"  -> lessonContent.lessonId,
      "contentId" -> lessonContent.contentId
    )

    for {
      lessonContentCollection <- futureLessonContentsCollection
      insertResult            <- lessonContentCollection.update(true).one(key, lessonContent, upsert = true)
    } yield insertResult.writeErrors.isEmpty
  }

}
