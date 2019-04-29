package model.tools.programming

import better.files.File._
import better.files._
import model.User
import model.docker._
import modules.DockerPullsStartTask
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Try}

object ProgCorrector {

  private val resultFileName  : String = "result.json"
  private val testDataFileName: String = "test_data.json"

  private def buildSolutionFileName(fileEnding: String): String = s"solution.${fileEnding}"

  private def buildTestMainFileName(fileEnding: String): String = s"test_main.${fileEnding}"

  def correct(user: User, progSolution: ProgSolution, collection: ProgCollection, exercise: ProgExercise, part: ProgExPart,
              toolMain: ProgToolMain)(implicit ec: ExecutionContext): Future[Try[ProgCompleteResult]] = {

    val completeTestData: Seq[ProgTestData] = part match {
      case ProgExParts.TestCreation => progSolution.testData
      case _                        => exercise.sampleTestData
    }

    val implementation: String = part match {
      case ProgExParts.TestCreation => exercise.sampleSolutions.headOption.map(_.sample.implementation).getOrElse(???)
      case _                        => progSolution.implementation
    }

    val solutionTargetDir: File = toolMain.solutionDirForExercise(user.username, collection.id, exercise.id)

    // Write/Create files
    val solFileName = buildSolutionFileName(progSolution.language.fileEnding)
    val solutionFile = solutionTargetDir / solFileName
    solutionFile.createFileIfNotExists(createParents = true).write(implementation)

    val testDataFile = solutionTargetDir / testDataFileName
    val testDataFileContent: JsValue = exercise.buildTestDataFileContent(completeTestData /*, progSolution.extendedUnitTests*/)
    testDataFile.createFileIfNotExists(createParents = true).write(Json.prettyPrint(testDataFileContent))

    val resultFile = solutionTargetDir / resultFileName
    resultFile.createIfNotExists(createParents = true)

    val exerciseResourcesFolder: File = toolMain.exerciseResourcesFolder / (exercise.id + "-" + exercise.functionName)

    /*
     * FIXME: what if image does not exist?
     *  --> val imageExists: Boolean = DockerConnector.imageExists(DockerPullsStartTask.pythonProgTesterImage)
     */
    val containerResult: Future[RunContainerResult] = DockerConnector.runContainer(
      imageName = DockerPullsStartTask.pythonProgTesterImage,
      maybeDockerBinds = Some(mountProgrammingFiles(exerciseResourcesFolder, false /*progSolution.extendedUnitTests*/ , solutionTargetDir, solFileName, fileEnding = "py")),
      maybeCmd = if (false /*progSolution.extendedUnitTests*/ ) Some(Seq("--extended")) else None
    )

    containerResult
      .map {
        case RunContainerSuccess          => ResultsFileJsonFormat.readResultFile(solutionTargetDir / resultFileName, completeTestData)
        case failure: RunContainerFailure => Failure(failure)
      }
      .map { resultTries: Try[Seq[ExecutionResult]] =>
        resultTries.map(results => ProgCompleteResult(implementation, results))
      }
  }

  private def mountProgrammingFiles(exResFolder: File, extendedUnitTests: Boolean, solTargetDir: File, solFileName: String,
                                    fileEnding: String): Seq[DockerBind] = {
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
