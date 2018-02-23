package model.programming

import java.nio.file.attribute.{PosixFilePermission, PosixFilePermissions}
import java.nio.file.{Files, Path}

import com.github.dockerjava.api.model.AccessMode
import model.Enums.SuccessType
import model.core.FileUtils
import model.docker.DockerConnector.MaxRuntime
import model.docker._
import model.programming.ProgConsts._
import model.{JsonFormat, User}
import play.api.Logger
import play.api.libs.json._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

object ProgrammingCorrector extends FileUtils with JsonFormat {

  val ScriptName = "script.py"

  val ImplementationsFolder = "implementation"

  val ValidationFolder = "validation"

  val TestDataFile = "testconfig.json"

  val ResultFileName = "result.json"

  val FilePermissions: java.util.Set[PosixFilePermission] = PosixFilePermissions.fromString("rwxrwxrwx")

  private def dumpTestDataToJson(exercise: ProgExercise, inputTypes: Seq[InputType], testData: Seq[CompleteTestData]): JsValue = {
    val sortedInputTypes = inputTypes sortBy (_.id)

    val testdata: JsValue = JsArray(testData sortBy (_.testData.id) map { td =>

      val inputs: Seq[JsValue] = td.inputs zip sortedInputTypes map {
        case (sampleTestData, dataType) => dataType.inputType.toJson(sampleTestData.input)
      }

      Json.obj(
        "id" -> td.testData.id,
        "inputs" -> JsArray(inputs),
        "awaited" -> exercise.outputType.toJson(td.testData.output)
      )
    })

    Json.obj(
      "functionname" -> JsString(exercise.functionName),
      "variableTypes" -> JsArray(sortedInputTypes map (_.inputType.typeName) map JsString),
      "outputType" -> exercise.outputType.typeName,
      "testdata" -> testdata
    )
  }

  def validateTestdata(user: User, exercise: ProgCompleteEx, solution: TestdataSolution)(implicit ec: ExecutionContext): Future[ProgCompleteResult] = {

    val language = ProgLanguage.STANDARD_LANG

    val targetDir = ProgToolObject.solutionDirForExercise(user.username, exercise.id) / ValidationFolder

    val script = exercise.sampleSolution.solution

    correct(exercise, language, targetDir, script, solution.completeCommitedTestData) map ProgValidationCompleteResult

  }

  def correctImplementation(user: User, exercise: ProgCompleteEx, learnerSolution: String, language: ProgLanguage)(implicit ec: ExecutionContext): Future[ProgCompleteResult] = {

    val targetDir = ProgToolObject.solutionDirForExercise(user.username, exercise.id) / ImplementationsFolder

    val script = if (learnerSolution endsWith "\n") learnerSolution else learnerSolution + "\n"

    correct(exercise, language, targetDir, script, exercise.sampleTestData) map (ProgImplementationCompleteResult(script, _))

  }

  private def correct(exercise: ProgCompleteEx, language: ProgLanguage, targetDir: Path, script: String, completeTestData: Seq[CompleteTestData])
                     (implicit ec: ExecutionContext): Future[Seq[ProgEvalResult]] = {

    val scriptTargetPath = targetDir / ScriptName

    val futureImageExists: Future[Boolean] = Future(DockerConnector.imageExists(language.dockerImageName) || DockerConnector.pullImage(language.dockerImageName))

    write(targetDir / s"solution.${language.fileEnding}", script)

    write(targetDir / TestDataFile, Json.prettyPrint(dumpTestDataToJson(exercise.ex, exercise.inputTypes, completeTestData)))

    copy(ProgToolObject.exerciseResourcesFolder / ScriptName, scriptTargetPath)

    Files.setPosixFilePermissions(scriptTargetPath, FilePermissions)

    val workingDir = DockerConnector.DefaultWorkingDir

    // Check if image exists
    futureImageExists flatMap {
      _ =>
        DockerConnector.runContainer(
          imageName = language.dockerImageName,
          entryPoint = Seq("timeout", MaxRuntime + "s", s"$workingDir/script." + language.fileEnding),
          dockerBinds = Seq(new DockerBind(targetDir, workingDir, AccessMode.rw))
        )
    } map {
      // Error while waiting for container
      case exc: RunContainerException =>
        Logger.error("Error while running container:", exc.error)
        Seq(ProgEvalFailed)

      case RunContainerTimeOut => Seq(TimeOut)

      // Error while running script with status code other than 0 or 124 (from timeout!)
      case RunContainerError(_, msg) => Seq(SyntaxError(reason = msg))

      case RunContainerSuccess => readResultFile(targetDir, completeTestData)
    }
  }

  private def readResultFile(targetDir: Path, completeTestData: Seq[CompleteTestData]) = readAll(targetDir / ResultFileName) match {
    case Failure(error) =>
      Logger.error("TODO: catch error", error)
      throw new Exception("TODO: Fehler behandeln!")

    case Success(resultFileContent) =>
      val jsonResult = Json.parse(resultFileContent)
      jsonResult.asArray(_.asObj flatMap (matchDataWithJson(_, completeTestData, targetDir))) getOrElse Seq.empty
  }

  private def matchDataWithJson(jsObj: JsObject, completeTestData: Seq[CompleteTestData], targetDir: Path) = readDataFromJson(jsObj) map {
    case (id, successType, funcName, result, inputs) =>

      val evaluated = funcName + "(" + inputs.sortBy(_._1).map(_._2).mkString(", ") + ")"

      val consoleOutput: Option[String] = readAll(targetDir / s"output$id.txt") map Some.apply getOrElse None

      val testData: CompleteTestData = completeTestData find (_.testData.id == id) match {
        case None    => throw new Exception("FEHLER!")
        case Some(x) => x
      }

      ExecutionResult(successType, evaluated, testData, result, consoleOutput)
  }

  private def readDataFromJson(jsObj: JsObject): Option[(Int, SuccessType, String, String, Seq[(String, String)])] = for {
    id <- jsObj.intField(ID_NAME)
    success <- jsObj.enumField("success", str => Try(SuccessType.valueOf(str)) getOrElse SuccessType.NONE)
    functionName <- jsObj.stringField("functionName")
    result <- jsObj.forgivingStringField("result")
    inputs <- jsObj.arrayField(INPUTS_NAME, _.asObj flatMap readInputsFromJson)
  } yield (id, success, functionName, result, inputs)

  private def readInputsFromJson(inputJsObj: JsObject): Option[(String, String)] = for {
    maybeVariable <- inputJsObj.stringField("variable")
    maybeValue <- inputJsObj.forgivingStringField("value")
  } yield (maybeVariable, maybeValue)

}