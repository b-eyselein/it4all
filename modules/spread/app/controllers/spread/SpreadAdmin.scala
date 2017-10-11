package controllers.spread

import javax.inject.Inject

import controllers.core.AExerciseAdminController
import model.user.User
import model.{SpreadExercise, SpreadExerciseReader}
import play.data.FormFactory
import play.twirl.api.Html

class SpreadAdmin @Inject()(f: FormFactory)
  extends AExerciseAdminController[SpreadExercise](f, SpreadToolObject, SpreadExercise.finder, SpreadExerciseReader) {

  override def renderExEditForm(user: User, exercise: SpreadExercise, isCreation: Boolean): Html = ??? // FIXME: implement...

}
