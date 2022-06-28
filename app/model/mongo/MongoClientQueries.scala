package model.mongo

import model._
import model.tools.Helper.UntypedExercise
import model.tools.Tool
import play.api.libs.json.OFormat
import play.modules.reactivemongo.{ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.bson.BSONDocument
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.play.json.compat.json2bson._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

trait MongoRepo extends ReactiveMongoComponents {

  protected implicit val ec: ExecutionContext

  protected def futureCollection(name: String): Future[BSONCollection] = for {
    db <- reactiveMongoApi.database
  } yield db.collection(name)

}

class MongoClientQueries @Inject() (override val reactiveMongoApi: ReactiveMongoApi)(override protected implicit val ec: ExecutionContext)
    extends ReactiveMongoComponents
    with MongoUserRepo
    with MongoCollectionRepo
    with MongoExerciseQueries
    with MongoUserSolutionRepo
    with MongoExercisePartResultQueries {

  // Update user proficiencies

  private def futureUserProficienciesCollection: Future[BSONCollection] = reactiveMongoApi.database.map(_.collection("userProficiencies"))

  private implicit val userProficiencyFormat: OFormat[UserProficiency] = JsonProtocols.userProficiencyFormat

  def userProficienciesForTool(username: String, toolId: String): Future[Seq[UserProficiency]] = for {
    userProficienciesCollection <- futureUserProficienciesCollection
    userProfs <- userProficienciesCollection.find(BSONDocument("username" -> username, "topic.toolId" -> toolId)).cursor[UserProficiency]().collect[Seq]()
  } yield userProfs

  protected def allTopicsWithLevelForTool(username: String, tool: Tool): Future[Seq[TopicWithLevel]] = for {
    dbUserProficiencies <- userProficienciesForTool(username, tool.id)
    userProficiencies = dbUserProficiencies
      .map(up => (up.topic, TopicWithLevel(up.topic, up.getLevel)))
      .toMap
    allUserProficiencies = tool.allTopics
      .map { t => userProficiencies.getOrElse(t, TopicWithLevel(t, Level.Beginner)) }
  } yield allUserProficiencies

  def updateUserProficiency(
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

      oldUserProficiency: UserProficiency <- userProficienciesCollection
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
