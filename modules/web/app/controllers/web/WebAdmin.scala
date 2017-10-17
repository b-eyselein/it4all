package controllers.web

import javax.inject.Inject

import controllers.core.AExerciseAdminController
import model.{WebExercise, WebExerciseReader}
import play.data.FormFactory
import play.mvc.{Result, Results}
import play.twirl.api.Html

class WebAdmin @Inject()(f: FormFactory)
  extends AExerciseAdminController[WebExercise](f, WebToolObject, WebExercise.finder, WebExerciseReader) {

  override def statistics = new Html(
    s"""<li>Es existieren insgesamt ${WebExercise.finder.all.size} Aufgaben, davon
       |  <ul>
       |    <li>${WebExercise.withHtmlPart} Aufgaben mit HTML-Teil</li>
       |    <li>${WebExercise.withJsPart} Aufgaben mit JS-Teil</li>
       |  </ul>
       |</li>""".stripMargin)

  def exRest(exerciseId: Int): Result = Results.ok(views.html.webAdmin.webExRest.render(getUser, finder.byId(exerciseId)))

}
