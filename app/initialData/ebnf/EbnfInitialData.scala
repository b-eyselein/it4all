package initialData.ebnf

import initialData.{InitialData, InitialExercise}
import model.tools.ebnf.EbnfTool.EbnfExercise
import model.tools.ebnf.{EbnfExerciseContent, EbnfGrammar, EbnfRule}
import model.{Exercise, ExerciseCollection}

abstract class RegexInitialExercise(collectionId: Int, exerciseId: Int) extends InitialExercise("ebnf", collectionId, exerciseId)

object RegexColl01Ex01 extends RegexInitialExercise(1, 1) {

  val ebnfColl1Ex1: EbnfExercise = Exercise(
    exerciseId,
    collectionId,
    toolId,
    title = "Binärpalindrome",
    Seq("bje40dc"),
    text = "Erstellen Sie eine EBNF-Grammatik, die alle Binärzahlen erzeugt, die gleichzeitig auch Palindrome sein!",
    Seq.empty,
    difficulty = 1,
    EbnfExerciseContent(
      predefinedTerminals = Some(Seq("0", "1")),
      sampleSolutions = Seq(
        EbnfGrammar(
          startSymbol = "S",
          rules = Seq(
            EbnfRule("S", "'1' S '1' | '0' S '0' | '1' '1' | '0' '0' | '1' | '0'")
          )
        )
      )
    )
  )

}

object EbnfInitialData extends InitialData[EbnfExerciseContent] {

  override protected val toolId: String = "ebnf"

  override val exerciseData: Seq[(ExerciseCollection, Seq[Exercise[EbnfExerciseContent]])] = Seq(
    (ExerciseCollection(1, toolId, "EBNF - Grundlagen", Seq("bje40dc")), Seq(RegexColl01Ex01.ebnfColl1Ex1))
  )

}
