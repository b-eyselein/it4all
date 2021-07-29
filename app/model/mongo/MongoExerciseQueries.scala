package model.mongo

import model.tools.Helper.UntypedExercise
import model.tools.{Tool, ToolList}
import model.{Exercise, ExerciseContent}
import play.api.libs.json.{Format, OFormat}
import play.modules.reactivemongo.ReactiveMongoComponents
import reactivemongo.api.Cursor
import reactivemongo.api.bson.BSONDocument
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.play.json.compat.json2bson._

import scala.concurrent.{ExecutionContext, Future}

trait MongoExerciseQueries {
  self: ReactiveMongoComponents =>

  protected implicit val ec: ExecutionContext

  private def futureExercisesCollection: Future[BSONCollection] =
    reactiveMongoApi.database.map(_.collection("exercises"))

  protected def futureExerciseCountForTool(toolId: String): Future[Long] =
    for {
      exercisesCollection <- futureExercisesCollection
      exerciseCount       <- exercisesCollection.count(Some(BSONDocument("toolId" -> toolId)))
    } yield exerciseCount

  protected def futureExercisesForTool(tool: Tool): Future[Seq[Exercise[tool.ExContentType]]] = {
    implicit val ef: Format[Exercise[tool.ExContentType]] = tool.jsonFormats.exerciseFormat

    for {
      exercisesCollection <- futureExercisesCollection
      exercises <-
        exercisesCollection
          .find(BSONDocument("toolId" -> tool.id), Option.empty[BSONDocument])
          .cursor[Exercise[tool.ExContentType]]()
          .collect[Seq](-1, Cursor.FailOnError())
    } yield exercises
  }

  protected def futureExerciseCountForCollection(toolId: String, collectionId: Int): Future[Long] =
    for {
      exercisesCollection <- futureExercisesCollection
      exerciseCount <-
        exercisesCollection
          .count(Some(BSONDocument("toolId" -> toolId, "collectionId" -> collectionId)))
    } yield exerciseCount

  protected def futureExercisesForCollection(toolId: String, collectionId: Int): Future[Seq[UntypedExercise]] =
    ToolList.tools.find(_.id == toolId) match {
      case None => Future.failed(new Exception("No such tool..."))
      case Some(tool) =>
        implicit val ef: Format[Exercise[tool.ExContentType]] = tool.jsonFormats.exerciseFormat

        for {
          exercisesCollection <- futureExercisesCollection
          exercises <-
            exercisesCollection
              .find(BSONDocument("toolId" -> tool.id, "collectionId" -> collectionId), Option.empty[BSONDocument])
              .sort(BSONDocument("exerciseId" -> 1))
              .cursor[Exercise[tool.ExContentType]]()
              .collect[Seq](-1, Cursor.FailOnError())
        } yield exercises
    }

  protected def futureExerciseExists(toolId: String, collectionId: Int, exerciseId: Int): Future[Boolean] =
    for {
      exercisesCollection <- futureExercisesCollection
      maybeExercise <-
        exercisesCollection
          .find(
            BSONDocument("toolId" -> toolId, "collectionId" -> collectionId, "exerciseId" -> exerciseId),
            Option.empty[BSONDocument]
          )
          .one[BSONDocument]
    } yield maybeExercise.isDefined

  protected def futureExerciseById(
    tool: Tool,
    collectionId: Int,
    exerciseId: Int
  ): Future[Option[Exercise[tool.ExContentType]]] = {
    implicit val ef: OFormat[Exercise[tool.ExContentType]] = tool.jsonFormats.exerciseFormat

    for {
      exercisesCollection <- futureExercisesCollection
      maybeExercise <-
        exercisesCollection
          .find(
            BSONDocument("toolId" -> tool.id, "collectionId" -> collectionId, "exerciseId" -> exerciseId),
            Option.empty[BSONDocument]
          )
          .one[Exercise[tool.ExContentType]]
    } yield maybeExercise
  }

  protected def futureInsertExercise[EC <: ExerciseContent](
    exercise: Exercise[EC],
    exerciseFormat: OFormat[Exercise[EC]]
  ): Future[Boolean] = {
    implicit val ef: OFormat[Exercise[EC]] = exerciseFormat

    for {
      exercisesCollection <- futureExercisesCollection
      insertResult        <- exercisesCollection.insert(true).one(exercise)
    } yield insertResult.writeErrors.isEmpty
  }

}
