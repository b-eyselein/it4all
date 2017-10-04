package controllers.ebnf;

import controllers.core.AExerciseAdminController
import controllers.core.BaseController
import javax.persistence.Entity
import model.ebnf.EBNFExercise
import model.ebnf.EBNFExerciseReader
import model.user.User
import play.data.FormFactory
import play.mvc.Results
import javax.inject.Inject

class EBNFAdmin @Inject() (factory: FormFactory)
  extends AExerciseAdminController[EBNFExercise](factory, EBNFToolObject, EBNFExercise.finder, EBNFExerciseReader) {

  override def renderAdminIndex(user: User) = views.html.ebnfAdmin.index.render(user)

  override def renderExEditForm(user: User, exercise: EBNFExercise, isCreation: Boolean) =
    views.html.ebnfAdmin.newExForm.render(BaseController.getUser, exercise)

}