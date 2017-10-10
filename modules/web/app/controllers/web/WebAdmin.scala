package controllers.web

import javax.inject.Inject

import controllers.core.AExerciseAdminController
import model.user.User
import model.{WebExercise, WebExerciseReader}
import play.api.Configuration
import play.data.FormFactory
import play.mvc.{Result, Results}
import play.twirl.api.Html

class WebAdmin @Inject()(c: Configuration, f: FormFactory)
  extends AExerciseAdminController[WebExercise](c, f, new WebToolObject(c), WebExercise.finder, WebExerciseReader) {

  override def statistics = new Html(
    s"""<li>Es existieren insgesamt ${WebExercise.finder.all.size} Aufgaben, davon
  <ul>
    <li>${WebExercise.withHtmlPart} Aufgaben mit HTML-Teil</li>
    <li>${WebExercise.withJsPart} Aufgaben mit JS-Teil</li>
  </ul>
</li>""")

  def exRest(exerciseId: Int): Result =
    Results.ok(views.html.webAdmin.webExRest.render(getUser, finder.byId(exerciseId)))

  override def renderExEditForm(user: User, exercise: WebExercise, isCreation: Boolean): Html =
    views.html.webAdmin.editExForm.render(user, exercise)

}
