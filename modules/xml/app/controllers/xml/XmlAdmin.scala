package controllers.xml

import javax.inject.Inject

import controllers.core.AExerciseAdminController
import model.user.User
import model.{XmlExercise, XmlExerciseReader}
import play.data.FormFactory
import play.twirl.api.Html

import scala.collection.JavaConverters.asScalaBufferConverter

class XmlAdmin @Inject()(f: FormFactory)
  extends AExerciseAdminController[XmlExercise](f, XmlToolObject, XmlExercise.finder, XmlExerciseReader) {

  override def statistics = new Html(
    s"""
<li>Es existieren insgesamt ${XmlExercise.finder.all.size} <a href="${controllers.xml.routes.XmlAdmin.exercises()}">Aufgaben</a>, davon
  <ul>
  ${
      XmlExercise.finder.all.asScala.groupBy(_.exerciseType).map({
        case (exType, exes) => s"""<li>${exes.size} Aufgaben von Typ $exType"""
      }).mkString("\n")
    }
  </ul>
</li>""")

  override def renderExEditForm(u: User, e: XmlExercise, isCreation: Boolean): Html = views.html.editExForm.render(u, e, isCreation)

}
