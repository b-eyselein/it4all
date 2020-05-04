package model

import model.json.JsonProtocols
import model.lesson.Lesson
import model.tools._
import play.api.libs.json.{Format, JsObject, Json, OFormat}
import play.modules.reactivemongo.MongoController
import reactivemongo.api.{Cursor, ReadConcern}
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}

trait MongoClientQueries {
  self: MongoController =>

  protected implicit val ec: ExecutionContext

  private implicit val userFormat: OFormat[User]                             = Json.format
  private implicit val lessonFormat: OFormat[Lesson]                         = JsonProtocols.lessonFormat
  private implicit val exerciseCollectionFormat: OFormat[ExerciseCollection] = JsonProtocols.collectionFormat

  private def userFilter(username: String): JsObject = Json.obj(
    "username" -> username
  )

  private def toolFilter(toolId: String): JsObject = Json.obj(
    "toolId" -> toolId
  )
  private def lessonFilter(toolId: String, lessonId: Int): JsObject = Json.obj(
    "toolId"   -> toolId,
    "lessonId" -> lessonId
  )

  private def collectionFilter(toolId: String, collectionId: Int): JsObject = Json.obj(
    "toolId"       -> toolId,
    "collectionId" -> collectionId
  )

  private def exerciseFilter(toolId: String, collectionId: Int, exerciseId: Int): JsObject = Json.obj(
    "toolId"       -> toolId,
    "collectionId" -> collectionId,
    "exerciseId"   -> exerciseId
  )

  // Collections

  private def futureUsersCollection: Future[JSONCollection] = database.map(_.collection("users"))

  private def futureLessonsCollection: Future[JSONCollection] = database.map(_.collection("lessons"))

  private def futureCollectionsCollection: Future[JSONCollection] = database.map(_.collection("exercise_collections"))

  private def futureExercisesCollection: Future[JSONCollection] = database.map(_.collection("exercises"))

  private def futureUserSolutionsCollection: Future[JSONCollection] = database.map(_.collection("solutions"))

  // User queries

  protected def getUser(username: String): Future[Option[User]] =
    for {
      usersCollection <- futureUsersCollection
      maybeUser       <- usersCollection.find(userFilter(username)).one[User]
    } yield maybeUser

  protected def insertUser(user: User): Future[Boolean] =
    for {
      usersCollection <- futureUsersCollection
      insertResult    <- usersCollection.insert(true).one(user)
    } yield insertResult.n == 1

  // Lesson queries

  protected def lessonCountForTool(toolId: String): Future[Long] =
    for {
      lessonsCollection <- futureLessonsCollection
      count             <- lessonsCollection.count(Some(toolFilter(toolId)), None, 0, None, ReadConcern.Local)
    } yield count

  protected def lessonsForTool(toolId: String): Future[Seq[Lesson]] =
    for {
      lessonsCollection <- futureLessonsCollection
      lessons <- lessonsCollection
        .find(toolFilter(toolId))
        .cursor[Lesson]()
        .collect[Seq](-1, Cursor.FailOnError())
    } yield lessons

  protected def getLesson(toolId: String, lessonId: Int): Future[Option[Lesson]] =
    for {
      lessonsCollection <- futureLessonsCollection
      maybeLesson       <- lessonsCollection.find(lessonFilter(toolId, lessonId)).one[Lesson]
    } yield maybeLesson

  // Collection queries

  protected def getCollectionCount(toolId: String): Future[Long] =
    for {
      collectionCollection <- futureCollectionsCollection
      collectionCount      <- collectionCollection.count(Some(toolFilter(toolId)), None, 0, None, ReadConcern.Local)
    } yield collectionCount

  protected def getExerciseCollections(toolId: String): Future[Seq[ExerciseCollection]] =
    for {
      collectionsCollection <- futureCollectionsCollection
      collections <- collectionsCollection
        .find(toolFilter(toolId))
        .cursor[ExerciseCollection]()
        .collect[Seq](-1, Cursor.FailOnError())
    } yield collections

