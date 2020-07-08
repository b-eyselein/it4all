package initialData.regex.coll_2

import model.tools.regex.RegexTool.RegexExercise
import model.tools.regex.{RegexCorrectionType, RegexExerciseContent, RegexExtractionTestData}
import model.{Exercise, SampleSolution}

object RegexColl1Ex2 {

  val RegexColl2Ex2: RegexExercise = Exercise(
    exerciseId = 2,
    collectionId = 2,
    toolId = "regex",
    title = "Kilometerstände",
    authors = Seq("bje40dc"),
    text = """Schreiben Sie einen regulären Ausdruck, der die Kilometerstände aus Autoanzeigen extrahieren kann.
             |Diese bestehen aus einer Zahl, einem 'T' für Tausend und der Angabe der Einheit (km).
             |Zwischen der Zahl und dem T und zwischen dem T und der Einheit können optional Leerzeichen vorhanden sein.
             |Alle Buchstaben können außerdem groß oder klein geschrieben sein.""".stripMargin.replace("\n", ""),
    difficulty = 3,
    content = RegexExerciseContent(
      maxPoints = 5,
      correctionType = RegexCorrectionType.EXTRACTION,
      matchTestData = Seq.empty,
      extractionTestData = Seq(
        RegexExtractionTestData(
          id = 1,
          base =
            "VW Golf V 1.4 Trendline, 3-türig, 55 kW, EZ 7/05, TÜV 2/20, 160 Tkm, VB 2.500.- EUR. Tel. 0151-1375575 (Ochsenfurt)"
        ),
        RegexExtractionTestData(
          id = 2,
          base =
            """Smart fort two, Mhd, EZ 2014, 39 TKm, schw./silber, 71 PS, TÜV neu, Navi, 8-f. Alu, Unfallfrei,
              |Klima, Benzin, Panoramadach, Cockpituhr, Drehzahlmesser, VB 6 490.- EUR. Tel. 0175/1119771""".stripMargin
              .replace("\n", " ")
        ),
        RegexExtractionTestData(
          id = 3,
          base = """Audi B4, 2.6 l, Bj. 92, gute Ausstattung, TÜV 03/20, 920 tkm, 500 EUR. Tel. 0971 61388"""
        ),
        RegexExtractionTestData(
          id = 4,
          base = """Volkswagen Passat, Baujahr 94, 1.8, 90 PS, Automatik, TÜV 7/2020, VB 500.-. Tel. 0177-6942776"""
            .replace("\n", "")
        ),
        RegexExtractionTestData(
          id = 5,
          base =
            "Porsche Boxster S Bj. 2000, 134tkm, Bestzustand, Reifen, HU neu, nauticblau/beige, FP 13 000,- EUR. Tel. 0178/8024956"
        ),
        RegexExtractionTestData(
          id = 6,
          base = "Smart ForFour, EZ 01/2018, 16 t km, HU 01/2021, E10 geeignet, 71 PS, Tel +49 (0)931 805-580"
        )
      ),
      sampleSolutions = Seq(
        SampleSolution(id = 1, sample = """\d+\s?[Tt]\s?[Kk][Mm]""")
      )
    )
  )

}
