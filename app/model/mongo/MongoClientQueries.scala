package model.mongo

import model.tools.Helper.UntypedExercise
import model.tools.Tool
import model.{JsonProtocols, _}
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoComponents
import reactivemongo.api.Cursor
import reactivemongo.play.json.compat._
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}

trait MongoClientQueries
    extends ReactiveMongoComponents
    with MongoUserQueries
    with MongoCollectionQueries
    with MongoExerciseQueries
    with MongoResultQueries
    with MongoLessonQueries
    with MongoLessonContentQueries {

  protected implicit val ec: ExecutionContext

  private implicit val topicFormat: OFormat[Topic] = JsonProtocols.topicFormat

  // Solution queries

  private def futureUserSolutionsCollection: Future[JSONCollection] =
    reactiveMongoApi.database.map(_.collection("userSolutions"))

  protected def nextUserSolutionId[P](exercise: UntypedExercise, part: P)(implicit pf: Format[P]): Future[Int] = {
    val exFilter = Json.obj(
      "toolId"       -> exercise.toolId,
      "collectionId" -> exercise.collectionId,
      "exerciseId"   -> exercise.exerciseId,
      "part"         -> part
    )

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

  private def futureUserProficienciesCollection: Future[JSONCollection] =
    reactiveMongoApi.database.map(_.collection("userProficiencies"))

  private implicit val userProficiencyFormat: OFormat[UserProficiency] = JsonProtocols.userProficiencyFormat

  protected def userProficienciesForTool(username: String, toolId: String): Future[Seq[UserProficiency]] =
    for {
      userProficienciesCollection <- futureUserProficienciesCollection
      userProfs <-
        userProficienciesCollection
          .find(Json.obj("username" -> username, "topic.toolId" -> toolId), Option.empty[JsObject])
          .cursor[UserProficiency]()
          .collect[Seq](-1, Cursor.FailOnError())
    } yield userProfs

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

  protected def updateUserProficiency(
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
