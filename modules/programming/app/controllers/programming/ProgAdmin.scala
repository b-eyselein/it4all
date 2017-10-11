package controllers.programming

import javax.inject.Inject

import controllers.core.AExerciseAdminController
import model.user.User
import model.{ProgExercise, ProgExerciseReader}
import play.data.FormFactory
import play.twirl.api.Html

class ProgAdmin @Inject()(f: FormFactory)
  extends AExerciseAdminController[ProgExercise](f, ProgToolObject, ProgExercise.finder, ProgExerciseReader.getInstance()) {

  override def renderExEditForm(user: User, exercise: ProgExercise, isCreation: Boolean): Html =
    views.html.progAdmin.editExForm.render(user, exercise)

}
