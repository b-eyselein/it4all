package initialData.regex.coll_1

import model.tools.regex.RegexTool.RegexExercise
import model.tools.regex.{RegexCorrectionType, RegexExerciseContent, RegexMatchTestData}
import model.{Exercise, SampleSolution}

object RegexColl1Ex3 {

  val regexColl1Ex3: RegexExercise = Exercise(
    exerciseId = 3,
    collectionId = 1,
    toolId = "regex",
    title = "Komplexe Datumsangaben",
    authors = Seq("bje40dc"),
    text =
      """Schreiben Sie einen regulären Ausdruck, der eine komplexere Version von Datumsangaben überprüfen kann.
        |Dabei sollen der Tag und der Monat jeweils aus ein oder zwei und die Jahresangabe aus zwei bis vier Ziffern
        |bestehen.
        |Als Trennzeichen soll jeweils ein Punkt verwendet werden.
        |Eine Validierung auf kalendarische Korrektheit ist nicht erforderlich.
        |Führende Nullen sind erlaubt.""".stripMargin.replace("\n", " "),
    difficulty = 3,
    content = RegexExerciseContent(
      maxPoints = 3,
      correctionType = RegexCorrectionType.MATCHING,
      matchTestData = Seq(
        RegexMatchTestData(id = 1, data = "12.01.1999", isIncluded = true),
        RegexMatchTestData(id = 2, data = "99.17.3006", isIncluded = true),
        RegexMatchTestData(id = 3, data = "33.11.1931", isIncluded = true),
        RegexMatchTestData(id = 4, data = "1.1.1999", isIncluded = true),
        RegexMatchTestData(id = 5, data = "01.01.0700", isIncluded = true),
        RegexMatchTestData(id = 6, data = "01.01.700", isIncluded = true),
        RegexMatchTestData(id = 7, data = "3.12.2011", isIncluded = true),
        RegexMatchTestData(id = 8, data = "29.3.2018", isIncluded = true),
        RegexMatchTestData(id = 9, data = "1.1.1", isIncluded = false),
        RegexMatchTestData(id = 10, data = "12.10", isIncluded = false),
        RegexMatchTestData(id = 11, data = "11.2.", isIncluded = false),
        RegexMatchTestData(id = 12, data = "12-01-1999", isIncluded = false)
      ),
      extractionTestData = Seq.empty,
      sampleSolutions = Seq(
        SampleSolution(id = 1, sample = """\d\d?\.\d\d?\.\d\d\d?\d?"""),
        SampleSolution(id = 2, sample = """\d{1,2}\.\d{1,2}\.\d{2,4}"""),
        SampleSolution(id = 3, sample = """[0-9]{1,2}\.[0-9]{1,2}\.[0-9]{2,4}""")
      )
    )
  )

}
