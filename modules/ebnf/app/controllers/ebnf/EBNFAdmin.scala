package controllers.ebnf

import javax.inject.Inject

import controllers.core.AExerciseAdminController
import model.ebnf.{EBNFExercise, EBNFExerciseReader}
import model.user.User
import play.api.Configuration
import play.data.FormFactory
import play.twirl.api.Html

class EBNFAdmin @Inject()(c: Configuration, f: FormFactory)
  extends AExerciseAdminController[EBNFExercise](c, f, new EBNFToolObject(c), EBNFExercise.finder, EBNFExerciseReader) {

  override def renderExEditForm(user: User, exercise: EBNFExercise, isCreation: Boolean): Html =
    views.html.ebnfAdmin.newExForm.render(getUser, exercise)

}