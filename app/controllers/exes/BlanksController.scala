package controllers.exes

import javax.inject._

import controllers.core.AIdExController
import model.User
import model.blanks.BlanksExercise
import model.core.result.{CompleteResult, EvaluationResult}
import model.core.{Repository, Secured, StringSolution}
import net.jcazevedo.moultingyaml.YamlFormat
import play.api.data.Form
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{ControllerComponents, EssentialAction}
import play.twirl.api.Html

import scala.concurrent.ExecutionContext
import scala.language.implicitConversions
import scala.util.Try

@Singleton
class BlanksController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository)(implicit ec: ExecutionContext)
  extends AIdExController[BlanksExercise, EvaluationResult](cc, dbcp, r, BlanksToolObject) with Secured {

  override type SolutionType = StringSolution

  override def solForm: Form[StringSolution] = ???

  // db

  override val yamlFormat: YamlFormat[BlanksExercise] = null

  override implicit def dbType2ExType(dbt: BlanksExercise): BlanksExercise = dbt

  override type TQ = repo.BlanksExercisesTable

  override def tq = repo.blanksExercises

  override type ExerciseType = BlanksExercise

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

  override protected def correctEx(sol: StringSolution, exercise: Option[BlanksExercise], user: User): Try[CompleteResult[EvaluationResult]] = ???

  override protected def renderExesListRest: Html = new Html("")

  def testBlanks: EssentialAction = withUser { user => implicit request => Ok(views.html.blanks.blanks.render(user, null)) }

}