  protected def getExerciseCollection(toolId: String, collectionId: Int): Future[Option[ExerciseCollection]] =
    for {
      collectionCollection <- futureCollectionsCollection
      maybeCollection <- collectionCollection
        .find(collectionFilter(toolId, collectionId))
        .one[ExerciseCollection]
    } yield maybeCollection

  protected def insertCollection(exerciseCollection: ExerciseCollection): Future[Boolean] =
    for {
      collectionsCollection <- futureCollectionsCollection
      insertResult          <- collectionsCollection.insert(true).one(exerciseCollection)
    } yield insertResult.ok

  // Exercise queries

  protected def getExerciseCountForTool(toolId: String): Future[Long] =
    for {
      exercisesCollection <- futureExercisesCollection
      exerciseCount       <- exercisesCollection.count(Some(toolFilter(toolId)), None, 0, None, ReadConcern.Local)
    } yield exerciseCount

  protected def getExercisesForTool(tool: CollectionTool): Future[Seq[Exercise[tool.SolType, tool.ExContentType]]] = {
    implicit val ef: Format[Exercise[tool.SolType, tool.ExContentType]] = tool.toolJsonProtocol.exerciseFormat

    for {
      exercisesCollection <- futureExercisesCollection
      exercises <- exercisesCollection
        .find(toolFilter(tool.id))
        .cursor[Exercise[tool.SolType, tool.ExContentType]]()
        .collect[Seq](-1, Cursor.FailOnError())
    } yield exercises
  }

  protected def getExerciseCountForCollection(toolId: String, collectionId: Int): Future[Long] =
    for {
      exercisesCollection <- futureExercisesCollection
      exerciseCount <- exercisesCollection
        .count(Some(collectionFilter(toolId, collectionId)), None, 0, None, ReadConcern.Local)
    } yield exerciseCount

  protected def getExercisesForCollection(
    tool: CollectionTool,
    collectionId: Int
  ): Future[Seq[Exercise[tool.SolType, tool.ExContentType]]] = {
    implicit val ef: Format[Exercise[tool.SolType, tool.ExContentType]] = tool.toolJsonProtocol.exerciseFormat

    for {
      exercisesCollection <- futureExercisesCollection
      exercises <- exercisesCollection
        .find(collectionFilter(tool.id, collectionId))
        .cursor[Exercise[tool.SolType, tool.ExContentType]]()
        .collect[Seq](-1, Cursor.FailOnError())
    } yield exercises
  }

  protected def getExercise[S, EC <: ExerciseContent[S]](
    tool: CollectionTool,
    collectionId: Int,
    exerciseId: Int,
    exerciseFormat: OFormat[Exercise[S, EC]]
  ): Future[Option[Exercise[S, EC]]] = {
    implicit val ef: OFormat[Exercise[S, EC]] = exerciseFormat

    for {
      exercisesCollection <- futureExercisesCollection
      maybeExercise <- exercisesCollection
        .find(exerciseFilter(tool.id, collectionId, exerciseId))
        .one[Exercise[S, EC]]
    } yield maybeExercise
  }

  protected def insertExercise[S, EC <: ExerciseContent[S]](
    exercise: Exercise[S, EC],
    exerciseFormat: OFormat[Exercise[S, EC]]
  ): Future[Boolean] = {
    implicit val ef: OFormat[Exercise[S, EC]] = exerciseFormat

    for {
      exercisesCollection <- futureExercisesCollection
      insertResult        <- exercisesCollection.insert(true).one(exercise)
    } yield insertResult.ok
  }

  // Solution queries

  protected def insertSolution[S](
    solution: UserSolution[S],
    solutionFormat: OFormat[UserSolution[S]]
  ): Future[Boolean] = {
    implicit val sf: OFormat[UserSolution[S]] = solutionFormat

    for {
      userSolutionsCollection <- futureUserSolutionsCollection
      insertResult            <- userSolutionsCollection.insert(true).one(solution)
    } yield insertResult.ok
  }

}
