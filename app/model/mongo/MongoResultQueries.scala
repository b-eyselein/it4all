package model.mongo

import model.result.BasicExerciseResult
import model.tools.Tool
import play.api.libs.json.{Format, JsObject, Json, OFormat}
import play.modules.reactivemongo.ReactiveMongoComponents
import reactivemongo.play.json.collection.JSONCollection
import reactivemongo.play.json.compat._

import scala.concurrent.{ExecutionContext, Future}

trait MongoResultQueries {
  self: ReactiveMongoComponents =>

  protected implicit val ec: ExecutionContext

  private def futureExerciseResultsCollection: Future[JSONCollection] =
    reactiveMongoApi.database.map(_.collection("exerciseResults"))

  protected def futureExerciseResultById(
    tool: Tool,
    collectionId: Int,
    exerciseId: Int
  )(part: tool.PartType): Future[Option[BasicExerciseResult[tool.PartType]]] = {

    implicit val pf: Format[tool.PartType]                         = tool.jsonFormats.partTypeFormat
    implicit val berf: OFormat[BasicExerciseResult[tool.PartType]] = tool.jsonFormats.basicExerciseResultFormat

    for {
      exerciseResultsCollection <- futureExerciseResultsCollection
      maybeExerciseResult <-
        exerciseResultsCollection
          .find(
            Json.obj("toolId" -> tool.id, "collectionId" -> collectionId, "exerciseId" -> exerciseId, "part" -> part),
            Option.empty[JsObject]
          )
          .one[BasicExerciseResult[tool.PartType]]
    } yield maybeExerciseResult
  }

  protected def futureUpsertExerciseResult(
    tool: Tool
  )(exerciseResult: BasicExerciseResult[tool.PartType]): Future[Boolean] = {
    implicit val pf: Format[tool.PartType]                         = tool.jsonFormats.partTypeFormat
    implicit val berf: OFormat[BasicExerciseResult[tool.PartType]] = tool.jsonFormats.basicExerciseResultFormat

    val key = Json.obj(
      "toolId"       -> exerciseResult.toolId,
      "collectionId" -> exerciseResult.collectionId,
      "exerciseId"   -> exerciseResult.exerciseId,
      "part"         -> exerciseResult.part
    )

    for {
      exerciseResultsCollection <- futureExerciseResultsCollection
      insertResult              <- exerciseResultsCollection.update(true).one(key, exerciseResult, upsert = true)
    } yield insertResult.ok
  }

}
