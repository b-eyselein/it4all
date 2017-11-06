package model.web

import model.core.{ExTag, Exercise}
import play.twirl.api.Html

class WebExTag(part: String, hasExes: Boolean) extends ExTag {

  override def cssClass: String = if (hasExes) "label label-primary" else "label label-default"

  override def buttonContent: String = part

  override def title = s"Diese Aufgabe besitzt ${if (!hasExes) "k" else ""}einen $part-Teil"

}

case class WebExercise(ex: DbWebExercise, htmlTasks: Seq[DbHtmlTask], jsTasks: Seq[DbJsTask]) extends Exercise(ex.id, ex.title, ex.author, ex.text, ex.state) {

  override def tags: List[ExTag] = List(new WebExTag("Html", htmlTasks.nonEmpty), new WebExTag("Js", jsTasks.nonEmpty))

  override def renderRest: Html = new Html(
    s"""<td>
       |    <a href="@controllers.exes.routes.WebController.exRest(exercise.id)">${htmlTasks.size} / ${jsTasks.size}</a>
       |</td>
       |<td>
       |    <a href="@controllers.exes.routes.WebController.exRest(exercise.id)">${renderText(ex.htmlText)} / ${renderText(ex.jsText)}</a>
       |</td> """.stripMargin)

  private def renderText(str: String) = if (text == null || text.isEmpty)
    """<span class="glyphicon glyphicon-remove"></span>"""
  else
    """<span class="glyphicon glyphicon-ok"></span>"""

}