package initialData.ebnf

import initialData.InitialData
import model.tools.ebnf.EbnfExerciseContent
import model.tools.ebnf.EbnfTool.EbnfExercise
import model.{Exercise, ExerciseCollection}

object EbnfInitialData extends InitialData[EbnfExerciseContent] {

  override protected val toolId: String = "ebnf"

  private val ebnfColl1Ex1: EbnfExercise = Exercise(
    1,
    1,
    toolId,
    "TODO!",
    Seq("bje40dc"),
    "TODO: exercise text...",
    Seq.empty,
    1,
    EbnfExerciseContent(Seq.empty)
  )

  override val exerciseData: Seq[(ExerciseCollection, Seq[Exercise[EbnfExerciseContent]])] = Seq(
    (ExerciseCollection(1, toolId, "EBNF - Grundlagen", Seq("bje40dc")), Seq(ebnfColl1Ex1))
  )

}
