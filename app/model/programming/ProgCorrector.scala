package model.programming

import better.files.File._
import better.files._
import model.User
import model.docker._
import modules.DockerPullsStartTask
import play.api.libs.json._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Try}

object ProgCorrector {

  private val resultFileName  : String = "result.json"
  private val testDataFileName: String = "test_data.json"

  private def buildSolutionFileName(fileEnding: String): String = "solution." + fileEnding

  private def buildTestMainFileName(fileEnding: String): String = "test_main." + fileEnding

  def correct(user: User, progSolution: ProgSolution, exercise: ProgExercise, toolMain: ProgToolMain)(implicit ec: ExecutionContext): Future[Try[ProgCompleteResult]] = {

    val (implementation: String, completeTestData: Seq[TestData]) = progSolution match {
      case ProgTestDataSolution(td, _)        => (exercise.sampleSolutions.headOption.map(_.sample).getOrElse(???), td)
      case ProgStringSolution(solution, _, _) => (solution, exercise.sampleTestData)
    }

    val exerciseResourcesFolder: File = toolMain.exerciseResourcesFolder / (exercise.id + "-" + exercise.folderIdentifier)

    val solutionTargetDir: File = toolMain.solutionDirForExercise(user.username, exercise.id)

    val testDataFileContent: JsValue = exercise.buildTestDataFileContent(completeTestData, progSolution.extendedUnitTests)

    // Write/Create files
    val solFileName = buildSolutionFileName(progSolution.language.fileEnding)
    val solutionFile = solutionTargetDir / solFileName
    solutionFile.createFileIfNotExists(createParents = true).write(implementation)

    val testDataFile = solutionTargetDir / testDataFileName
    testDataFile.createFileIfNotExists(createParents = true).write(Json.prettyPrint(testDataFileContent))

    val resultFile = solutionTargetDir / resultFileName
    resultFile.createIfNotExists(createParents = true)


    /*
     * FIXME: what if image does not exist?
     *  --> val imageExists: Boolean = DockerConnector.imageExists(DockerPullsStartTask.pythonProgTesterImage)
     */
    val containerResult: Future[RunContainerResult] = DockerConnector.runContainer(
      imageName = DockerPullsStartTask.pythonProgTesterImage,
      maybeDockerBinds = Some(mountProgrammingFiles(exerciseResourcesFolder, progSolution.extendedUnitTests, solutionTargetDir, solFileName, fileEnding = "py")),
      maybeCmd = if (progSolution.extendedUnitTests) Some(Seq("--extended")) else None
    )

    val futureResults = containerResult map {

      case RunContainerSuccess => ResultsFileJsonFormat.readResultFile(solutionTargetDir / resultFileName, completeTestData)

      case failure: RunContainerFailure => Failure(failure)

    }

    futureResults map { resultTries: Try[Seq[ExecutionResult]] =>
      resultTries map (results => ProgCompleteResult(implementation, results))
    }
  }

  private def mountProgrammingFiles(exResFolder: File, extendedUnitTests: Boolean, solTargetDir: File, solFileName: String, fileEnding: String): Seq[DockerBind] = {
    val testMainFileName = buildTestMainFileName(fileEnding)

    val testMainFileMount = if (extendedUnitTests) None
    else Some(
      DockerBind(exResFolder / testMainFileName, DockerConnector.DefaultWorkingDir / testMainFileName, isReadOnly = true),
    )

    Seq(
      DockerBind(solTargetDir / solFileName, DockerConnector.DefaultWorkingDir / solFileName, isReadOnly = true),
      DockerBind(solTargetDir / testDataFileName, DockerConnector.DefaultWorkingDir / testDataFileName, isReadOnly = true),
      DockerBind(solTargetDir / resultFileName, DockerConnector.DefaultWorkingDir / resultFileName)
    ) ++ testMainFileMount
  }
}
