package initialData.regex.coll_1

import model.Exercise
import model.tools.regex.RegexTool.RegexExercise
import model.tools.regex.{RegexCorrectionType, RegexExerciseContent, RegexMatchTestData}

object RegexColl1Ex4 {

  val regexColl1Ex4: RegexExercise = Exercise(
    exerciseId = 4,
    collectionId = 1,
    toolId = "regex",
    title = "Semantische Versionierung",
    authors = Seq("bje40dc"),
    text =
      """Schreiben Sie einen regulären Ausruck, der semantische Versionsnummern überprüfen kann.
        |Diese bestehen aus einer Haupt-, einer Neben-, einer Revisions- und einer optionalen Buildnummer, die jeweils
        |mit einem Punkt beziehungsweise im Fall der Revisions- und Buildnummer mit einem Minus getrennt werden.
        |Jede der Nummern kann mehrstellig sein und führende Nullen besitzen.""".stripMargin.replace("\n", " "),
    difficulty = 3,
    content = RegexExerciseContent(
      maxPoints = 3,
      correctionType = RegexCorrectionType.MATCHING,
      matchTestData = Seq(
        RegexMatchTestData(id = 1, data = "2.3.5-0041", isIncluded = true),
        RegexMatchTestData(id = 2, data = "0.0.1", isIncluded = true),
        RegexMatchTestData(id = 3, data = "1.3.3-7", isIncluded = true),
        RegexMatchTestData(id = 4, data = "1.12.2-21", isIncluded = true),
        RegexMatchTestData(id = 5, data = "2.05.7-11", isIncluded = true),
        RegexMatchTestData(id = 6, data = "1.0", isIncluded = false),
        RegexMatchTestData(id = 7, data = "1..10", isIncluded = false),
        RegexMatchTestData(id = 8, data = "1..10", isIncluded = false),
        RegexMatchTestData(id = 9, data = "1.0-3", isIncluded = false)
      ),
      extractionTestData = Seq.empty,
      sampleSolutions = Seq(
        """\d+\.\d+\.\d+(-\d+)?""",
        """[0-9]+\.[0-9]+\.[0-9]+(-[0-9]+)?"""
      )
    )
  )

}
