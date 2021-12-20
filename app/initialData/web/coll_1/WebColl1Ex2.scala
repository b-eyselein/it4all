package initialData.web.coll_1

import initialData.FileLoadConfig
import initialData.InitialData._
import initialData.web.WebInitialExercise
import model.tools.web.WebExerciseContent
import model.tools.web.WebTool.WebExercise
import model.tools.web.sitespec.{HtmlTask, SiteSpec, WebElementSpec}
import model.{Exercise, FilesSolution}

object WebColl1Ex2 extends WebInitialExercise(1, 2) {

  private val html_tasks: Seq[HtmlTask] = Seq(
    HtmlTask(
      id = 1,
      text = "Erstellen Sie das Grundtag für die Tabelle.",
      elementSpec = WebElementSpec(
        xpathQuery = "/html/body//table",
        awaitedTagName = "table"
      )
    ),
    HtmlTask(
      id = 2,
      text = "Erstellen Sie die erste Zeile für die Überschriften.",
      elementSpec = WebElementSpec(
        xpathQuery = "/html/body//table//tr[1]",
        awaitedTagName = "tr"
      )
    ),
    HtmlTask(
      id = 3,
      text = """Erstellen Sie die erste Zelle für die Überschrift. Diese soll als Inhalt 'Jahr' haben.""",
      elementSpec = WebElementSpec(
        xpathQuery = "html/body//table//tr[1]/th[1]",
        awaitedTagName = "th",
        awaitedTextContent = Some("Jahr")
      )
    ),
    HtmlTask(
      id = 4,
      text = """Erstellen Sie die zweite Zelle für die Überschrift. Diese soll als Inhalt 'Produktion' haben.""",
      elementSpec = WebElementSpec(
        xpathQuery = "html/body//table//tr[1]/th[2]",
        awaitedTagName = "th",
        awaitedTextContent = Some("Produktion")
      )
    ),
    HtmlTask(
      id = 5,
      text = "Erstellen Sie die zweite Zeile in der Tabelle",
      elementSpec = WebElementSpec(
        xpathQuery = "/html/body//table//tr[2]",
        awaitedTagName = "tr"
      )
    ),
    HtmlTask(
      id = 6,
      text = """Erstellen Sie die erste Zelle in der zweiten Zeile. Diese soll als Inhalt '1900' haben.""",
      elementSpec = WebElementSpec(
        xpathQuery = "html/body//table//tr[2]/td[1]",
        awaitedTagName = "td",
        awaitedTextContent = Some("1900")
      )
    ),
    HtmlTask(
      id = 7,
      text = """Erstellen Sie die zweite Zelle in der zweiten Zeile. Diese soll als Inhalt '9504' haben.""",
      elementSpec = WebElementSpec(
        xpathQuery = "html/body//table//tr[2]/td[2]",
        awaitedTagName = "td",
        awaitedTextContent = Some("9504")
      )
    ),
    HtmlTask(
      id = 8,
      text = "Erstellen Sie die dritte Zeile in der Tabelle",
      elementSpec = WebElementSpec(
        xpathQuery = "/html/body//table//tr[3]",
        awaitedTagName = "tr"
      )
    ),
    HtmlTask(
      id = 9,
      text = """Erstellen Sie die erste Zelle in der dritten Zeile. Diese soll als Inhalt '1950' haben.""",
      elementSpec = WebElementSpec(
        xpathQuery = "html/body//table//tr[3]/td[1]",
        awaitedTagName = "td",
        awaitedTextContent = Some("1950")
      )
    ),
    HtmlTask(
      id = 10,
      text = """Erstellen Sie die zweite Zelle in der dritten Zeile. Diese soll als Inhalt '10577426' haben.""",
      elementSpec = WebElementSpec(
        xpathQuery = "html/body//table//tr[3]/td[2]",
        awaitedTagName = "td",
        awaitedTextContent = Some("10577426")
      )
    ),
    HtmlTask(
      id = 11,
      text = "Erstellen Sie die vierte Zeile in der Tabelle.",
      elementSpec = WebElementSpec(
        xpathQuery = "/html/body//table//tr[4]",
        awaitedTagName = "td"
      )
    ),
    HtmlTask(
      id = 12,
      text = """Erstellen Sie die erste Zelle in der vierten Zeile. Diese soll als Inhalt '2000' haben.""",
      elementSpec = WebElementSpec(
        xpathQuery = "html/body//table//tr[4]/td[1]",
        awaitedTagName = "td",
        awaitedTextContent = Some("2000")
      )
    ),
    HtmlTask(
      id = 13,
      text = """Erstellen Sie die zweite Zelle in der vierten Zeile. Diese soll als Inhalt '58374162' haben""",
      elementSpec = WebElementSpec(
        xpathQuery = "html/body//table//tr[4]/td[2]",
        awaitedTagName = "td",
        awaitedTextContent = Some("58374162")
      )
    ),
    HtmlTask(
      id = 14,
      text = """Binden Sie die vorgegebene CSS-Datei 'productionStyle.css' ein.
               |Die entsprechende Datei ist unter der URL 'productionStyle.css' zu finden.
               |Setzen Sie auch den entsprechenden Wert für das Attribut 'rel'.""".stripMargin
        .replace("\n", " "),
      elementSpec = WebElementSpec(
        xpathQuery = "/html/head//link",
        awaitedTagName = "link",
        attributes = Map("href" -> "productionStyle.css", "rel" -> "stylesheet")
      )
    )
  )

  private val sampleSolutionFiles = loadFilesFromFolder(
    exResPath / "sol_1",
    Seq(
      FileLoadConfig("production.html")
    )
  )

  val webColl1Ex2: WebExercise = Exercise(
    exerciseId,
    collectionId,
    toolId = "web",
    title = "Tabellen in Html",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    difficulty = 2,
    content = WebExerciseContent(
      siteSpec = SiteSpec("production.html", html_tasks, jsTasks = Seq.empty),
      files = loadFilesFromFolder(
        exResPath,
        Seq(
          FileLoadConfig("production.html", editable = true),
          FileLoadConfig("productionStyle.css")
        )
      ),
      sampleSolutions = Seq(FilesSolution(sampleSolutionFiles))
    )
  )

}
