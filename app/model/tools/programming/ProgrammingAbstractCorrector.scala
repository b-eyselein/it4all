package model.tools.programming

import better.files.File
import model.core.{DockerBind, DockerConnector}
import model.points.Points
import model.tools.{AbstractCorrector, ExerciseFile}

import scala.util.matching.Regex

trait ProgrammingAbstractCorrector extends AbstractCorrector {

  override type AbstractResult = ProgrammingAbstractResult

  override protected def buildInternalError(
    msg: String,
    solutionSaved: Boolean,
    maxPoints: Points
  ): ProgrammingInternalErrorResult = ProgrammingInternalErrorResult(msg, solutionSaved, maxPoints)

  val resultFileName = "result.json"

  protected val testDataFileName = "test_data.json"
  protected val testMainFileName = "test_main.py"

  protected val implFileRegex: Regex = """.*_\d*\.py""".r

  protected def createFileAndWrite(file: File, content: String): Unit =
    file
      .createFileIfNotExists(createParents = true)
      .write(content)

  def writeExerciseFileAndMount(
    exerciseFile: ExerciseFile,
    writeToDirectory: File,
    bindToDirectory: File = DockerConnector.DefaultWorkingDir,
    isReadOnly: Boolean = true
  ): DockerBind = {
    val targetPath = writeToDirectory / exerciseFile.name

    createFileAndWrite(targetPath, exerciseFile.content)

    DockerBind(targetPath, bindToDirectory / exerciseFile.name, isReadOnly)
  }

}
