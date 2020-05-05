package initialData.web.coll_1

import de.uniwue.webtester.sitespec.{HtmlTask, SiteSpec}
import initialData.InitialData.{ex_resources_path, load_text_from_file}
import model.tools.web.WebTool.WebExercise
import model.tools.web.{WebExerciseContent, WebSolution}
import model.{Exercise, ExerciseFile, SampleSolution}

object WebColl1Ex3 {

  private val ex_res_folder = ex_resources_path("web", 1, 3)

  private val html_tasks = Seq(
    HtmlTask(
      id = 1,
      text = """Erstellen Sie eine passende h1 - Überschrift, die 'Ford Mustang' enthält.""",
      xpathQuery = "/html/body//h1",
      awaitedTagName = "h1",
      awaitedTextContent = Some("Ford Mustang")
    ),
    HtmlTask(
      id = 2,
      text = """Erstellen Sie den Link auf der Seite, der auf Wikipedia verweist.
               |Geben Sie als Ziel die URL 'https=//de.wikipedia.org/wiki/Ford_Mustang' an.""".stripMargin
        .replace("\n", " "),
      xpathQuery = "/html/body//a",
      awaitedTagName = "a",
      attributes = Map("href" -> "https=//de.wikipedia.org/wiki/Ford_Mustang")
    ),
    HtmlTask(
      id = 3,
      text = """Erstellen Sie im Link das Bild des Ford Mustang.
               |Geben Sie als Quelle des Bildes die URL
               |'https=//upload.wikimedia.org/wikipedia/commons/2/2d/1964_12_Ford_Mustang.jpg' und als alternative
               |Beschreibung 'Ford Mustang' an.
               |Geben Sie außerdem eine Breite von 250 und eine Höhe von 188 an, um das Bild zu skalieren.""".stripMargin
        .replace("\n", " "),
      xpathQuery = "/html/body//a//img",
      awaitedTagName = "img",
      attributes = Map(
        "src"    -> "https=//upload.wikimedia.org/wikipedia/commons/2/2d/1964_12_Ford_Mustang.jpg",
        "alt"    -> "Ford Mustang",
        "width"  -> "250",
        "height" -> "188"
      )
    ),
    HtmlTask(
      id = 4,
      text = """Binden Sie die vorgegebene CSS - Datei 'mustangStyle.css' ein.
               |Die entsprechende Datei ist unter der URL 'mustangStyle.css' zu finden.
               |Setzen Sie auch den entsprechenden Wert für das Attribut 'rel'.""".stripMargin
        .replace("\n", " "),
      xpathQuery = "/html/head//link",
      awaitedTagName = "link",
      attributes = Map("rel" -> "stylesheet", "href" -> "mustangStyle.css")
    )
  )

  private val sampleSolution: SampleSolution[WebSolution] = SampleSolution(
    id = 1,
    sample = WebSolution(
      files = Seq(
        ExerciseFile(
          name = "mustang.html",
          fileType = "htmlmixed",
          editable = false,
          content = load_text_from_file(ex_res_folder / "sol_1" / "mustang.html")
        )
      )
    )
  )

  val webColl1Ex3: WebExercise = Exercise(
    exerciseId = 3,
    collectionId = 1,
    toolId = "web",
    title = "Hyperlinks und Bilder in HTML",
    authors = Seq("bje40dc"),
    text = load_text_from_file(ex_res_folder / "text.html"),
    difficulty = 2,
    topicAbbreviations = Seq.empty,
    content = WebExerciseContent(
      files = Seq(
        ExerciseFile(
          name = "mustang.html",
          fileType = "htmlmixed",
          editable = true,
          content = load_text_from_file(ex_res_folder / "mustang.html")
        ),
        ExerciseFile(
          name = "mustangStyle.css",
          fileType = "css",
          editable = false,
          content = load_text_from_file(ex_res_folder / "mustangStyle.css")
        )
      ),
      siteSpec = SiteSpec(
        fileName = "mustang.html",
        htmlTasks = html_tasks,
        jsTasks = Seq.empty
      ),
      sampleSolutions = Seq(sampleSolution)
    )
  )

}