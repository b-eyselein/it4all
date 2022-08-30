package initialData.ebnf

import initialData.{InitialCollection, InitialData, InitialExercise}
import model.tools.ebnf.{EbnfExerciseContent, EbnfGrammar, EbnfRule}

object EbnfInitialData extends InitialData[EbnfExerciseContent] {

  private val ebnfColl01Ex01 = InitialExercise(
    title = "Binärpalindrome",
    Seq("bje40dc"),
    text = "Erstellen Sie eine EBNF-Grammatik, die alle Binärzahlen erzeugt, die gleichzeitig auch Palindrome sein!",
    difficulty = 1,
    content = EbnfExerciseContent(
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

  override val initialData: Map[Int, InitialCollection[EbnfExerciseContent]] = Map(
    1 -> InitialCollection(
      "EBNF - Grundlagen",
      initialExercises = Map(
        1 -> ebnfColl01Ex01
      )
    )
  )

}
