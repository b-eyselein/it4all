package controllers

import javax.inject.Inject

import controllers.core.BaseController
import model.blanks.BlanksExercise
import play.api.mvc.ControllerComponents
import play.mvc.Security

@Security.Authenticated(classOf[model.Secured])
class BlanksTestController @Inject()(cc: ControllerComponents) extends BaseController(cc) {

  private val exercise = new BlanksExercise


  def correctBlanks(id: Int) = Action { implicit request =>
    //    val form = factory.form().bindFromRequest()
    //    val inputCount = Integer.parseInt(form.get("count"))
    //
    //    val inputs = (for (count <- 0 until inputCount) yield form.get(s"inp$count")).toList
    //
    //    ok(Json.toJson(exercise.correct(inputs)))
    Ok("TODO")
  }

  def testBlanks = Action { implicit request => Ok(views.html.blanks.render(getUser, exercise)) }
}
