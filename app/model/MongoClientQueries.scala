package model

import model.json.JsonProtocols
import model.lesson.Lesson
import model.tools.Helper.UntypedExercise
import model.tools._
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoComponents
import reactivemongo.api.{Cursor, ReadConcern}
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}

trait MongoClientQueries extends ReactiveMongoComponents {

  protected implicit val ec: ExecutionContext

  private implicit val userFormat: OFormat[User]     = Json.format
  private implicit val lessonFormat: OFormat[Lesson] = JsonProtocols.lessonFormat
  private implicit val exerciseCollectionKeyFormat: OFormat[ExerciseCollectionKey] =
    JsonProtocols.exerciseCollectionKeyFormat
  private implicit val exerciseKeyFormat: OFormat[ExerciseKey]               = JsonProtocols.exerciseKeyFormat
  private implicit val exerciseCollectionFormat: OFormat[ExerciseCollection] = JsonProtocols.exerciseCollectionFormat
  private implicit val topicFormat: OFormat[Topic]                           = JsonProtocols.topicFormat
  private implicit val userProficiencyFormat: OFormat[UserProficiency]       = JsonProtocols.userProficiencyFormat

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

  private def exercisePartFilter[P](
    toolId: String,
    collectionId: Int,
    exerciseId: Int,
    part: P
  )(implicit pf: Format[P]): JsObject = Json.obj(
    "toolId"       -> toolId,
    "collectionId" -> collectionId,
    "exerciseId"   -> exerciseId,
    "part"         -> part
  )

  // Collections

  private def futureUsersCollection: Future[JSONCollection] = reactiveMongoApi.database.map(_.collection("users"))

  private def futureLessonsCollection: Future[JSONCollection] = reactiveMongoApi.database.map(_.collection("lessons"))

  private def futureCollectionsCollection: Future[JSONCollection] =
    reactiveMongoApi.database.map(_.collection("exerciseCollections"))

  private def futureExercisesCollection: Future[JSONCollection] =
    reactiveMongoApi.database.map(_.collection("exercises"))

  private def futureUserSolutionsCollection: Future[JSONCollection] =
    reactiveMongoApi.database.map(_.collection("userSolutions"))

  private def futureUserProficienciesCollection: Future[JSONCollection] =
    reactiveMongoApi.database.map(_.collection("userProficiencies"))

  // User queries

  protected def getUser(username: String): Future[Option[User]] =
    for {
      usersCollection <- futureUsersCollection
      maybeUser <- usersCollection
        .find(userFilter(username), Option.empty[JsObject])
        .one[User]
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
        .find(toolFilter(toolId), Option.empty[JsObject])
        .cursor[Lesson]()
        .collect[Seq](-1, Cursor.FailOnError())
    } yield lessons

  protected def getLesson(toolId: String, lessonId: Int): Future[Option[Lesson]] =
    for {
      lessonsCollection <- futureLessonsCollection
      maybeLesson <- lessonsCollection
        .find(lessonFilter(toolId, lessonId), Option.empty[JsObject])
        .one[Lesson]
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
        .find(toolFilter(toolId), Option.empty[JsObject])
        .sort(Json.obj("collectionId" -> 1))
        .cursor[ExerciseCollection]()
        .collect[Seq](-1, Cursor.FailOnError())
    } yield collections

  protected def getExerciseCollection(toolId: String, collectionId: Int): Future[Option[ExerciseCollection]] = {

    val key = ExerciseCollectionKey(collectionId, toolId)

    for {
      collectionCollection <- futureCollectionsCollection
      maybeCollection <- collectionCollection
        .find(key, Option.empty[JsObject])
        .one[ExerciseCollection]
    } yield maybeCollection
  }

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
    implicit val ef: Format[Exercise[tool.SolType, tool.ExContentType]] = tool.jsonFormats.exerciseFormat

    for {
      exercisesCollection <- futureExercisesCollection
      exercises <- exercisesCollection
        .find(toolFilter(tool.id), Option.empty[JsObject])
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
    implicit val ef: Format[Exercise[tool.SolType, tool.ExContentType]] = tool.jsonFormats.exerciseFormat

    for {
      exercisesCollection <- futureExercisesCollection
      exercises <- exercisesCollection
        .find(collectionFilter(tool.id, collectionId), Option.empty[JsObject])
        .sort(Json.obj("exerciseId" -> 1))
        .cursor[Exercise[tool.SolType, tool.ExContentType]]()
        .collect[Seq](-1, Cursor.FailOnError())
    } yield exercises
  }

