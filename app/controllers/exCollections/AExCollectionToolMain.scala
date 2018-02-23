package controllers.exCollections

import controllers.exes.AToolMain
import model._
import model.core.{CompleteResult, EvaluationResult}
import model.yaml.MyYamlFormat
import play.api.mvc.{AnyContent, Request, Result}
import play.twirl.api.Html

import scala.concurrent.Future
import scala.util.Try

trait AExCollectionToolMain[Ex <: Exercise, CompEx <: CompleteEx[Ex], Coll <: ExerciseCollection[Ex, CompEx], CompColl <: CompleteCollection[Ex, CompEx, Coll]]
  extends AToolMain[Ex, CompEx] {

  type R <: EvaluationResult

  type CompResult <: CompleteResult[R]

  // FIXME: fill...
  val collectionSingularName = ""
  val collectionPluralName   = ""

  // Reading solution from Request

  type SolType

  def readSolutionFromPostRequest(implicit request: Request[AnyContent]): Option[SolType]

  def readSolutionFromPutRequest(implicit request: Request[AnyContent]): Option[SolType]

  // Reading Yaml

  implicit val yamlFormat: MyYamlFormat[CompColl]

  // Database queries

  override val tables: ExerciseCollectionTableDefs[Ex, CompEx, Coll, CompColl]

  def numOfExes: Future[Int] = tables.futureNumOfExes

  def numOfExesInColl(id: Int): Future[Int]

  def futureCollById(id: Int): Future[Option[Coll]] = tables.futureCollById(id)

  def futureCompleteColls: Future[Seq[CompColl]] = tables.futureCompleteColls

  def futureCompleteCollById(id: Int): Future[Option[CompColl]] = tables.futureCompleteCollById(id)

  def futureCompleteExById(collId: Int, id: Int): Future[Option[CompEx]] = tables.futureCompleteExById(collId, id)

  def statistics: Future[Html] = numOfExes map (num => Html(s"<li>Es existieren insgesamt $num Aufgaben</li>"))

  def saveRead(read: Seq[CompColl]): Future[Seq[Boolean]]

  def wrap(compColl: CompColl): CompleteCollectionWrapper

  // Correction

  def correctEx(user: User, form: SolType, exercise: CompEx, collection: Coll): Future[Try[CompResult]]

  // Views

  def renderExercise(user: User, coll: Coll, exercise: CompEx, numOfExes: Int): Future[Html]

  def onSubmitCorrectionResult(user: User, result: CompResult): Result

  def onSubmitCorrectionError(user: User, error: Throwable): Result

  def onLiveCorrectionResult(result: CompResult): Result

  def onLiveCorrectionError(error: Throwable): Result

  def adminRenderEditRest(exercise: Option[CompColl]): Html

}
