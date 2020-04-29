package model

import model.json.JsonProtocols
import model.lesson.Lesson
import model.tools._
import play.api.libs.json.{Format, JsObject, Json, OFormat}
import reactivemongo.api.{Cursor, DefaultDB, ReadConcern}
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}

trait MongoClientQueries {

  private implicit val lessonFormat: OFormat[Lesson]                         = JsonProtocols.lessonFormat
  private implicit val exerciseCollectionFormat: OFormat[ExerciseCollection] = JsonProtocols.collectionFormat

  private val lessonsCollectionName            = "lessons"
  private val exerciseCollectionCollectionName = "exercise_collections"
  private val exercisesCollectionName          = "exercises"
  private val solutionsCollectionName          = "solutions"

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

  // Lesson queries

  protected def lessonCountForTool(
    futureDefaulDB: Future[DefaultDB],
    toolId: String
  )(implicit ec: ExecutionContext): Future[Long] =
    for {
      db <- futureDefaulDB
      count <- db
        .collection[JSONCollection](lessonsCollectionName)
        .count(Some(toolFilter(toolId)), None, 0, None, ReadConcern.Local)
    } yield count

  protected def lessonsForTool(
    futureDefaultDB: Future[DefaultDB],
    toolId: String
  )(implicit ec: ExecutionContext): Future[Seq[Lesson]] =
    for {
      db <- futureDefaultDB
      lessons <- db
        .collection[JSONCollection](lessonsCollectionName)
        .find(toolFilter(toolId))
        .cursor[Lesson]()
        .collect[Seq](-1, Cursor.FailOnError())
    } yield lessons

  protected def getLesson(
    futureDefaultDB: Future[DefaultDB],
    toolId: String,
    lessonId: Int
  )(implicit ec: ExecutionContext): Future[Option[Lesson]] =
    for {
      db <- futureDefaultDB
      lesson <- db
        .collection[JSONCollection](lessonsCollectionName)
        .find(lessonFilter(toolId, lessonId))
        .one[Lesson]
    } yield lesson

  // Collection queries

  protected def getExerciseCountForTool(
    futureDefaultDB: Future[DefaultDB],
    toolId: String
  )(implicit ec: ExecutionContext): Future[Long] =
    for {
      db <- futureDefaultDB
      count <- db
        .collection[JSONCollection](exercisesCollectionName)
        .count(Some(toolFilter(toolId)), None, 0, None, ReadConcern.Local)
    } yield count

  protected def getCollectionCount(
    futureDefaultDB: Future[DefaultDB],
    toolId: String
  )(implicit ec: ExecutionContext): Future[Long] =
    for {
      db <- futureDefaultDB
      count <- db
        .collection[JSONCollection](exerciseCollectionCollectionName)
        .count(Some(toolFilter(toolId)), None, 0, None, ReadConcern.Local)
    } yield count

  protected def getExerciseCollections(
    defaultDB: Future[DefaultDB],
    toolId: String
  )(implicit ec: ExecutionContext): Future[Seq[ExerciseCollection]] =
    defaultDB
      .map(
        _.collection[JSONCollection](exerciseCollectionCollectionName)
          .find(toolFilter(toolId))
          .cursor[ExerciseCollection]()
      )
      .flatMap(_.collect[Seq](-1, Cursor.FailOnError()))

  protected def getExerciseCollection(
    defaultDB: Future[DefaultDB],
    toolId: String,
    collectionId: Int
  )(implicit ec: ExecutionContext): Future[Option[ExerciseCollection]] =
    for {
      db <- defaultDB
      exercise <- db
        .collection[JSONCollection](exerciseCollectionCollectionName)
        .find(collectionFilter(toolId, collectionId))
        .one[ExerciseCollection]
    } yield exercise

  protected def upsertExerciseCollection(
    defaultDB: Future[DefaultDB],
    exerciseCollection: ExerciseCollection,
    upsert: Boolean = true
  )(implicit ec: ExecutionContext): Future[Boolean] = {
    for {
      db <- defaultDB
      key = collectionFilter(exerciseCollection.toolId, exerciseCollection.collectionId)
      result <- db
        .collection[JSONCollection](exerciseCollectionCollectionName)
        .update(true)
        .one(key, exerciseCollection, upsert)
    } yield result.n == 1
  }

  // Exercise queries

  protected def getExercisesForTool(
    defaultDB: Future[DefaultDB],
    tool: CollectionTool
  )(implicit ec: ExecutionContext): Future[Seq[Exercise[tool.SolType, tool.ExContentType]]] = {
    implicit val ef: Format[Exercise[tool.SolType, tool.ExContentType]] = tool.toolJsonProtocol.exerciseFormat

    for {
      db <- defaultDB
      exercises <- db
        .collection[JSONCollection](exercisesCollectionName)
        .find(toolFilter(tool.id))
        .cursor[Exercise[tool.SolType, tool.ExContentType]]()
        .collect[Seq](-1, Cursor.FailOnError())
    } yield exercises
  }

  protected def getExerciseCountForCollection(
    defaultDB: Future[DefaultDB],
    toolId: String,
    collectionId: Int
  )(implicit ec: ExecutionContext): Future[Long] =
    for {
      db <- defaultDB
      count <- db
        .collection[JSONCollection](exercisesCollectionName)
        .count(Some(collectionFilter(toolId, collectionId)), None, 0, None, ReadConcern.Local)
    } yield count

  protected def getExercisesForCollection(
    defaultDB: Future[DefaultDB],
    tool: CollectionTool,
    collectionId: Int
  )(implicit ec: ExecutionContext): Future[Seq[Exercise[tool.SolType, tool.ExContentType]]] = {
    implicit val ef: Format[Exercise[tool.SolType, tool.ExContentType]] = tool.toolJsonProtocol.exerciseFormat

    for {
      db <- defaultDB
      exercises <- db
        .collection[JSONCollection](exercisesCollectionName)
        .find(collectionFilter(tool.id, collectionId))
        .cursor[Exercise[tool.SolType, tool.ExContentType]]()
        .collect[Seq](-1, Cursor.FailOnError())
    } yield exercises
  }

  protected def getExercise[S, EC <: ExerciseContent[S]](
    defaultDB: Future[DefaultDB],
    tool: CollectionTool,
    collectionId: Int,
    exerciseId: Int,
    exerciseFormat: OFormat[Exercise[S, EC]]
  )(implicit ec: ExecutionContext): Future[Option[Exercise[S, EC]]] = {
    implicit val ef: OFormat[Exercise[S, EC]] = exerciseFormat

    for {
      db <- defaultDB
      exercise <- db
        .collection[JSONCollection](exercisesCollectionName)
        .find(exerciseFilter(tool.id, collectionId, exerciseId))
        .one[Exercise[S, EC]]
    } yield exercise
  }

  protected def upsertExercise[S, EC <: ExerciseContent[S]](
    defaultDB: Future[DefaultDB],
    tool: CollectionTool,
    exercise: Exercise[S, EC]
  )(implicit exerciseFormat: OFormat[Exercise[S, EC]], ec: ExecutionContext): Future[Boolean] = ???

  // Solution queries

  protected def insertSolution[S](
    futureDefaultDB: Future[DefaultDB],
    solution: UserSolution[S],
    solutionFormat: OFormat[UserSolution[S]]
  )(implicit ec: ExecutionContext): Future[Boolean] = {
    implicit val sf: OFormat[UserSolution[S]] = solutionFormat

    for {
      db <- futureDefaultDB
      insertResult <- db
        .collection[JSONCollection](solutionsCollectionName)
        .insert(true)
        .one(solution)
    } yield insertResult.n == 1
  }

}
