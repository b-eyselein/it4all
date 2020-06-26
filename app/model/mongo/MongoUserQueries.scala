package model.mongo

import model.User
import play.api.libs.json.{JsObject, Json, OFormat}
import play.modules.reactivemongo.ReactiveMongoComponents
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}

trait MongoUserQueries {
  self: ReactiveMongoComponents =>

  protected implicit val ec: ExecutionContext

  private implicit val userFormat: OFormat[User] = Json.format

  private def futureUsersCollection: Future[JSONCollection] = reactiveMongoApi.database.map(_.collection("users"))

  protected def futureUserByUsername(username: String): Future[Option[User]] =
    for {
      usersCollection <- futureUsersCollection
      maybeUser <-
        usersCollection
          .find(Json.obj("username" -> username), Option.empty[JsObject])
          .one[User]
    } yield maybeUser

  protected def futureInsertUser(user: User): Future[Boolean] =
    for {
      usersCollection <- futureUsersCollection
      insertResult    <- usersCollection.insert(true).one(user)
    } yield insertResult.n == 1

}
