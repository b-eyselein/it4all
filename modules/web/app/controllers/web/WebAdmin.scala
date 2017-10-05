package controllers.web

import javax.inject.Inject

import controllers.core.AExerciseAdminController
import model.WebExercise
import model.WebExerciseReader
import model.user.User
import play.data.FormFactory
import play.mvc.Result
import play.twirl.api.Html
import controllers.core.BaseController
import play.mvc.Results
import model.WebExerciseReader

class WebAdmin @Inject() (f: FormFactory)
  extends AExerciseAdminController[WebExercise](f, WebToolObject, WebExercise.finder, WebExerciseReader) {

  override def renderAdminIndex(user: User) = views.html.webAdmin.index.render(user)

  def exRest(exerciseId: Int) =
    Results.ok(views.html.webAdmin.webExRest.render(BaseController.getUser, finder.byId(exerciseId)))

  override def renderExEditForm(user: User, exercise: WebExercise, isCreation: Boolean) =
    views.html.webAdmin.editExForm.render(user, exercise)

}
