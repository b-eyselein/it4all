package initialData.regex.coll_1

import model.Exercise
import model.tools.regex.RegexTool.RegexExercise
import model.tools.regex.{RegexCorrectionType, RegexExerciseContent, RegexMatchTestData}

object RegexColl1Ex1 {

  val regexColl1Ex1: RegexExercise = Exercise(
    exerciseId = 1,
    collectionId = 1,
    toolId = "regex",
    title = "Postleitzahlen",
    authors = Seq("bje40dc"),
    text = """Schreiben Sie einen regulären Ausdruck, der (deutsche) Postleitzahlen überprüfen kann.
             |Diese bestehen aus 5 Ziffern.""".stripMargin
      .replace("\n", " "),
    difficulty = 1,
    content = RegexExerciseContent(
      maxPoints = 1,
      correctionType = RegexCorrectionType.MATCHING,
      matchTestData = Seq(
        RegexMatchTestData(id = 1, data = "97070", isIncluded = true),
        RegexMatchTestData(id = 2, data = "90210", isIncluded = true),
        RegexMatchTestData(id = 3, data = "10115", isIncluded = true),
        RegexMatchTestData(id = 4, data = "65432", isIncluded = true),
        RegexMatchTestData(id = 5, data = "01234", isIncluded = true),
        RegexMatchTestData(id = 6, data = "8911", isIncluded = false),
        RegexMatchTestData(id = 7, data = "654321", isIncluded = false)
      ),
      extractionTestData = Seq.empty,
      sampleSolutions = Seq("""\d{5}""", """[0-9]{}""", """\d\d\d\d\d""")
    )
  )

}
