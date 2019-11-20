package model.tools.collectionTools.web

import de.uniwue.webtester.{SiteSpec, WebTask}
import model._
import model.core.LongText
import model.points._
import model.tools.collectionTools.{ExPart, ExParts, ExerciseFile, FileExercise, LoadExerciseFilesMessage}

import scala.collection.immutable.IndexedSeq

sealed abstract class WebExPart(val partName: String, val urlName: String) extends ExPart


object WebExParts extends ExParts[WebExPart] {

  val values: IndexedSeq[WebExPart] = findValues


  case object HtmlPart extends WebExPart("Html-Teil", "html")

  case object JsPart extends WebExPart("Js-Teil", "js")

}


final case class WebExercise(
  id: Int, collectionId: Int, toolId: String = WebConsts.toolId, semanticVersion: SemanticVersion,
  title: String, author: String, text: LongText, state: ExerciseState,
  htmlText: Option[String],
  jsText: Option[String],
  siteSpec: SiteSpec,
  files: Seq[ExerciseFile],
  sampleSolutions: Seq[FilesSampleSolution]
) extends FileExercise {

  def tasksForPart(part: WebExPart): Seq[WebTask] = part match {
    case WebExParts.HtmlPart => siteSpec.htmlTasks
    case WebExParts.JsPart   => siteSpec.jsTasks
  }

}


final case class WebSolution(htmlSolution: String, jsSolution: Option[String])

final case class WebUserSolution(id: Int, part: WebExPart, solution: Seq[ExerciseFile], points: Points, maxPoints: Points)
  extends UserSolution[WebExPart, Seq[ExerciseFile]]

final case class WebExerciseReview(difficulty: Difficulty, maybeDuration: Option[Int]) extends ExerciseReview
