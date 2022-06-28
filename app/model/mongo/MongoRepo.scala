package model.mongo

import play.modules.reactivemongo.ReactiveMongoComponents
import reactivemongo.api.bson.collection.BSONCollection

import scala.concurrent.{ExecutionContext, Future}

trait MongoRepo extends ReactiveMongoComponents {

  protected implicit val ec: ExecutionContext

  protected def futureCollection(name: String): Future[BSONCollection] = for {
    db <- reactiveMongoApi.database
  } yield db.collection(name)

}
