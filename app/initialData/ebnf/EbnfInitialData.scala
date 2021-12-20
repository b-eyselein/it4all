package initialData.ebnf

import initialData.{InitialCollection, InitialData, InitialExercise}
import model.tools.ebnf.{EbnfExerciseContent, EbnfGrammar, EbnfRule}

object EbnfColl01Ex01
    extends InitialExercise(
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

object EbnfInitialData extends InitialData[EbnfExerciseContent] {

  type EbnfInitialExercise = InitialExercise[EbnfExerciseContent]

  override val initialData = Map(
    1 -> InitialCollection(
      "EBNF - Grundlagen",
      Seq("bje40dc"),
      initialExercises = Map(
        1 -> EbnfColl01Ex01
      )
    )
  )

}
