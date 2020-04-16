package model.tools.web

import de.uniwue.webtester.sitespec.SiteSpec
import model.tools._

sealed abstract class WebExPart(val partName: String, val urlName: String) extends ExPart

object WebExParts extends ExParts[WebExPart] {

  val values: IndexedSeq[WebExPart] = findValues

  case object HtmlPart extends WebExPart("Html-Teil", "html")

  case object JsPart extends WebExPart("Js-Teil", "js")

}

final case class WebSolution(
  files: Seq[ExerciseFile]
)

final case class WebExercise(
  id: Int,
  collectionId: Int,
  toolId: String,
  title: String,
  authors: Seq[String],
  text: String,
  topics: Seq[Topic],
  difficulty: Option[Int],
  sampleSolutions: Seq[SampleSolution[WebSolution]],
  htmlText: Option[String],
  jsText: Option[String],
  siteSpec: SiteSpec,
  files: Seq[ExerciseFile]
) extends Exercise {

  override type SolType = WebSolution

}

final case class WebExerciseContent(
  htmlText: Option[String],
  jsText: Option[String],
  siteSpec: SiteSpec,
  files: Seq[ExerciseFile],
  sampleSolutions: Seq[SampleSolution[WebSolution]]
) extends ExerciseContent {

  override type SolType = WebSolution

}
