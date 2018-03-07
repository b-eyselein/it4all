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

  def validateTestdata(user: User, exercise: ProgCompleteEx, solution: TestDataSolution, solSaved: Boolean, solutionTargetDir: Path, exerciseResourcesFolder: Path)
                      (implicit ec: ExecutionContext): Future[ProgValidationCompleteResult] = {

    val language = ProgLanguage.STANDARD_LANG

    val script = exercise.sampleSolution.solution

    correct(exercise, language, solutionTargetDir, script, solution.completeCommitedTestData, exerciseResourcesFolder) map (res => ProgValidationCompleteResult.apply(solSaved, res))
  }


  def correctImplementation(user: User, exercise: ProgCompleteEx, learnerSolution: String, solSaved: Boolean, language: ProgLanguage, solutionTargetDir: Path, exerciseResourcesFolder: Path)
                           (implicit ec: ExecutionContext): Future[ProgCompleteResult] = {

    correct(exercise, language, solutionTargetDir, learnerSolution, exercise.sampleTestData, exerciseResourcesFolder) map {
      res => ProgImplementationCompleteResult(learnerSolution, solSaved, res)
    }

  }


  private def correct(exercise: ProgCompleteEx, language: ProgLanguage, targetDir: Path, script: String, completeTestData: Seq[CompleteTestData], exerciseResourcesFolder: Path)
                     (implicit ec: ExecutionContext): Future[Seq[ProgEvalResult]] = {

    val scriptTargetPath = targetDir / ScriptName

    val futureImageExists: Future[Boolean] = Future(DockerConnector.imageExists(language.dockerImageName))

    val workingDir = DockerConnector.DefaultWorkingDir

    val fileTries: Try[(Path, Path, Path, Path)] = for {
      solutionWrite: Path <- write(targetDir / s"solution.${language.fileEnding}", script)

      testDataWrite: Path <- write(targetDir / TestDataFile, Json.prettyPrint(dumpTestDataToJson(exercise.ex, exercise.inputTypes, completeTestData)))

      scriptCopy: Path <- copy(/*ProgToolObject.exerciseResourcesFolder */ exerciseResourcesFolder / ScriptName, scriptTargetPath)

      permissionChange: Path <- Try(Files.setPosixFilePermissions(scriptTargetPath, FilePermissions))
    } yield (solutionWrite, testDataWrite, scriptCopy, permissionChange)

    fileTries match {
      case Failure(error) =>
        Logger.error("There has been an error writing a file: ", error)
        Future(Seq.empty)
      case Success(_)     =>
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

      println(inputs)

      val evaluated = funcName + "(" + inputs.sortBy(_._1).map(_._2).mkString(", ") + ")"

      val consoleOutput: Option[String] = readAll(targetDir / s"output$id.txt") map Some.apply getOrElse None

      val testData: CompleteTestData = completeTestData find (_.testData.id == id) match {
        case None    => throw new Exception("FEHLER!")
        case Some(x) => x
      }

      ExecutionResult(successType, evaluated, testData, result, consoleOutput)
  }

  private def readDataFromJson(jsObj: JsObject): Option[(Int, SuccessType, String, String, Seq[(String, String)])] = for {
    id <- jsObj.intField(idName)
    success <- jsObj.enumField("success", str => Try(SuccessType.valueOf(str)) getOrElse SuccessType.NONE)
    functionName <- jsObj.stringField("functionName")
    result <- jsObj.forgivingStringField("result")
    inputs <- jsObj.arrayField(InputsName, _.asObj flatMap readInputsFromJson)
  } yield (id, success, functionName, result, inputs)

  private def readInputsFromJson(inputJsObj: JsObject): Option[(String, String)] ={
    println(inputJsObj)
    for {
    maybeVariable <- inputJsObj.stringField("variable")
    maybeValue <- inputJsObj.forgivingStringField("value")
  } yield (maybeVariable, maybeValue)
  }

}