package controllers.exes.idExes

import javax.inject._

import controllers.Secured
import model.User
import model.blanks.BlanksExercise
import model.core._
import net.jcazevedo.moultingyaml.YamlFormat
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{AnyContent, ControllerComponents, EssentialAction, Request}
import play.twirl.api.Html
import views.html.blanks._

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.Try

@Singleton
class BlanksController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository)(implicit ec: ExecutionContext)
  extends AIdExController[BlanksExercise, EvaluationResult, GenericCompleteResult[EvaluationResult]](cc, dbcp, r, BlanksToolObject) with Secured {

  // Reading solution from requests

  override type SolType = String

  override def readSolutionFromPostRequest(implicit request: Request[AnyContent]): Option[String] =
    Solution.stringSolForm.bindFromRequest() fold(_ => None, sol => Some(sol.learnerSolution))

  override def readSolutionFromPutRequest(implicit request: Request[AnyContent]): Option[String] =
    Solution.stringSolForm.bindFromRequest() fold(_ => None, sol => Some(sol.learnerSolution))

  // Yaml

  override type CompEx = BlanksExercise

  override val yamlFormat: YamlFormat[BlanksExercise] = null

  // db

  override type TQ = repo.BlanksExercisesTable

  override def tq = repo.blanksExercises

  override protected def futureCompleteExById(id: Int): Future[Option[BlanksExercise]] = ???

  override protected def futureCompleteExes: Future[Seq[BlanksExercise]] = ???

  override protected def saveRead(read: Seq[BlanksExercise]): Future[Seq[Any]] = ???


  // Other routes

  def testBlanks: EssentialAction = withUser { user => implicit request => Ok(blanks(user, null)) }

  def correctBlanks(id: Int): EssentialAction = withUser { user =>
    implicit request =>
      //    val form = factory.form().bindFromRequest()
      //    val inputCount = Integer.parseInt(form.get("count"))
      //    val inputs = (for (count <- 0 until inputCount) yield form.get(s"inp$count")).toList
      //    ok(Json.toJson(exercise.correct(inputs)))
      Ok("TODO")
  }

  override protected def correctEx(user: User, sol: String, exercise: BlanksExercise): Try[GenericCompleteResult[EvaluationResult]] = ???

  //  override protected def renderExesListRest: Html = new Html("")

  override def renderExercise(user: User, exercise: BlanksExercise): Html = ???

  override def renderResult(correctionResult: GenericCompleteResult[EvaluationResult]): Html = ???

}

