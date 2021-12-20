package initialData.web.coll_1

import initialData.InitialData._
import initialData.web.WebInitialExerciseContainer
import initialData.{FileLoadConfig, InitialExercise}
import model.FilesSolution
import model.tools.web.WebExerciseContent
import model.tools.web.sitespec.{HtmlTask, SiteSpec, WebElementSpec}

object WebColl1Ex5 extends WebInitialExerciseContainer(1, 5) {

  private val html_tasks: Seq[HtmlTask] = Seq(
    HtmlTask(
      id = 1,
      text = """Erstellen Sie eine passende h1-Überschrift die 'Deutsche Nationalhymne' enthält.""",
      elementSpec = WebElementSpec(
        xpathQuery = "/html/body//h1",
        awaitedTagName = "h1",
        awaitedTextContent = Some("Deutsche Nationalhymne")
      )
    ),
    HtmlTask(
      id = 2,
      text = """Erstellen Sie das Grundelement für die Audiodatei und aktivieren Sie die Kontrollelement.
               |Falls der Browser keine Audiodateien unterstützt, soll der Text 'Ihr Browser unterstützt kein Audio!'
               |ausgegeben werden.""".stripMargin
        .replace("\n", " "),
      elementSpec = WebElementSpec(
        xpathQuery = "/html/body//audio",
        awaitedTagName = "audio",
        attributes = Map("controls" -> "true"),
        awaitedTextContent = Some("Ihr Browser unterstützt kein Audio!")
      )
    ),
    HtmlTask(
      id = 3,
      text = """Erstellen Sie das Element für die Quelldatei. Diese ist vom Typ 'audio/ogg' und befindet sich an der URL
               |'https=//upload.wikimedia.org/wikipedia/commons/c/cb/National_anthem_of_Germany_-_U.S._Army_1st_Armored_Division_Band.ogg'""".stripMargin
        .replace("\n", " "),
      elementSpec = WebElementSpec(
        xpathQuery = "/html/body//audio/source",
        awaitedTagName = "source",
        attributes = Map(
          "type" -> "audio/ogg",
          "src"  -> "https=//upload.wikimedia.org/wikipedia/commons/c/cb/National_anthem_of_Germany_-_U.S._Army_1st_Armored_Division_Band.ogg"
        )
      )
    )
  )

  val webColl1Ex5: InitialExercise[WebExerciseContent] = InitialExercise(
    title = "Audio in HTML 5",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    difficulty = 1,
    content = WebExerciseContent(
      SiteSpec("audio.html", html_tasks, jsTasks = Seq.empty),
      files = loadFilesFromFolder(
        exResPath,
        Seq(FileLoadConfig("audio.html", editable = true))
      ),
      sampleSolutions = Seq(
        FilesSolution(
          loadFilesFromFolder(
            exResPath / "sol_1",
            Seq(FileLoadConfig("audio.html"))
          )
        )
      )
    )
  )
}
