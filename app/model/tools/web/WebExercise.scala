package model.tools.web

import de.uniwue.webtester.{SiteSpec, WebTask}
import enumeratum.{EnumEntry, PlayEnum}
import model._
import model.points._
import play.twirl.api.Html

import scala.collection.immutable.IndexedSeq

sealed abstract class WebExPart(val partName: String, val urlName: String) extends ExPart with EnumEntry


object WebExParts extends PlayEnum[WebExPart] {

  val values: IndexedSeq[WebExPart] = findValues

  case object HtmlPart extends WebExPart("Html-Teil", "html")

  case object JsPart extends WebExPart("Js-Teil", "js")

}


final case class WebCollection(id: Int, title: String, author: String, text: String, state: ExerciseState, shortName: String)
  extends ExerciseCollection

final case class WebExercise(id: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String, state: ExerciseState,
                             htmlText: Option[String], jsText: Option[String], siteSpec: SiteSpec, files: Seq[ExerciseFile],
                             sampleSolutions: Seq[FilesSampleSolution]
                            ) extends Exercise with FileExercise[WebExPart] {

  override def preview: Html = // FIXME: move to toolMain!
    views.html.toolViews.web.webPreview(this)

  def tasksForPart(part: WebExPart): Seq[WebTask] = part match {
    case WebExParts.HtmlPart => siteSpec.htmlTasks
    case WebExParts.JsPart   => siteSpec.jsTasks
  }

  override def filesForExercisePart(part: WebExPart): LoadExerciseFilesMessage = LoadExerciseFilesMessage(files, None)

}


final case class WebSolution(htmlSolution: String, jsSolution: Option[String])

final case class WebSampleSolution(id: Int, sample: Seq[ExerciseFile])
  extends SampleSolution[Seq[ExerciseFile]]

final case class WebUserSolution(id: Int, part: WebExPart, solution: Seq[ExerciseFile], points: Points, maxPoints: Points)
  extends UserSolution[WebExPart, Seq[ExerciseFile]]

final case class WebExerciseReview(difficulty: Difficulty, maybeDuration: Option[Int]) extends ExerciseReview
