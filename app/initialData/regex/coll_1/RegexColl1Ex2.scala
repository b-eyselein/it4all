package initialData.regex.coll_1

import initialData.InitialExercise
import model.tools.regex.{RegexCorrectionType, RegexExerciseContent, RegexMatchTestData}

object RegexColl1Ex2
    extends InitialExercise(
      title = "Einfache Datumsangaben",
      authors = Seq("bje40dc"),
      text = """\
        Schreiben Sie einen regulären Ausdruck, der eine einfache Version von Datumsangaben findet.
               |Dabei sollen der Tag und der Monat jeweils aus zwei und die Jahresangabe aus vier Zahlen bestehen.
               |Als Trennzeichen soll jeweils ein Punkt verwendet werden.
               |Eine Validierung auf kalendarische Korrektheit (zum Beispiel, dass der 30.02.2019 oder der 99.17.3005 nicht
               |existieren) ist nicht erforderlich.
               |Außerdem sind führende Nullen erlaubt.""".stripMargin.replace("\n", " "),
      difficulty = 2,
      content = RegexExerciseContent(
        maxPoints = 2,
        correctionType = RegexCorrectionType.MATCHING,
        matchTestData = Seq(
          RegexMatchTestData(id = 1, data = "12.01.1999", isIncluded = true),
          RegexMatchTestData(id = 2, data = "99.17.3006", isIncluded = true),
          RegexMatchTestData(id = 3, data = "33.11.1931", isIncluded = true),
          RegexMatchTestData(id = 4, data = "01.01.0700", isIncluded = true),
          RegexMatchTestData(id = 5, data = "1.1.1999", isIncluded = false),
          RegexMatchTestData(id = 6, data = "01.01.700", isIncluded = false),
          RegexMatchTestData(id = 7, data = "3.12.2011", isIncluded = false),
          RegexMatchTestData(id = 8, data = "29.3.2018", isIncluded = false),
          RegexMatchTestData(id = 9, data = "12-01-1999", isIncluded = false),
          RegexMatchTestData(id = 10, data = "1.1.1", isIncluded = false),
          RegexMatchTestData(id = 11, data = "12.10", isIncluded = false),
          RegexMatchTestData(id = 12, data = "11.2.", isIncluded = false)
        ),
        extractionTestData = Seq.empty,
        sampleSolutions = Seq("""\d\d\.\d\d\.\d\d\d\d""", """\d{2}\.\d{2}\.\d{4}""", """[0-9]{2}\.[0-9]{2}\.[0-9]{4}""")
      )
    )
