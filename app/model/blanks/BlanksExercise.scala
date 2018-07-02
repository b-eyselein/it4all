package model.blanks

import model.{Exercise, ExerciseState, PartSolution, PartsCompleteEx}
import play.twirl.api.Html


// Classes for user

case class BlanksCompleteExercise(ex: BlanksExercise, samples: Seq[BlanksAnswer]) extends PartsCompleteEx[BlanksExercise, BlanksExPart] {

  override def preview: Html = // FIXME: move to toolMain!
    views.html.idExercises.blanks.blanksPreview(this)

  override def hasPart(partType: BlanksExPart): Boolean = true

}


case class BlanksExercise(override val id: Int, override val title: String, override val author: String, override val text: String, override val state: ExerciseState,
                          rawBlanksText: String, blanksText: String) extends Exercise {

  def this(baseValues: (Int, String, String, String, ExerciseState), rawBlanksText: String, blanksText: String) =
    this(baseValues._1, baseValues._2, baseValues._3, baseValues._4, baseValues._5, rawBlanksText, blanksText)

}

case class BlanksAnswer(id: Int, exerciseId: Int, solution: String)

case class BlanksSolution(username: String, exerciseId: Int, part: BlanksExPart, solution: Seq[BlanksAnswer],
                          points: Double, maxPoints: Double) extends PartSolution[BlanksExPart, Seq[BlanksAnswer]]

