package controllers

import javax.inject.Inject

import controllers.core.BaseController
import model.blanks.BlanksExercise
import play.data.FormFactory
import play.libs.Json
import play.mvc.Results._
import play.mvc.{Result, Security}

@Security.Authenticated(classOf[model.Secured])
class BlanksTestController @Inject()(f: FormFactory) extends BaseController(f) {

  private val exercise = new BlanksExercise


  def correctBlanks(id: Int): Result = {
    val form = factory.form().bindFromRequest()
    val inputCount = Integer.parseInt(form.get("count"))

    val inputs = (for (count <- 0 until inputCount) yield form.get(s"inp$count")).toList

    ok(Json.toJson(exercise.correct(inputs)))
  }

  def testBlanks: Result = ok(views.html.blanks.render(getUser, exercise))
}
