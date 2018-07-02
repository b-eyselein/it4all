package model.blanks

import model._
import play.twirl.api.Html


// Classes for user

case class BlanksCompleteExercise(ex: BlanksExercise, samples: Seq[BlanksAnswer]) extends PartsCompleteEx[BlanksExercise, BlanksExPart] {

  override def preview: Html = // FIXME: move to toolMain!
    views.html.idExercises.blanks.blanksPreview(this)

  override def hasPart(partType: BlanksExPart): Boolean = true

}


case class BlanksExercise(id: Int, title: String, author: String, text: String, state: ExerciseState, semanticVersion: SemanticVersion,
                          rawBlanksText: String, blanksText: String) extends Exercise

case class BlanksAnswer(id: Int, exerciseId: Int, solution: String)

case class BlanksSolution(username: String, exerciseId: Int, part: BlanksExPart, solution: Seq[BlanksAnswer],
                          points: Double, maxPoints: Double) extends PartSolution[BlanksExPart, Seq[BlanksAnswer]]