  protected def getExercise[S, EC <: ExerciseContent[S]](
    toolId: String,
    collectionId: Int,
    exerciseId: Int,
    exerciseFormat: OFormat[Exercise[S, EC]]
  ): Future[Option[Exercise[S, EC]]] = {
    implicit val ef: OFormat[Exercise[S, EC]] = exerciseFormat

    val exKey = ExerciseKey(exerciseId, collectionId, toolId)

    for {
      exercisesCollection <- futureExercisesCollection
      maybeExercise <- exercisesCollection
        .find(exKey, Option.empty[JsObject])
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

  protected def nextUserSolutionId[P](exercise: UntypedExercise, part: P)(implicit pf: Format[P]): Future[Int] = {
    val exFilter = exercisePartFilter(exercise.toolId, exercise.collectionId, exercise.exerciseId, part)

    final case class ThisResult(solutionId: Int)

    implicit val resultFormat: Reads[ThisResult] = (__ \ "solutionId").read[Int].map(ThisResult.apply _)

    for {
      userSolutionsCollection <- futureUserSolutionsCollection
      x <- userSolutionsCollection
        .find(exFilter, Some(Json.obj("solutionId" -> 1, "_id" -> 0)))
        .sort(Json.obj("solutionId" -> -1))
        .one[ThisResult]
    } yield x match {
      case None        => 1
      case Some(jsObj) => jsObj.solutionId + 1
    }
  }

  protected def insertSolution[S, P <: ExPart](
    solution: UserSolution[S, P],
    solutionFormat: OFormat[UserSolution[S, P]]
  ): Future[Boolean] = {
    implicit val sf: OFormat[UserSolution[S, P]] = solutionFormat

    for {
      userSolutionsCollection <- futureUserSolutionsCollection
      insertResult            <- userSolutionsCollection.insert(true).one(solution)
    } yield insertResult.ok
  }

  // Update user proficiencies

  protected def userProficienciesForTool(username: String, toolId: String): Future[Seq[UserProficiency]] = {
    val filter = Json.obj(
      "username"     -> username,
      "topic.toolId" -> toolId
    )

    for {
      userProficienciesCollection <- futureUserProficienciesCollection
      userProfs <- userProficienciesCollection
        .find(filter, Option.empty[JsObject])
        .cursor[UserProficiency]()
        .collect[Seq](-1, Cursor.FailOnError())
    } yield userProfs
  }

  protected def allTopicsWithLevelForTool(username: String, tool: CollectionTool): Future[Seq[TopicWithLevel]] =
    for {
      dbUserProficiencies <- userProficienciesForTool(username, tool.id)
      userProficiencies = dbUserProficiencies
        .map(up => (up.topic, TopicWithLevel(up.topic, up.getLevel)))
        .toMap
      allUserProficiencies = tool.allTopics
        .map { t => userProficiencies.getOrElse(t, TopicWithLevel(t, Level.Beginner)) }
    } yield allUserProficiencies

  protected def updateUserProficiencies(
    username: String,
    exercise: UntypedExercise,
    topicWithLevel: TopicWithLevel
  ): Future[Boolean] = {

    val filter = Json.obj(
      "username"           -> username,
      "topic.abbreviation" -> topicWithLevel.topic.abbreviation,
      "topic.toolId"       -> topicWithLevel.topic.toolId
    )

    val levelForExerciseToAdd = LevelForExercise(
      exercise.exerciseId,
      exercise.collectionId,
      topicWithLevel.level
    )

    for {
      userProficienciesCollection <- futureUserProficienciesCollection

      oldUserProficiency: UserProficiency <- userProficienciesCollection
        .find(filter, Option.empty[JsObject])
        .one[UserProficiency]
        .map(_.getOrElse(UserProficiency(username, topicWithLevel.topic)))

      newUserProficiency: UserProficiency = oldUserProficiency.copy(
        pointsForExercises = oldUserProficiency.pointsForExercises + levelForExerciseToAdd
      )

      updateResult <- userProficienciesCollection
        .update(true)
        .one(filter, newUserProficiency, upsert = true)

    } yield updateResult.ok
  }

}
