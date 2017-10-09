package controllers.xml

import scala.collection.JavaConverters.asScalaBufferConverter

import controllers.core.AExerciseAdminController
import javax.inject.Inject
import model.{ XmlExercise, XmlExerciseReader }
import model.user.User
import play.data.FormFactory
import play.twirl.api.Html

class XmlAdmin @Inject() (f: FormFactory)
  extends AExerciseAdminController[XmlExercise](f, XmlToolObject, XmlExercise.finder, XmlExerciseReader) {

  override def renderAdminIndex(user: User) = views.html.admin.exerciseAdminMain.render(user, statistics, XmlToolObject, new Html(""))

  def statistics = new Html(s"""
<li>Es existieren insgesamt ${XmlExercise.finder.all.size} <a href="${controllers.xml.routes.XmlAdmin.exercises}">Aufgaben</a>, davon
  <ul>
  ${XmlExercise.finder.all.asScala.groupBy(_.exerciseType).map(e => s"""<li>${e._2.size} Aufgaben von Typ ${e._1}""").mkString("\n")}
  </ul>
</li>""")

  override def renderExEditForm(user: User, exercise: XmlExercise, isCreation: Boolean) =
    views.html.xmlAdmin.editExForm.render(user, exercise, isCreation)

}
