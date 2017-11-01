package controllers.blanks

import javax.inject._

import controllers.core.AIdExController
import model.blanks.{BlanksExercise, BlanksExerciseReads}
import model.core.result.{CompleteResult, EvaluationResult}
import model.core.{Repository, Secured}
import play.api.data.Form
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Reads
import play.api.mvc.{ControllerComponents, EssentialAction}

import scala.concurrent.ExecutionContext
import scala.util.Try

class BlanksTestController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository)(implicit ec: ExecutionContext)
  extends AIdExController[BlanksExercise, EvaluationResult](cc, dbcp, r, BlanksToolObject) with Secured {

  override def solForm: (Form[SolType]) = ???

  implicit def reads: Reads[BlanksExercise] = BlanksExerciseReads.blanksExReads

  override type TQ = repo.BlanksExercisesTable

  override def tq = repo.blanksExercises

  private val exercise = null // new BlanksExercise


  def correctBlanks(id: Int): EssentialAction = withUser { user =>
    implicit request =>
      //    val form = factory.form().bindFromRequest()
      //    val inputCount = Integer.parseInt(form.get("count"))
      //
      //    val inputs = (for (count <- 0 until inputCount) yield form.get(s"inp$count")).toList
      //
      //    ok(Json.toJson(exercise.correct(inputs)))
      Ok("TODO")
  }

  override protected def correctEx(sol: SolType, exercise: Option[BlanksExercise], user: model.User): Try[CompleteResult[EvaluationResult]] = ???

  def testBlanks: EssentialAction = withUser { user => implicit request => Ok(views.html.blanks.blanks.render(user, exercise)) }
}

