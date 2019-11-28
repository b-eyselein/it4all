package model.tools.collectionTools.web

import de.uniwue.webtester.SiteSpec
import model.tools.collectionTools._

sealed abstract class WebExPart(val partName: String, val urlName: String) extends ExPart


object WebExParts extends ExParts[WebExPart] {

  val values: IndexedSeq[WebExPart] = findValues


  case object HtmlPart extends WebExPart("Html-Teil", "html")

  case object JsPart extends WebExPart("Js-Teil", "js")

}


final case class WebExerciseContent(
  htmlText: Option[String],
  jsText: Option[String],
  siteSpec: SiteSpec,
  files: Seq[ExerciseFile],
  sampleSolutions: Seq[SampleSolution[Seq[ExerciseFile]]]
) extends FileExerciseContent

