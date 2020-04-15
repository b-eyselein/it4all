package model.tools.web

import de.uniwue.webtester.sitespec.SiteSpec
import enumeratum.{EnumEntry, PlayEnum}
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

sealed trait WebExTag extends EnumEntry

case object WebExTag extends PlayEnum[WebExTag] {

  override val values: IndexedSeq[WebExTag] = findValues

  case object WebExTagTodo extends WebExTag

}

final case class WebExercise(
  id: Int,
  collectionId: Int,
  toolId: String,
  semanticVersion: SemanticVersion,
  title: String,
  authors: Seq[String],
  text: String,
  tags: Seq[WebExTag],
  difficulty: Option[Int],
  sampleSolutions: Seq[SampleSolution[WebSolution]],
  htmlText: Option[String],
  jsText: Option[String],
  siteSpec: SiteSpec,
  files: Seq[ExerciseFile]
) extends Exercise {

  override type ET      = WebExTag
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
