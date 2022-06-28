package model

import model.mongo.MongoRepo
import play.api.libs.json.{Json, OFormat}
import reactivemongo.api.bson.BSONDocument
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.play.json.compat.json2bson._

import scala.concurrent.Future

final case class RegisterValues(username: String, firstPassword: String, secondPassword: String) {

  def isInvalid: Boolean = firstPassword != secondPassword

}

final case class UserCredentials(username: String, password: String)

final case class User(username: String, pwHash: Option[String] = None)

final case class LoginResult(
  username: String,
  jwt: String
)

trait MongoUserRepo extends MongoRepo {

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
