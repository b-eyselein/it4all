package initialData.regex.coll_2

import model.Exercise
import model.tools.regex.RegexTool.RegexExercise
import model.tools.regex.{RegexCorrectionType, RegexExerciseContent, RegexExtractionTestData}

object RegexColl2Ex1 {

  val regexColl2Ex1: RegexExercise = Exercise(
    exerciseId = 1,
    collectionId = 2,
    toolId = "regex",
    title = "Heiße Preise",
    authors = Seq("bje40dc"),
    text = """Schreiben Sie einen regulären Ausdruck, der Preise aus Angeboten extrahiert.
             |Diese sollen aus einer Ganz- oder Fließkommazahl mit maximal zwei Nachkommastellen bestehen.
             |Es soll kein Symbol für eine Währungeinheit mitextrahiert werden.""".stripMargin.replace("\n", " "),
    difficulty = 3,
    content = RegexExerciseContent(
      maxPoints = 2,
      correctionType = RegexCorrectionType.EXTRACTION,
      matchTestData = Seq.empty,
      extractionTestData = Seq(
        RegexExtractionTestData(id = 1, base = "Frische Äpfel= 2,99€/kg, Bananen= 3,49€/kg, Ananas 1,99€/Stück"),
        RegexExtractionTestData(id = 2, base = "Reifenwechsel= 50€, Ölservice= 99€, Lichtcheck= 119,99€")
      ),
      sampleSolutions = Seq("""\d+[,\d\d?]?""")
    )
  )

}
