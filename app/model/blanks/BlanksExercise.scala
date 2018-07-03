package model.blanks

import model._
import play.twirl.api.Html


// Classes for user

case class BlanksCompleteExercise(ex: BlanksExercise, samples: Seq[BlanksAnswer]) extends SingleCompleteEx[BlanksExercise, BlanksExPart] {

  override def preview: Html = // FIXME: move to toolMain!
    views.html.idExercises.blanks.blanksPreview(this)

  override def hasPart(partType: BlanksExPart): Boolean = true

}


case class BlanksExercise(id: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String, state: ExerciseState,
                          rawBlanksText: String, blanksText: String) extends Exercise

case class BlanksAnswer(id: Int, exerciseId: Int, exSemVer: SemanticVersion, solution: String)

case class BlanksSolution(username: String, exerciseId: Int, exSemVer: SemanticVersion, part: BlanksExPart, solution: Seq[BlanksAnswer],
                          points: Double, maxPoints: Double) extends DBPartSolution[BlanksExPart, Seq[BlanksAnswer]]

