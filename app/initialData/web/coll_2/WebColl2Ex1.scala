package initialData.web.coll_2

import initialData.FileLoadConfig
import initialData.InitialData._
import initialData.web.WebInitialExercise
import model.tools.web.WebExerciseContent
import model.tools.web.WebTool.WebExercise
import model.tools.web.sitespec._
import model.{Exercise, FilesSolution}

object WebColl2Ex1 extends WebInitialExercise(2, 1) {

  private val html_tasks: Seq[HtmlTask] = Seq(
    HtmlTask(
      id = 1,
      text = """Erstellen Sie einen Button mit dem Text 'Klick mich!'.
               |Beim Klick die Funktion 'increment()' aufgerufen werden.""".stripMargin.replace("\n", " "),
      elementSpec = WebElementSpec(
        xpathQuery = "/html/body//button",
        awaitedTagName = "button",
        attributes = Map("onclick" -> "increment()"),
        awaitedTextContent = Some("Klick mich!")
      )
    ),
    HtmlTask(
      id = 2,
      text = """Erstellen Sie einen Span mit der ID 'theSpan', in dem die Anzahl der Klicks angezeigt werden.
               |Zu Anfang soll dieser eine 0 anzeigen.""".stripMargin.replace("\n", " "),
      elementSpec = WebElementSpec(
        xpathQuery = """/html/body//span[@id='theSpan']""",
        awaitedTagName = "span",
        awaitedTextContent = Some("0")
      )
    ),
    HtmlTask(
      id = 3,
      text = """Binden Sie die Javascript - Datei 'clickCounter.js' ein.""",
      elementSpec = WebElementSpec(
        xpathQuery = "/html/head//script",
        awaitedTagName = "script",
        attributes = Map("src" -> "clickCounter.js")
      )
    )
  )

  private val js_tasks: Seq[JsTask] = (1 to 5).map { i =>
    JsTask(
      id = i,
      text = s"Test $i",
      preConditions = Seq(
        WebElementSpec(
          xpathQuery = """/html/body//span[@id='theSpan']""",
          awaitedTagName = "span",
          awaitedTextContent = Some(s"${i - 1}")
        )
      ),
      action = JsAction(
        xpathQuery = "/html/body//button",
        actionType = JsActionType.Click,
        keysToSend = None
      ),
      postConditions = Seq(
        WebElementSpec(
          xpathQuery = """/html/body//span[@id='theSpan']""",
          awaitedTagName = "span",
          awaitedTextContent = Some(s"$i")
        )
      )
    )
  }

  private val sampleSolutionFiles = loadFilesFromFolder(
    exResPath / "sol_1",
    Seq(
      FileLoadConfig("clickCounter.html"),
      FileLoadConfig("clickCounter.js")
    )
  )

  val webColl2Ex1: WebExercise = Exercise(
    exerciseId,
    collectionId,
    toolId = "web",
    title = "Klickzähler",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    difficulty = 2,
    content = WebExerciseContent(
      SiteSpec("clickCounter.html", html_tasks, js_tasks),
      loadFilesFromFolder(
        exResPath,
        Seq(
          FileLoadConfig("clickCounter.html", editable = true),
          FileLoadConfig("clickCounter.js", editable = true)
        )
      ),
      Seq(FilesSolution(sampleSolutionFiles)),
      htmlText = Some("Erstellen Sie zunächst den Rumpf der Seite in HTML."),
      jsText = Some(
        """Implementieren Sie nun die Funktion <code>increment()</code> die aufgerufen wird wenn auf den Knopf
          |gedrückt wird.
          |Sie soll den Inhalt (innerHTML) des Elementes mit der id 'theSpan' auslesen und um 1 erhöhen.
          |Sie können die Funktion <code>parseInt(str)</code> verwenden um einen String in eine Ganzzahl
          |umzuwandeln.""".stripMargin.replace("\n", " ")
      )
    )
  )

}
