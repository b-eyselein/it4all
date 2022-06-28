package model.mongo

import model.User
import play.api.libs.json.{Json, OFormat}
import reactivemongo.api.bson.BSONDocument
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.play.json.compat.json2bson._

import scala.concurrent.Future

trait MongoUserQueries extends MongoRepo {

  private implicit val userFormat: OFormat[User] = Json.format

  private def futureUsersCollection: Future[BSONCollection] = futureCollection("users")

  def futureUserByUsername(username: String): Future[Option[User]] = for {
    usersCollection <- futureUsersCollection
    maybeUser       <- usersCollection.find(BSONDocument("username" -> username)).one[User]
  } yield maybeUser

  def futureInsertUser(user: User): Future[Boolean] = for {
    usersCollection <- futureUsersCollection
    insertResult    <- usersCollection.insert(true).one(user)
  } yield insertResult.n == 1

}
