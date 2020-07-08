package model.tools.programming

import better.files.File
import model.ExerciseFile
import model.core.{DockerBind, DockerConnector}
import model.points.Points
import model.tools.AbstractCorrector

import scala.util.matching.Regex

trait ProgrammingAbstractCorrector extends AbstractCorrector {

  override type AbstractResult = ProgrammingAbstractResult

  override protected def buildInternalError(msg: String, maxPoints: Points): ProgrammingInternalErrorResult =
    ProgrammingInternalErrorResult(msg, maxPoints)

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
    bindToDirectory: String = DockerConnector.DefaultWorkingDir,
    isReadOnly: Boolean = true
  ): DockerBind = {
    val targetPath = writeToDirectory / exerciseFile.name

    createFileAndWrite(targetPath, exerciseFile.content)

    DockerBind(targetPath, s"$bindToDirectory/${exerciseFile.name}", isReadOnly)
  }

}
