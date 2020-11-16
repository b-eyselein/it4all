package model.tools.web

import de.uniwue.webtester.sitespec.SiteSpec
import model._

sealed abstract class WebExPart(val partName: String, val id: String) extends ExPart

object WebExPart extends ExParts[WebExPart] {

  val values: IndexedSeq[WebExPart] = findValues

  case object HtmlPart extends WebExPart(partName = "Html-Teil", id = "html")

  case object JsPart extends WebExPart(partName = "Js-Teil", id = "js")

}

final case class WebExerciseContent(
  siteSpec: SiteSpec,
  files: Seq[ExerciseFile],
  sampleSolutions: Seq[FilesSolution],
  htmlText: Option[String] = None,
  jsText: Option[String] = None
) extends ExerciseContent {

  override protected type S = FilesSolution

  override def parts: Seq[ExPart] = {
    val htmlPart = if (siteSpec.htmlTasks.nonEmpty) Some(WebExPart.HtmlPart) else None
    val jsPart   = if (siteSpec.jsTasks.nonEmpty) Some(WebExPart.JsPart) else None

    Seq(htmlPart, jsPart).flatten
  }

}
