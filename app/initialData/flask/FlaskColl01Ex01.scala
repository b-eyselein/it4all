package initialData.flask

import initialData.InitialData.{exerciseResourcesPath, loadTextFromFile}
import model.points._
import model.tools.flask.FlaskExerciseContent
import model.tools.flask.FlaskTool.FlaskExercise
import model.{Exercise, ExerciseFile, FilesSolution, SampleSolution}

object FlaskColl01Ex01 {

  private val exResPath = exerciseResourcesPath("flask", 1, 1)

  private val solPath = exResPath / "solution"

  private val sampleSolutions: Seq[SampleSolution[FilesSolution]] = Seq(
    SampleSolution(
      1,
      FilesSolution(
        files = Seq(
          ExerciseFile("server.py", "python", editable = false, loadTextFromFile(solPath / "server.py"))
        )
      )
    )
  )

  private val files: Seq[ExerciseFile] = Seq(
    ExerciseFile("server.py", "python", editable = true, loadTextFromFile(exResPath / "server.py")),
    ExerciseFile(
      "templates/base.html",
      "htmlmixed",
      editable = true,
      loadTextFromFile(exResPath / "templates/base.html")
    ),
    ExerciseFile(
      "templates/index.html",
      "htmlmixed",
      editable = true,
      loadTextFromFile(exResPath / "templates/index.html")
    ),
    ExerciseFile(
      "templates/login.html",
      "htmlmixed",
      editable = true,
      loadTextFromFile(exResPath / "templates/login.html")
    ),
    ExerciseFile(
      "templates/register.html",
      "htmlmixed",
      editable = true,
      loadTextFromFile(exResPath / "templates/register.html")
    )
  )

  val flaskColl01Ex01: FlaskExercise = Exercise(
    exerciseId = 1,
    collectionId = 1,
    toolId = "flask",
    title = "Testaufgabe",
    authors = Seq("bje40dc"),
    text = "TODO!",
    difficulty = 1,
    content = FlaskExerciseContent(
      files,
      maxPoints = 10,
      sampleSolutions
    )
  )

}
