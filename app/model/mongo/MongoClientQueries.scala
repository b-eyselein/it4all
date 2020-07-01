package model.mongo

import model.{JsonProtocols, _}
import model.tools.Tool
import model.tools.Helper.UntypedExercise
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoComponents
import reactivemongo.api.Cursor
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}

trait MongoClientQueries
    extends ReactiveMongoComponents
    with MongoUserQueries
    with MongoCollectionQueries
    with MongoExerciseQueries
    with MongoLessonQueries
    with MongoLessonContentQueries {

  protected implicit val ec: ExecutionContext

  private implicit val topicFormat: OFormat[Topic] = JsonProtocols.topicFormat

  private implicit val userProficiencyFormat: OFormat[UserProficiency] = JsonProtocols.userProficiencyFormat

  private def exercisePartFilter[P](
    toolId: String,
    collectionId: Int,
    exerciseId: Int,
    part: P
  )(implicit pf: Format[P]): JsObject =
    Json.obj(
      "toolId"       -> toolId,
      "collectionId" -> collectionId,
      "exerciseId"   -> exerciseId,
      "part"         -> part
    )

  // Collections

  private def futureUserProficienciesCollection: Future[JSONCollection] =
    reactiveMongoApi.database.map(_.collection("userProficiencies"))

  // Solution queries

  private def futureUserSolutionsCollection: Future[JSONCollection] =
    reactiveMongoApi.database.map(_.collection("userSolutions"))

  protected def nextUserSolutionId[P](exercise: UntypedExercise, part: P)(implicit pf: Format[P]): Future[Int] = {
    val exFilter = exercisePartFilter(exercise.toolId, exercise.collectionId, exercise.exerciseId, part)

    final case class ThisResult(solutionId: Int)

    implicit val resultFormat: Reads[ThisResult] = (__ \ "solutionId").read[Int].map(ThisResult.apply _)

    for {
      userSolutionsCollection <- futureUserSolutionsCollection
      x <-
        userSolutionsCollection
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
      userProfs <-
        userProficienciesCollection
          .find(filter, Option.empty[JsObject])
          .cursor[UserProficiency]()
          .collect[Seq](-1, Cursor.FailOnError())
    } yield userProfs
  }

  protected def allTopicsWithLevelForTool(username: String, tool: Tool): Future[Seq[TopicWithLevel]] =
    for {
      dbUserProficiencies <- userProficienciesForTool(username, tool.id)
      userProficiencies =
        dbUserProficiencies
          .map(up => (up.topic, TopicWithLevel(up.topic, up.getLevel)))
          .toMap
      allUserProficiencies =
        tool.allTopics
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

      oldUserProficiency: UserProficiency <-
        userProficienciesCollection
          .find(filter, Option.empty[JsObject])
          .one[UserProficiency]
          .map(_.getOrElse(UserProficiency(username, topicWithLevel.topic)))

      newUserProficiency: UserProficiency = oldUserProficiency.copy(
        pointsForExercises = oldUserProficiency.pointsForExercises + levelForExerciseToAdd
      )

      updateResult <-
        userProficienciesCollection
          .update(true)
          .one(filter, newUserProficiency, upsert = true)

    } yield updateResult.ok
  }

}
