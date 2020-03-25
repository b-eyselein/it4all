package model.persistence

import model._
import model.lesson.{Lesson, LessonContent}
import model.tools.collectionTools._
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

  private def solutionFilter(exercise: Exercise, user: User, part: ExPart): UserSolutionsTable => Rep[Boolean] = {
    implicit val ptct: profile.api.BaseColumnType[ExPart] = exPartColumnType

    userSolution =>
      userSolution.username === user.username &&
        userSolution.exerciseId === exercise.id &&
        userSolution.collectionId === exercise.collectionId &&
        userSolution.toolId === exercise.toolId &&
        userSolution.part === part
  }

  private def futureNextUserSolutionId(exercise: Exercise, user: User, part: ExPart): Future[Int] =
    for {
      maybeCurrentHighestId <- db.run(
        userSolutionsTQ.filter(solutionFilter(exercise, user, part)).map(_.id).max.result
      )
    } yield maybeCurrentHighestId.fold(0)(_ + 1)

  private def lessonFromDbLesson(dbLesson: DbLesson): Lesson = {
    val lessonContentSeqReads = Reads.seq(ToolJsonProtocol.lessonContentFormat)

    dbLesson match {
      case DbLesson(id, toolId, title, contentJson) =>
        lessonContentSeqReads
          .reads(contentJson)
          .map(content => Lesson(id, toolId, title, content))
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

  def futureExercisesForTool(toolId: String): Future[Seq[Exercise]] = db.run(
    exercisesTQ
      .filter(_.toolId === toolId)
      .result
  )

  @deprecated
  def futureExerciseMetaDataForTool(toolId: String): Future[Seq[ExerciseMetaData]] = db.run(
    exercisesTQ
      .filter { ex =>
        ex.toolId === toolId
      }
      .result
      .map { exes =>
        exes.map(ExerciseMetaData.forExercise)
      }
  )

  def futureExerciseMetaDataForCollection(toolId: String, collId: Int): Future[Seq[ExerciseMetaData]] = db.run(
    exercisesTQ
      .filter { ex =>
        ex.toolId === toolId && ex.collectionId === collId
      }
      .result
      .map { exes =>
        exes.map(ExerciseMetaData.forExercise)
      }
  )

  def futureExerciseCountInColl(toolId: String, collId: Int): Future[Int] = db.run(
    exercisesTQ
      .filter { ex =>
        ex.toolId === toolId && ex.collectionId === collId
      }
      .size
      .result
  )

  def futureExercisesInColl(toolId: String, collId: Int): Future[Seq[Exercise]] = db.run(
    exercisesTQ.filter { ex =>
      ex.toolId === toolId && ex.collectionId === collId
    }.result
  )

  def futureExerciseById(toolId: String, collId: Int, id: Int): Future[Option[Exercise]] = db.run(
    exercisesTQ
      .filter { ex =>
        ex.toolId === toolId && ex.collectionId === collId && ex.id === id
      }
      .result
      .headOption
  )

  def futureCollectionAndExercise(
    toolId: String,
    collectionId: Int,
    exerciseId: Int
  ): Future[Option[(ExerciseCollection, Exercise)]] =
    for {
      collection <- futureCollById(toolId, collectionId)
      exercise   <- futureExerciseById(toolId, collectionId, exerciseId)
    } yield collection zip exercise

  // Saving

  def futureUpsertCollection(collection: ExerciseCollection): Future[Boolean] =
    db.run(collectionsTQ.insertOrUpdate(collection)).transform(_ == 1, identity)

  def futureUpsertExercise(exercise: Exercise): Future[Boolean] =
    db.run(exercisesTQ.insertOrUpdate(exercise)).transform(_ == 1, identity)

  def futureUpsertLesson(lesson: Lesson): Future[Boolean] = {
    val lessonContentWrites: Writes[Seq[LessonContent]] = Writes.seq(ToolJsonProtocol.lessonContentFormat)

    val dbLesson = DbLesson(lesson.id, lesson.toolId, lesson.title, lessonContentWrites.writes(lesson.content))

    db.run(lessonsTQ.insertOrUpdate(dbLesson)).transform(_ == 1, identity)
  }

  def futureInsertSolution(user: User, exercise: Exercise, part: ExPart, solution: JsValue): Future[Boolean] =
    for {
      nextSolutionId <- futureNextUserSolutionId(exercise, user, part)

      dbUserSolution = DbUserSolution(
        nextSolutionId,
        exercise.id,
        exercise.collectionId,
        exercise.toolId,
        exercise.semanticVersion,
        part,
        user.username,
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
