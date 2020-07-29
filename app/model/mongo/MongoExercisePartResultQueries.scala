package model.mongo

import model.result.BasicExercisePartResult
import model.tools.Tool
import play.api.libs.json.{Format, JsObject, Json, OFormat}
import play.modules.reactivemongo.ReactiveMongoComponents
import reactivemongo.play.json.collection.JSONCollection
import reactivemongo.play.json.compat._

import scala.concurrent.{ExecutionContext, Future}

trait MongoExercisePartResultQueries {
  self: ReactiveMongoComponents =>

  protected implicit val ec: ExecutionContext

  private def futureExerciseResultsCollection: Future[JSONCollection] =
    reactiveMongoApi.database.map(_.collection("exerciseResults"))

  private def basicExercisePartResultKey[P](
    toolId: String,
    collectionId: Int,
    exerciseId: Int,
    part: P
  )(implicit pf: Format[P]) =
    Json.obj(
      "toolId"       -> toolId,
      "collectionId" -> collectionId,
      "exerciseId"   -> exerciseId,
      "part"         -> part
    )

  protected def futureExerciseResultById(
    tool: Tool,
    collectionId: Int,
    exerciseId: Int
  )(part: tool.PartType): Future[Option[BasicExercisePartResult[tool.PartType]]] = {

    implicit val pf: Format[tool.PartType]                             = tool.jsonFormats.partTypeFormat
    implicit val berf: OFormat[BasicExercisePartResult[tool.PartType]] = tool.jsonFormats.basicExerciseResultFormat

    for {
      exerciseResultsCollection <- futureExerciseResultsCollection
      maybeExerciseResult <-
        exerciseResultsCollection
          .find(basicExercisePartResultKey(tool.id, collectionId, exerciseId, part), Option.empty[JsObject])
          .one[BasicExercisePartResult[tool.PartType]]
    } yield maybeExerciseResult
  }

  protected def futureUpsertExerciseResult(
    tool: Tool
  )(exerciseResult: BasicExercisePartResult[tool.PartType]): Future[Boolean] = {
    implicit val berf: OFormat[BasicExercisePartResult[tool.PartType]] = tool.jsonFormats.basicExerciseResultFormat

    val key = basicExercisePartResultKey(
      exerciseResult.toolId,
      exerciseResult.collectionId,
      exerciseResult.exerciseId,
      exerciseResult.part
    )(tool.jsonFormats.partTypeFormat)

    for {
      exerciseResultsCollection <- futureExerciseResultsCollection
      insertResult              <- exerciseResultsCollection.update(true).one(key, exerciseResult, upsert = true)
    } yield insertResult.ok
  }

}
