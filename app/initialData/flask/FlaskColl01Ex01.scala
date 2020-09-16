package initialData.flask

import initialData.FileLoadConfig
import model.tools.flask.FlaskTool.FlaskExercise
import model.tools.flask.{FlaskExerciseContent, FlaskSingleTestConfig, FlaskTestsConfig}
import model.{Exercise, ExerciseFile, FilesSolution, SampleSolution}

object FlaskColl01Ex01 extends FlaskInitialExercise(1, 1) {

  private val sampleSolutionFiles = loadFilesFromFolder(
    solPath,
    Seq(
      FileLoadConfig("server.py", "python"),
      FileLoadConfig("templates/base.html", "jinja2"),
      FileLoadConfig("templates/index.html", "jinja2"),
      FileLoadConfig("templates/login.html", "jinja2"),
      FileLoadConfig("templates/register.html", "jinja2")
    )
  )

  private val testFiles = loadFilesFromFolder(exResPath, Seq(FileLoadConfig("test_login.py", "python")))

  private val files: Seq[ExerciseFile] = loadFilesFromFolder(
    declarationPath,
    Seq(
      FileLoadConfig("server.py", "python", editable = true),
      FileLoadConfig("templates/base.html", "jinja2", editable = true),
      FileLoadConfig("templates/index.html", "jinja2", editable = true),
      FileLoadConfig("templates/login.html", "jinja2", editable = true),
      FileLoadConfig("templates/register.html", "jinja2", editable = true)
    )
  )

  private val testsConfig = FlaskTestsConfig(
    testFileName = "test_login",
    testClassName = "MyTest",
    tests = Seq(
      FlaskSingleTestConfig(maxPoints = 1, testName = "test_redirect_to_login"),
      FlaskSingleTestConfig(maxPoints = 3, testName = "test_register_form"),
      FlaskSingleTestConfig(
        maxPoints = 3,
        testName = "test_register_functionality",
        dependencies = Some(Seq("test_register_form"))
      ),
      FlaskSingleTestConfig(maxPoints = 2, testName = "test_login_form"),
      FlaskSingleTestConfig(
        maxPoints = 2,
        testName = "test_login_functionality",
        dependencies = Some(Seq("test_login_form"))
      ),
      FlaskSingleTestConfig(
        maxPoints = 5,
        testName = "test_index",
        dependencies = Some(Seq("test_login_functionality"))
      )
    )
  )

  private val sampleSolutions = Seq(SampleSolution(1, FilesSolution(sampleSolutionFiles)))

  val flaskColl01Ex01: FlaskExercise = Exercise(
    exerciseId,
    collectionId,
    toolId,
    title = "Testaufgabe",
    authors = Seq("bje40dc"),
    text = "TODO!",
    difficulty = 1,
    content = FlaskExerciseContent(files, testFiles, testsConfig, sampleSolutions)
  )

}
