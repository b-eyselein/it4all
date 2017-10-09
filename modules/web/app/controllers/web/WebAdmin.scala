package controllers.web

import controllers.core.{ AExerciseAdminController, BaseController }
import javax.inject.Inject
import model.{ WebExercise, WebExerciseReader }
import model.user.User
import play.data.FormFactory
import play.mvc.Results
import play.twirl.api.Html

class WebAdmin @Inject() (f: FormFactory)
  extends AExerciseAdminController[WebExercise](f, WebToolObject, WebExercise.finder, WebExerciseReader) {

  override def renderAdminIndex(user: User) = views.html.admin.exerciseAdminMain.render(user, statistics, WebToolObject, new Html(""))

  def statistics = new Html(s"""<li>Es existieren insgesamt ${WebExercise.finder.all.size} Aufgaben, davon
  <ul>
    <li>${WebExercise.withHtmlPart} Aufgaben mit HTML-Teil</li>
    <li>${WebExercise.withJsPart} Aufgaben mit JS-Teil</li>
  </ul>
</li>""")

  def exRest(exerciseId: Int) =
    Results.ok(views.html.webAdmin.webExRest.render(BaseController.getUser, finder.byId(exerciseId)))

  override def renderExEditForm(user: User, exercise: WebExercise, isCreation: Boolean) =
    views.html.webAdmin.editExForm.render(user, exercise)

}
