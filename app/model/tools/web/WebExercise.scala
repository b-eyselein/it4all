package model.tools.web

import enumeratum.Enum
import model.tools.web.sitespec.SiteSpec
import model.{ExPart, ExerciseFile, FileExerciseContent, FilesSolution}

sealed abstract class WebExPart(val partName: String, val id: String) extends ExPart

object WebExPart extends Enum[WebExPart] {

  case object HtmlPart extends WebExPart(partName = "Html-Teil", id = "html")
  case object JsPart   extends WebExPart(partName = "Js-Teil", id = "js")

  val values: IndexedSeq[WebExPart] = findValues

}

final case class WebExerciseContent(
  siteSpec: SiteSpec,
  files: Seq[ExerciseFile],
  sampleSolutions: Seq[FilesSolution],
  htmlText: Option[String] = None,
  jsText: Option[String] = None
) extends FileExerciseContent {

  override def parts: Seq[ExPart] = {
    val htmlPart = if (siteSpec.htmlTasks.nonEmpty) Some(WebExPart.HtmlPart) else None
    val jsPart   = if (siteSpec.jsTasks.nonEmpty) Some(WebExPart.JsPart) else None

    Seq(htmlPart, jsPart).flatten
  }

}
