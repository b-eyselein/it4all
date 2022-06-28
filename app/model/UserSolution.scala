package model

import model.mongo.MongoRepo
import model.tools.Helper.UntypedExercise
import play.api.libs.json.{Format, OFormat}
import reactivemongo.api.bson.BSONDocument
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.play.json.compat.json2bson._

import scala.concurrent.Future

final case class UserSolution[SolType, PartType <: ExPart](
  solutionId: Int,
  exerciseId: Int,
  collectionId: Int,
  toolId: String,
  username: String,
  solution: SolType,
  part: PartType
)

trait MongoUserSolutionRepo extends MongoRepo {

  private def futureUserSolutionsCollection: Future[BSONCollection] = futureCollection("userSolutions")

  def nextUserSolutionId[P](exercise: UntypedExercise, part: P)(implicit pf: Format[P]): Future[Int] = {
    val exFilter = BSONDocument(
      "toolId"       -> exercise.toolId,
      "collectionId" -> exercise.collectionId,
      "exerciseId"   -> exercise.exerciseId,
      "part"         -> part
    )

    val solutionIdExtractor = BSONDocument("solutionId" -> 1, "_id" -> 0)

    for {
      userSolutionsCollection <- futureUserSolutionsCollection
      maybeMaxKeyDocument <- userSolutionsCollection
        .find(exFilter, Some(solutionIdExtractor))
        .sort(BSONDocument("solutionId" -> -1))
        .one[BSONDocument]
    } yield maybeMaxKeyDocument match {
      case None        => 1
      case Some(jsObj) => jsObj.getAsOpt[Int]("solutionId").map(_ + 1).getOrElse(1)
    }
  }

  def insertSolution[S, P <: ExPart](
    solution: UserSolution[S, P],
    solutionFormat: OFormat[UserSolution[S, P]]
  ): Future[Boolean] = {
    implicit val sf: OFormat[UserSolution[S, P]] = solutionFormat

    for {
      userSolutionsCollection <- futureUserSolutionsCollection
      insertResult            <- userSolutionsCollection.insert(true).one(solution)
    } yield insertResult.writeErrors.isEmpty
  }

}
