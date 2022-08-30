package initialData.flask

import initialData.InitialData.loadTextFromFile
import initialData.{FileLoadConfig, InitialExercise}
import model._
import model.tools.flask.{FlaskExerciseContent, FlaskSingleTestConfig, FlaskTestsConfig}

import scala.language.implicitConversions

object FlaskColl01Ex01 extends FlaskInitialExerciseContainer(1, 1) {

  private val testsConfig = FlaskTestsConfig(
    testFileName = "test_login",
    testClassName = "MyTest",
    tests = Seq(
      FlaskSingleTestConfig(
        id = 1,
        description = """Ein Nutzer, der nicht eingeloggt ist und die URL <code>/index.html</code> aufruft, soll auf die URL
                        |<code>/login.html</code> weitergeleitet werden.""".stripMargin,
        maxPoints = 1,
        testName = "test_redirect_to_login"
      ),
      FlaskSingleTestConfig(
        id = 2,
        description = """Bei einem Aufruf der URL <code>register</code> mit der Methode <code>GET</code> soll das Formular im Template
                        |<code>register.html</code> mit den entsprechenden Inhalten angezeigt werden.""".stripMargin,
        maxPoints = 3,
        testName = "test_register_form"
      ),
      FlaskSingleTestConfig(
        id = 3,
        description = """Die Funktionalität des Formulars im Template <code>register.html</code> wird im Zusammenspiel mit der URL
                        |<code>/register</code> gegeben sein.""".stripMargin,
        maxPoints = 3,
        testName = "test_register_functionality",
        dependencies = Some(Seq("test_register_form"))
      ),
      FlaskSingleTestConfig(
        id = 4,
        description = """Bei einem Aufruf der URL <code>/login</code> mit der Methode <code>GET</code> soll das Formular im Template
                        |<code>login.html</code> mit den entsprechenden Inhalten angezeigt werden.""".stripMargin,
        maxPoints = 2,
        testName = "test_login_form"
      ),
      FlaskSingleTestConfig(
        id = 5,
        description = """Die Funktionalität des Formulars im Template <code>login.html</code> wird im Zusammenspiel mit der URL
                        |<code>/login</code> gegeben sein.""".stripMargin,
        maxPoints = 2,
        testName = "test_login_functionality",
        dependencies = Some(Seq("test_login_form"))
      ),
      FlaskSingleTestConfig(
        id = 6,
        description = "Bei einem eingeloggten Nutzer soll das Tempalte <code>index.html</code> angezeigt werden.",
        maxPoints = 5,
        testName = "test_index",
        dependencies = Some(Seq("test_login_functionality"))
      )
    )
  )

  val flaskColl01Ex01: InitialExercise[FlaskExerciseContent] = InitialExercise(
    title = "Testaufgabe Login",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    difficulty = 1,
    content = FlaskExerciseContent(
      files = loadFilesFromFolder(
        declarationPath,
        Seq(
          FileLoadConfig("server.py", editable = true),
          FileLoadConfig("templates/base.html", editable = true),
          FileLoadConfig("templates/index.html", editable = true),
          FileLoadConfig("templates/login.html", editable = true),
          FileLoadConfig("templates/register.html", editable = true)
        )
      ),
      testFiles = loadFilesFromFolder(
        exResPath,
        Seq(
          FileLoadConfig("test_login.py")
        )
      ),
      testsConfig,
      sampleSolutions = Seq(
        FilesSolution(
          loadFilesFromFolder(
            solPath,
            Seq(
              FileLoadConfig("server.py"),
              FileLoadConfig("templates/base.html"),
              FileLoadConfig("templates/index.html"),
              FileLoadConfig("templates/login.html"),
              FileLoadConfig("templates/register.html")
            )
          )
        )
      )
    )
  )

}
