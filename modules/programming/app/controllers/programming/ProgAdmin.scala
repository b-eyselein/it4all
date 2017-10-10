package controllers.programming


import javax.inject.Inject

import controllers.core.AExerciseAdminController
import model.{ProgExercise, ProgExerciseReader}
import model.user.User
import play.api.Configuration
import play.data.FormFactory
import play.twirl.api.Html

class ProgAdmin @Inject()(c: Configuration, f: FormFactory)
  extends AExerciseAdminController[ProgExercise](c, f, new ProgToolObject(c), ProgExercise.finder, ProgExerciseReader.getInstance()) {

  override def renderExEditForm(user: User, exercise: ProgExercise, isCreation: Boolean): Html = views.html.progAdmin.editExForm.render(user, exercise)

}
