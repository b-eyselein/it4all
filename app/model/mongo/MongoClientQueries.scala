package model.mongo

import model._
import model.tools.Helper.UntypedExercise
import model.tools.Tool
import play.api.libs.json.{Format, OFormat}
import play.modules.reactivemongo.ReactiveMongoComponents
import reactivemongo.api.Cursor
import reactivemongo.api.bson.BSONDocument
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.play.json.compat.json2bson._

import scala.concurrent.{ExecutionContext, Future}

trait MongoClientQueries
    extends ReactiveMongoComponents
    with MongoUserQueries
    with MongoCollectionQueries
    with MongoExerciseQueries
    with MongoExercisePartResultQueries
    with MongoLessonQueries
    with MongoLessonContentQueries {

  protected implicit val ec: ExecutionContext

  private implicit val topicFormat: OFormat[Topic] = JsonProtocols.topicFormat

  // Solution queries

  private def futureUserSolutionsCollection: Future[BSONCollection] =
    reactiveMongoApi.database.map(_.collection("userSolutions"))

  protected def nextUserSolutionId[P](exercise: UntypedExercise, part: P)(implicit pf: Format[P]): Future[Int] = {
    val exFilter = BSONDocument(
      "toolId"       -> exercise.toolId,
      "collectionId" -> exercise.collectionId,
      "exerciseId"   -> exercise.exerciseId,
      "part"         -> part
    )

    for {
      userSolutionsCollection <- futureUserSolutionsCollection
      maybeMaxKeyDocument <-
        userSolutionsCollection
          .find(exFilter, Some(BSONDocument("solutionId" -> 1, "_id" -> 0)))
          .sort(BSONDocument("solutionId" -> -1))
          .one[BSONDocument]
    } yield maybeMaxKeyDocument match {
      case None        => 1
      case Some(jsObj) => jsObj.getAsOpt[Int]("solutionId").map(_ + 1).getOrElse(1)
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
    } yield insertResult.writeErrors.isEmpty
  }

  // Update user proficiencies

  private def futureUserProficienciesCollection: Future[BSONCollection] =
    reactiveMongoApi.database.map(_.collection("userProficiencies"))

  private implicit val userProficiencyFormat: OFormat[UserProficiency] = JsonProtocols.userProficiencyFormat

  protected def userProficienciesForTool(username: String, toolId: String): Future[Seq[UserProficiency]] =
    for {
      userProficienciesCollection <- futureUserProficienciesCollection
      userProfs <-
        userProficienciesCollection
          .find(BSONDocument("username" -> username, "topic.toolId" -> toolId), Option.empty[BSONDocument])
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

    val filter = BSONDocument(
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
          .find(filter, Option.empty[BSONDocument])
          .one[UserProficiency]
          .map(_.getOrElse(UserProficiency(username, topicWithLevel.topic)))

      newUserProficiency: UserProficiency = oldUserProficiency.copy(
        pointsForExercises = oldUserProficiency.pointsForExercises + levelForExerciseToAdd
      )

      updateResult <- userProficienciesCollection.update(true).one(filter, newUserProficiency, upsert = true)

    } yield updateResult.writeErrors.isEmpty
  }

}
