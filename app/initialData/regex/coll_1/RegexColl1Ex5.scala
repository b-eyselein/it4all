package initialData.regex.coll_1

import initialData.InitialExercise
import model.tools.regex.{RegexCorrectionType, RegexExerciseContent, RegexMatchTestData}

object RegexColl1Ex5
    extends InitialExercise(
      title = "E-Mail-Adressen",
      authors = Seq("bje40dc"),
      text = """Erstellen Sie einen regulären Ausdruck, der E-Mail-Adressen überprüfen kann.
               |Eine Mailadresse besteht aus einem lokalen und einem Domänenteil, die durch ein @-Zeichen getrennt sind.
               |Der Domänenteil wiederum besteht aus einem Hostnamen und einer Top-Level-Domain, die durch einen Punkt
               |getrennt sind.
               |Der Einfachkeit halber beschränken wir die Zeichen, die in einer Mailadresse vorkommen dürfen, auf alle
               |Klein- und Großbuchstaben, alle Ziffern, Minus, Punkt und Unterstrich (_).
               |Jeder Teil besteht aus mindestens einem dieser Zeichen.""".stripMargin.replace("\n", " "),
      difficulty = 4,
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
