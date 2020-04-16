package model.persistence

import model.json.JsonProtocols
import model.lesson.{Lesson, LessonContent}
import model.tools._
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.libs.json.{JsValue, Reads, Writes}
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

trait ExerciseTableDefQueries extends HasDatabaseConfigProvider[JdbcProfile] {
  self: ExerciseTableDefs =>

  import profile.api._

  // Helpers

  private def collectionFilter(toolId: String, collId: Int): ExerciseCollectionsTable => Rep[Boolean] =
    coll => coll.toolId === toolId && coll.id === collId

  private def futureNextUserSolutionId(
    exerciseId: Int,
    collectionId: Int,
    toolId: String,
    username: String,
    part: ExPart
  ): Future[Int] = {
    implicit val ptct: profile.api.BaseColumnType[ExPart] = exPartColumnType

    for {
      maybeCurrentHighestId <- db.run(
        userSolutionsTQ
          .filter { userSolution =>
            userSolution.username === username &&
            userSolution.exerciseId === exerciseId &&
            userSolution.collectionId === collectionId &&
            userSolution.toolId === toolId &&
            userSolution.part === part
          }
          .map(_.id)
          .max
          .result
      )
    } yield maybeCurrentHighestId.fold(0)(_ + 1)
  }

  private def lessonFromDbLesson(dbLesson: DbLesson): Lesson = {
    val lessonContentSeqReads = Reads.seq(JsonProtocols.lessonContentFormat)

    dbLesson match {
      case DbLesson(id, toolId, title, description, contentJson) =>
        lessonContentSeqReads
          .reads(contentJson)
          .map(content => Lesson(id, toolId, title, description, content))
          .getOrElse(???)
    }
  }

  // Reading

  def futureCollectionCount(toolId: String): Future[Int] = db.run(
    collectionsTQ.filter(_.toolId === toolId).size.result
  )

  def futureAllCollections(toolId: String): Future[Seq[ExerciseCollection]] = db.run(
    collectionsTQ.filter(_.toolId === toolId).result
  )

  def futureCollById(toolId: String, collId: Int): Future[Option[ExerciseCollection]] = db.run(
    collectionsTQ
      .filter(collectionFilter(toolId, collId))
      .result
      .headOption
  )

  def futureLessonCount(toolId: String): Future[Int] = db.run(
    lessonsTQ.filter(_.toolId === toolId).size.result
  )

  def futureAllLessons(toolId: String): Future[Seq[Lesson]] =
    db.run(lessonsTQ.filter(_.toolId === toolId).result)
      .map { dbLessons: Seq[DbLesson] =>
        dbLessons.map(lessonFromDbLesson)
      }

  def futureLessonById(toolId: String, lessonId: Int): Future[Option[Lesson]] =
    db.run(
        lessonsTQ
          .filter(l => l.toolId === toolId && l.id === lessonId)
          .result
          .headOption
      )
      .map(maybeDbLesson => maybeDbLesson.map(lessonFromDbLesson))

  def futureExerciseCountForTool(toolId: String): Future[Int] = db.run(
    exercisesTQ
      .filter(_.toolId === toolId)
      .size
      .result
  )

  def futureExercisesForTool(toolId: String): Future[Seq[DbExercise]] = db.run(
    exercisesTQ
      .filter(_.toolId === toolId)
      .result
  )

  def futureExerciseCountInColl(toolId: String, collId: Int): Future[Int] = db.run(
    exercisesTQ
      .filter { ex =>
        ex.toolId === toolId && ex.collectionId === collId
      }
      .size
      .result
  )

  def futureExercisesInColl(toolId: String, collId: Int): Future[Seq[DbExercise]] = db.run(
    exercisesTQ.filter { ex =>
      ex.toolId === toolId && ex.collectionId === collId
    }.result
  )

  def futureExerciseById(toolId: String, collId: Int, id: Int): Future[Option[DbExercise]] = db.run(
    exercisesTQ
      .filter { ex =>
        ex.toolId === toolId && ex.collectionId === collId && ex.id === id
      }
      .result
      .headOption
  )

  // Saving

  def futureUpsertCollection(collection: ExerciseCollection): Future[Boolean] =
    db.run(collectionsTQ.insertOrUpdate(collection)).transform(_ == 1, identity)

  def futureUpsertExercise(exercise: DbExercise): Future[Boolean] =
    db.run(exercisesTQ.insertOrUpdate(exercise)).transform(_ == 1, identity)

  def futureUpsertLesson(lesson: Lesson): Future[Boolean] = {
    val lessonContentWrites: Writes[Seq[LessonContent]] = Writes.seq(JsonProtocols.lessonContentFormat)

    val dbLesson = DbLesson(
      lesson.id,
      lesson.toolId,
      lesson.title,
      lesson.description,
      lessonContentWrites.writes(lesson.content)
    )

    db.run(lessonsTQ.insertOrUpdate(dbLesson)).transform(_ == 1, identity)
  }

  def futureInsertSolution(
    username: String,
    exerciseId: Int,
    collectionId: Int,
    toolId: String,
    part: ExPart,
    solution: JsValue
  ): Future[Boolean] =
    for {
      nextSolutionId <- futureNextUserSolutionId(exerciseId, collectionId, toolId, username, part)

      dbUserSolution = DbUserSolution(
        nextSolutionId,
        exerciseId,
        collectionId,
        toolId,
        part,
        username,
        solution
      )

      inserted <- db.run(userSolutionsTQ += dbUserSolution).transform(_ == 1, identity)
    } yield inserted

  // Deletion

  def futureDeleteCollection(collId: Int): Future[Boolean] =
    db.run(collectionsTQ.filter(_.id === collId).delete).transform(_ == 1, identity)

  def futureDeleteExercise(collId: Int, id: Int): Future[Boolean] =
    db.run(exercisesTQ.filter(ex => ex.id === id && ex.collectionId === collId).delete).transform(_ == 1, identity)

}
