package initialData.web.coll_1

import initialData.FileLoadConfig
import initialData.InitialData._
import initialData.web.WebInitialExercise
import model.tools.web.WebExerciseContent
import model.tools.web.WebTool.WebExercise
import model.tools.web.sitespec.{HtmlTask, SiteSpec}
import model.{Exercise, FilesSolution}

object WebColl1Ex5 extends WebInitialExercise(1, 5) {

  private val sampleSolutionFiles = loadFilesFromFolder(
    exResPath / "sol_1",
    Seq(FileLoadConfig("audio.html", htmlFileType))
  )

  private val html_tasks: Seq[HtmlTask] = Seq(
    HtmlTask(
      id = 1,
      text = """Erstellen Sie eine passende h1-Überschrift die 'Deutsche Nationalhymne' enthält.""",
      xpathQuery = "/html/body//h1",
      awaitedTagName = "h1",
      awaitedTextContent = Some("Deutsche Nationalhymne")
    ),
    HtmlTask(
      id = 2,
      text = """Erstellen Sie das Grundelement für die Audiodatei und aktivieren Sie die Kontrollelement.
               |Falls der Browser keine Audiodateien unterstützt, soll der Text 'Ihr Browser unterstützt kein Audio!'
               |ausgegeben werden.""".stripMargin
        .replace("\n", " "),
      xpathQuery = "/html/body//audio",
      awaitedTagName = "audio",
      attributes = Map("controls" -> "true"),
      awaitedTextContent = Some("Ihr Browser unterstützt kein Audio!")
    ),
    HtmlTask(
      id = 3,
      text =
        """Erstellen Sie das Element für die Quelldatei. Diese ist vom Typ 'audio/ogg' und befindet sich an der URL
          |'https=//upload.wikimedia.org/wikipedia/commons/c/cb/National_anthem_of_Germany_-_U.S._Army_1st_Armored_Division_Band.ogg'""".stripMargin
          .replace("\n", " "),
      xpathQuery = "/html/body//audio/source",
      awaitedTagName = "source",
      attributes = Map(
        "type" -> "audio/ogg",
        "src"  -> "https=//upload.wikimedia.org/wikipedia/commons/c/cb/National_anthem_of_Germany_-_U.S._Army_1st_Armored_Division_Band.ogg"
      )
    )
  )

  val webColl1Ex5: WebExercise = Exercise(
    exerciseId,
    collectionId,
    toolId,
    title = "Audio in HTML 5",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    difficulty = 1,
    content = WebExerciseContent(
      SiteSpec("audio.html", html_tasks, jsTasks = Seq.empty),
      files = loadFilesFromFolder(
        exResPath,
        Seq(FileLoadConfig("audio.html", htmlFileType, editable = true))
      ),
      Seq(FilesSolution(sampleSolutionFiles))
    )
  )
}
