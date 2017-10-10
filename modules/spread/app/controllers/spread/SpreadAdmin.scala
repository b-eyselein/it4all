package controllers.spread

import javax.inject.Inject

import controllers.core.AExerciseAdminController
import model.user.User
import model.{SpreadExercise, SpreadExerciseReader}
import play.api.Configuration
import play.data.FormFactory
import play.twirl.api.Html

class SpreadAdmin @Inject()(c: Configuration, f: FormFactory)
  extends AExerciseAdminController[SpreadExercise](c, f, new SpreadToolObject(c), SpreadExercise.finder, SpreadExerciseReader) {

  override def renderAdminIndex(user: User): Html = views.html.spreadAdmin.index.render(user)

  override def renderExEditForm(user: User, exercise: SpreadExercise, isCreation: Boolean): Html = // FIXME: implement...
    ???
}
