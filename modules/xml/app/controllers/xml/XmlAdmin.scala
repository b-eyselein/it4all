package controllers.xml

import model.XmlExercise
import play.twirl.api.Html

import scala.collection.JavaConverters.asScalaBufferConverter

class XmlAdmin @javax.inject.Inject()(f: play.data.FormFactory)
  extends controllers.core.AExerciseAdminController[XmlExercise](f, XmlToolObject, XmlExercise.finder, model.XmlExerciseReader) {

  override def statistics = new Html(
    s"""<li>Es existieren insgesamt ${XmlExercise.finder.all.size} <a href="${controllers.xml.routes.XmlAdmin.exercises()}">Aufgaben</a>, davon
       |  <ul>
       |    ${XmlExercise.finder.all.asScala.groupBy(_.exerciseType).map({ case (exType, exes) => s"""<li>${exes.size} Aufgaben von Typ $exType""" }).mkString("\n")}
       |  </ul>
       |</li>""".stripMargin)

  override def renderExEditForm(u: model.user.User, e: XmlExercise, isCreation: Boolean): Html = views.html.editExForm.render(u, e, isCreation)

}
