package initialData.web.coll_1

import better.files.File
import de.uniwue.webtester.sitespec.{HtmlTask, SiteSpec}
import initialData.InitialData.{ex_resources_path, load_text_from_file}
import model.tools.web.WebTool.WebExercise
import model.tools.web.{WebExerciseContent, WebSolution}
import model.{Exercise, ExerciseFile, SampleSolution}

object WebColl1Ex2 {

  private val ex_res_folder: File = ex_resources_path("web", 1, 2)

  private val html_tasks: Seq[HtmlTask] = Seq(
    HtmlTask(
      id = 1,
      text = "Erstellen Sie das Grundtag für die Tabelle.",
      xpathQuery = "/html/body//table",
      awaitedTagName = "table"
    ),
    HtmlTask(
      id = 2,
      text = "Erstellen Sie die erste Zeile für die Überschriften.",
      xpathQuery = "/html/body//table//tr[1]",
      awaitedTagName = "tr"
    ),
    HtmlTask(
      id = 3,
      text = """Erstellen Sie die erste Zelle für die Überschrift. Diese soll als Inhalt 'Jahr' haben.""",
      xpathQuery = "html/body//table//tr[1]/th[1]",
      awaitedTagName = "th",
      awaitedTextContent = Some("Jahr")
    ),
    HtmlTask(
      id = 4,
      text = """Erstellen Sie die zweite Zelle für die Überschrift. Diese soll als Inhalt 'Produktion' haben.""",
      xpathQuery = "html/body//table//tr[1]/th[2]",
      awaitedTagName = "th",
      awaitedTextContent = Some("Produktion")
    ),
    HtmlTask(
      id = 5,
      text = "Erstellen Sie die zweite Zeile in der Tabelle",
      xpathQuery = "/html/body//table//tr[2]",
      awaitedTagName = "tr"
    ),
    HtmlTask(
      id = 6,
      text = """Erstellen Sie die erste Zelle in der zweiten Zeile. Diese soll als Inhalt '1900' haben.""",
      xpathQuery = "html/body//table//tr[2]/td[1]",
      awaitedTagName = "td",
      awaitedTextContent = Some("1900")
    ),
    HtmlTask(
      id = 7,
      text = """Erstellen Sie die zweite Zelle in der zweiten Zeile. Diese soll als Inhalt '9504' haben.""",
      xpathQuery = "html/body//table//tr[2]/td[2]",
      awaitedTagName = "td",
      awaitedTextContent = Some("9504")
    ),
    HtmlTask(
      id = 8,
      text = "Erstellen Sie die dritte Zeile in der Tabelle",
      xpathQuery = "/html/body//table//tr[3]",
      awaitedTagName = "tr"
    ),
    HtmlTask(
      id = 9,
      text = """Erstellen Sie die erste Zelle in der dritten Zeile. Diese soll als Inhalt '1950' haben.""",
      xpathQuery = "html/body//table//tr[3]/td[1]",
      awaitedTagName = "td",
      awaitedTextContent = Some("1950")
    ),
    HtmlTask(
      id = 10,
      text = """Erstellen Sie die zweite Zelle in der dritten Zeile. Diese soll als Inhalt '10577426' haben.""",
      xpathQuery = "html/body//table//tr[3]/td[2]",
      awaitedTagName = "td",
      awaitedTextContent = Some("10577426")
    ),
    HtmlTask(
      id = 11,
      text = "Erstellen Sie die vierte Zeile in der Tabelle.",
      xpathQuery = "/html/body//table//tr[4]",
      awaitedTagName = "td"
    ),
    HtmlTask(
      id = 12,
      text = """Erstellen Sie die erste Zelle in der vierten Zeile. Diese soll als Inhalt '2000' haben.""",
      xpathQuery = "html/body//table//tr[4]/td[1]",
      awaitedTagName = "td",
      awaitedTextContent = Some("2000")
    ),
    HtmlTask(
      id = 13,
      text = """Erstellen Sie die zweite Zelle in der vierten Zeile. Diese soll als Inhalt '58374162' haben""",
      xpathQuery = "html/body//table//tr[4]/td[2]",
      awaitedTagName = "td",
      awaitedTextContent = Some("58374162")
    ),
    HtmlTask(
      id = 14,
      text = """Binden Sie die vorgegebene CSS-Datei 'productionStyle.css' ein.
               |Die entsprechende Datei ist unter der URL 'productionStyle.css' zu finden.
               |Setzen Sie auch den entsprechenden Wert für das Attribut 'rel'.""".stripMargin
        .replace("\n", " "),
      xpathQuery = "/html/head//link",
      awaitedTagName = "link",
      attributes = Map("href" -> "productionStyle.css", "rel" -> "stylesheet")
    )
  )

  private val sampleSolution: SampleSolution[WebSolution] = SampleSolution(
    id = 1,
    sample = WebSolution(
      files = Seq(
        ExerciseFile(
          name = "production.html",
          fileType = "htmlmixed",
          editable = false,
          content = load_text_from_file(ex_res_folder / "sol_1" / "production.html")
        )
      )
    )
  )

  val webColl1Ex2: WebExercise = Exercise(
    exerciseId = 2,
    collectionId = 1,
    toolId = "web",
    title = "Tabellen in Html",
    authors = Seq("bje40dc"),
    text = load_text_from_file(ex_res_folder / "text.html"),
    difficulty = 2,
    topicAbbreviations = Seq.empty,
    content = WebExerciseContent(
      files = Seq(
        ExerciseFile(
          name = "production.html",
          fileType = "htmlmixed",
          editable = true,
          content = load_text_from_file(ex_res_folder / "production.html")
        ),
        ExerciseFile(
          name = "productionStyle.css",
          fileType = "css",
          editable = false,
          content = load_text_from_file(ex_res_folder / "productionStyle.css")
        )
      ),
      siteSpec = SiteSpec(
        fileName = "production.html",
        htmlTasks = html_tasks,
        jsTasks = Seq.empty
      ),
      sampleSolutions = Seq(sampleSolution)
    )
  )

}
