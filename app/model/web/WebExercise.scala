package model.web

import model.core.ExTag
import play.twirl.api.Html



//case class WebExercise(ex: DbWebExercise, htmlTasks: Seq[HtmlTask], jsTasks: Seq[JsTask]) extends Exercise(ex.id, ex.title, ex.author, ex.text, ex.state) {
//
//  override def tags: List[ExTag] = List(new WebExTag("Html", htmlTasks.nonEmpty), new WebExTag("Js", jsTasks.nonEmpty))
//
//  override def renderRest: Html = new Html(
//    s"""<td>
//       |    <a href="@controllers.exes.routes.WebController.exRest(exercise.id)">${htmlTasks.size} / ${jsTasks.size}</a>
//       |</td>
//       |<td>
//       |    <a href="@controllers.exes.routes.WebController.exRest(exercise.id)">${renderText(ex.htmlText)} / ${renderText(ex.jsText)}</a>
//       |</td> """.stripMargin)
//
//  private def renderText(str: String) = if (text == null || text.isEmpty)
//    """<span class="glyphicon glyphicon-remove"></span>"""
//  else
//    """<span class="glyphicon glyphicon-ok"></span>"""
//
//}