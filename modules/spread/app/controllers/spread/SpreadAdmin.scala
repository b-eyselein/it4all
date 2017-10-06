package controllers.spread

import controllers.core.AExerciseAdminController
import javax.inject.Inject
import model.{ SpreadExercise, SpreadExerciseReader }
import model.user.User
import play.data.FormFactory

class SpreadAdmin @Inject() (f: FormFactory)
  extends AExerciseAdminController[SpreadExercise](f, SpreadToolObject, SpreadExercise.finder, SpreadExerciseReader) {

  override def renderAdminIndex(user: User) = views.html.spreadAdmin.index.render(user)

  override def renderExEditForm(user: User, exercise: SpreadExercise, isCreation: Boolean) = // FIXME: implement...
    ???
}
