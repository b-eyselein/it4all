package controllers.xml

import controllers.core.AExerciseAdminController
import javax.inject.Inject
import model.{ XmlExercise, XmlExerciseReader }
import model.user.User
import play.data.FormFactory

class XmlAdmin @Inject() (f: FormFactory)
  extends AExerciseAdminController[XmlExercise](f, XmlToolObject, XmlExercise.finder, XmlExerciseReader) {

  override def renderAdminIndex(user: User) = views.html.xmlAdmin.index.render(user)

  override def renderExEditForm(user: User, exercise: XmlExercise, isCreation: Boolean) =
    views.html.xmlAdmin.editExForm.render(user, exercise, isCreation)

}
