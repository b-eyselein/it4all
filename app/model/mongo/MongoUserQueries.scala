package model.mongo

import model.User
import play.api.libs.json.{Json, OFormat}
import play.modules.reactivemongo.ReactiveMongoComponents
import reactivemongo.api.bson.BSONDocument
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.play.json.compat.json2bson._

import scala.concurrent.{ExecutionContext, Future}

trait MongoUserQueries {
  self: ReactiveMongoComponents =>

  protected implicit val ec: ExecutionContext

  private implicit val userFormat: OFormat[User] = Json.format

  private def futureUsersCollection: Future[BSONCollection] = reactiveMongoApi.database.map(_.collection("users"))

  protected def futureUserByUsername(username: String): Future[Option[User]] = for {
    usersCollection <- futureUsersCollection
    maybeUser <-
      usersCollection
        .find(BSONDocument("username" -> username), Option.empty[BSONDocument])
        .one[User]
  } yield maybeUser

  protected def futureInsertUser(user: User): Future[Boolean] = for {
    usersCollection <- futureUsersCollection
    insertResult    <- usersCollection.insert(true).one(user)
  } yield insertResult.n == 1

}
