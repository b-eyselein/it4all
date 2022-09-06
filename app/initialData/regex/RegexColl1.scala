package initialData.regex

import initialData.InitialExercise
import model.Level
import model.tools.regex.{RegexCorrectionType, RegexExerciseContent, RegexMatchTestData}

object RegexColl1 {

  private val regexColl1Ex1 = InitialExercise(
    title = "Postleitzahlen",
    authors = Seq("bje40dc"),
    text = """Schreiben Sie einen regulären Ausdruck, der (deutsche) Postleitzahlen überprüfen kann.
             |Diese bestehen aus 5 Ziffern.""".stripMargin
      .replace("\n", " "),
    difficulty = Level.Beginner,
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
      sampleSolutions = Seq("""\d{5}""", """[0-9]{5}""", """\d\d\d\d\d""")
    )
  )

  private val regexColl1Ex2 = InitialExercise(
    title = "Einfache Datumsangaben",
    authors = Seq("bje40dc"),
    text = """Schreiben Sie einen regulären Ausdruck, der eine einfache Version von Datumsangaben findet.
             |Dabei sollen der Tag und der Monat jeweils aus zwei und die Jahresangabe aus vier Zahlen bestehen.
             |Als Trennzeichen soll jeweils ein Punkt verwendet werden.
             |Eine Validierung auf kalendarische Korrektheit (zum Beispiel, dass der 30.02.2019 oder der 99.17.3005 nicht
             |existieren) ist nicht erforderlich.
             |Außerdem sind führende Nullen erlaubt.""".stripMargin.replace("\n", " "),
    difficulty = Level.Intermediate,
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

  private val regexColl1Ex3 = InitialExercise(
    title = "Komplexe Datumsangaben",
    authors = Seq("bje40dc"),
    text = """Schreiben Sie einen regulären Ausdruck, der eine komplexere Version von Datumsangaben überprüfen kann.
             |Dabei sollen der Tag und der Monat jeweils aus ein oder zwei und die Jahresangabe aus zwei bis vier Ziffern
             |bestehen.
             |Als Trennzeichen soll jeweils ein Punkt verwendet werden.
             |Eine Validierung auf kalendarische Korrektheit ist nicht erforderlich.
             |Führende Nullen sind erlaubt.""".stripMargin.replace("\n", " "),
    difficulty = Level.Advanced,
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
        """\d\d?\.\d\d?\.\d\d\d?\d?""",
        """\d{1,2}\.\d{1,2}\.\d{2,4}""",
        """[0-9]{1,2}\.[0-9]{1,2}\.[0-9]{2,4}"""
      )
    )
  )

  private val regexColl1Ex4 = InitialExercise(
    title = "Semantische Versionierung",
    authors = Seq("bje40dc"),
    text = """Schreiben Sie einen regulären Ausruck, der semantische Versionsnummern überprüfen kann.
             |Diese bestehen aus einer Haupt-, einer Neben-, einer Revisions- und einer optionalen Buildnummer, die jeweils
             |mit einem Punkt beziehungsweise im Fall der Revisions- und Buildnummer mit einem Minus getrennt werden.
             |Jede der Nummern kann mehrstellig sein und führende Nullen besitzen.""".stripMargin.replace("\n", " "),
    difficulty = Level.Advanced,
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

  private val regexColl1Ex5 = InitialExercise(
    title = "E-Mail-Adressen",
    authors = Seq("bje40dc"),
    text = """Erstellen Sie einen regulären Ausdruck, der E-Mail-Adressen überprüfen kann.
             |Eine Mailadresse besteht aus einem lokalen und einem Domänenteil, die durch ein @-Zeichen getrennt sind.
             |Der Domänenteil wiederum besteht aus einem Hostnamen und einer Top-Level-Domain, die durch einen Punkt
             |getrennt sind.
             |Der Einfachkeit halber beschränken wir die Zeichen, die in einer Mailadresse vorkommen dürfen, auf alle
             |Klein- und Großbuchstaben, alle Ziffern, Minus, Punkt und Unterstrich (_).
             |Jeder Teil besteht aus mindestens einem dieser Zeichen.""".stripMargin.replace("\n", " "),
    difficulty = Level.Expert,
    content = RegexExerciseContent(
      maxPoints = 4,
      correctionType = RegexCorrectionType.MATCHING,
      matchTestData = Seq(
        RegexMatchTestData(id = 1, data = "james.bond@mi5.uk", isIncluded = true),
        RegexMatchTestData(id = 2, data = "jason_bourne@cia.gov", isIncluded = true),
        RegexMatchTestData(id = 3, data = "jack.bauer@24.com", isIncluded = true),
        RegexMatchTestData(id = 4, data = "xyz@localhost", isIncluded = false),
        RegexMatchTestData(id = 5, data = "@web.de", isIncluded = false),
        RegexMatchTestData(id = 6, data = "test.net", isIncluded = false)
      ),
      extractionTestData = Seq.empty,
      sampleSolutions = Seq(
        """([a-z]|[A-Z]|[0-9]|_|-|\.)+@([a-z]|[A-Z]|[0-9]|_|-)+\.([a-z]|[A-Z]|[0-9]|_|-|\.)+"""
      )
    )
  )

  val initialCollections: Map[Int, InitialExercise[RegexExerciseContent]] = Map(
    1 -> regexColl1Ex1,
    2 -> regexColl1Ex2,
    3 -> regexColl1Ex3,
    4 -> regexColl1Ex4,
    5 -> regexColl1Ex5
  )

}
