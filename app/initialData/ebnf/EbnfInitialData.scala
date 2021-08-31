package initialData.ebnf

import initialData.{InitialData, InitialExercise}
import model.tools.ebnf.EbnfExerciseContent
import model.tools.ebnf.EbnfTool.EbnfExercise
import model.{Exercise, ExerciseCollection}

abstract class RegexInitialExercise(collectionId: Int, exerciseId: Int) extends InitialExercise("ebnf", collectionId, exerciseId)

object RegexColl01Ex01 extends RegexInitialExercise(1, 1) {

  val ebnfColl1Ex1: EbnfExercise = Exercise(
    exerciseId,
    collectionId,
    toolId,
    title = "TODO!",
    Seq("bje40dc"),
    text = "TODO: exercise text...",
    Seq.empty,
    difficulty = 1,
    EbnfExerciseContent(
      sampleSolutions = Seq.empty
    )
  )

}

object EbnfInitialData extends InitialData[EbnfExerciseContent] {

  override protected val toolId: String = "ebnf"

  override val exerciseData: Seq[(ExerciseCollection, Seq[Exercise[EbnfExerciseContent]])] = Seq(
    (ExerciseCollection(1, toolId, "EBNF - Grundlagen", Seq("bje40dc")), Seq(RegexColl01Ex01.ebnfColl1Ex1))
  )

}
