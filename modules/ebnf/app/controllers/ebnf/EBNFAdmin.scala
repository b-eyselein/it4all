package controllers.ebnf

import javax.inject.Inject

import controllers.core.AExerciseAdminController
import model.ebnf.{EBNFExercise, EBNFExerciseReader}
import model.user.User
import play.data.FormFactory
import play.twirl.api.Html

class EBNFAdmin @Inject()(f: FormFactory)
  extends AExerciseAdminController[EBNFExercise](f, EBNFToolObject, EBNFExercise.finder, EBNFExerciseReader) {

  override def renderExEditForm(user: User, exercise: EBNFExercise, isCreation: Boolean): Html =
    views.html.ebnfAdmin.newExForm.render(getUser, exercise)

}