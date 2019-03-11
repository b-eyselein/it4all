package model.tools.web

import de.uniwue.webtester.{SiteSpec, WebTask}
import model._
import model.points._
import play.twirl.api.Html

import scala.language.postfixOps

final case class WebCollection(id: Int, title: String, author: String, text: String, state: ExerciseState, shortName: String)
  extends ExerciseCollection

final case class WebExercise(id: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String, state: ExerciseState,
                             htmlText: Option[String], jsText: Option[String], siteSpec: SiteSpec, files: Seq[ExerciseFile],
                             sampleSolutions: Seq[WebSampleSolution]) extends Exercise {

  override def baseValues: BaseValues = BaseValues(id, semanticVersion, title, author, text, state)

  // other methods

  override def preview: Html = // FIXME: move to toolMain!
    views.html.toolViews.web.webPreview(this)

  def maxPoints(part: WebExPart): Points = part match {
    case WebExParts.HtmlPart => addUp(???) // htmlTasks.map(_.maxPoints))
    case WebExParts.JsPart   => addUp(???) // jsTasks.map(_.maxPoints))
  }

  def tasksForPart(part: WebExPart): Seq[WebTask] = part match {
    case WebExParts.HtmlPart => siteSpec.htmlTasks
    case WebExParts.JsPart   => siteSpec.jsTasks
  }

}


final case class WebSolution(htmlSolution: String, jsSolution: Option[String])

final case class WebSampleSolution(id: Int, sample: WebSolution)
  extends SampleSolution[WebSolution]

final case class WebUserSolution(id: Int, part: WebExPart, solution: WebSolution, points: Points, maxPoints: Points)
  extends UserSolution[WebExPart, WebSolution]

final case class WebExerciseReview(difficulty: Difficulty, maybeDuration: Option[Int]) extends ExerciseReview
