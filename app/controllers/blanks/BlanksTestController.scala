package controllers.blanks

import javax.inject._

import controllers.core.BaseController
import model.core.{Repository, Secured}
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{ControllerComponents, EssentialAction}

import scala.concurrent.ExecutionContext

class BlanksTestController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository)
                                    (implicit ec: ExecutionContext)
  extends BaseController(cc, dbcp, r) with Secured {

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

  def testBlanks: EssentialAction = withUser { user => implicit request => Ok(views.html.blanks.blanks.render(user, exercise)) }
}
