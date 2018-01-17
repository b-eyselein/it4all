package model.programming

import java.nio.file.Files
import java.nio.file.attribute.{PosixFilePermission, PosixFilePermissions}

import controllers.exes.idPartExes.ProgToolObject
import model.Enums.SuccessType
import model.core.FileUtils
import model.programming.ProgConsts._
import model.{JsonFormat, User}
import play.api.libs.json._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

object ProgCorrector extends FileUtils with JsonFormat {

  val ScriptName = "script.py"

  val TestdataFile = "testconfig.json"

  val FilePermissions: java.util.Set[PosixFilePermission] = PosixFilePermissions.fromString("rwxrwxrwx")

  val ResultFileName = "result.json"

  private def dumpTestDataToJson(exercise: ProgCompleteEx): JsValue = {
    val sortedInputTypes = exercise.inputTypes sortBy (_.id)

    val testdata: JsValue = JsArray(exercise.sampleTestData sortBy (_.sampleTestData.id) map { td =>

      val inputs: Seq[JsValue] = td.inputs zip sortedInputTypes map {
        case (sampleTestData, dataType) => dataType.inputType.toJson(sampleTestData.input)
      }

      Json.obj(
        "id" -> td.sampleTestData.id,
        "inputs" -> JsArray(inputs),
        "awaited" -> exercise.ex.outputType.toJson(td.sampleTestData.output)
      )
    })

    Json.obj(
      "functionname" -> JsString(exercise.ex.functionName),
      "variableTypes" -> JsArray(sortedInputTypes map (_.inputType.typeName) map JsString),
      "outputType" -> exercise.ex.outputType.typeName,
      "testdata" -> testdata
    )
  }


  def correct(user: User, exercise: ProgCompleteEx, learnerSolution: String, language: ProgLanguage)(implicit ec: ExecutionContext): Future[ProgCompleteResult] = {

    val futureImageExists: Future[Boolean] = Future(DockerConnector.imageExists(language.dockerImageName) || DockerConnector.pullImage(language.dockerImageName))

    val targetDir = ProgToolObject.solutionDirForExercise(user.username, exercise.ex)

    write(targetDir / s"solution.${language.fileEnding}", if (learnerSolution endsWith "\n") learnerSolution else learnerSolution + "\n")

    write(targetDir / TestdataFile, Json.prettyPrint(dumpTestDataToJson(exercise)))

    // FIXME: copy conf/resources/prog/script.py to targetDir!
    val scriptTargetPath = targetDir / ScriptName
    copy(ProgToolObject.resourcesFolder / "prog" / ScriptName, scriptTargetPath)
    Files.setPosixFilePermissions(scriptTargetPath, FilePermissions)

    // Check if image exists
    futureImageExists flatMap {
      _ => DockerConnector.runContainer(language, targetDir)
    } map {
      case Left(errorMsg) =>

        val res = SyntaxError(reason = errorMsg)
        ProgCompleteResult(learnerSolution, Seq(res))

      case Right(statusCode) =>
        readAll(targetDir / ResultFileName) match {
          case Failure(error) => throw new Exception("TODO: Fehler behandeln!")

          case Success(resultFileContent) =>
            val jsonResult = Json.parse(resultFileContent)

            // FIXME: read jsonResult!
            val results: Option[Seq[ProgEvalResult]] = jsonResult.asArray(_.asObj flatMap { jsObj =>

              val data: Option[(Int, SuccessType, String, String, Seq[(String, String)])] = readDataFromJson(jsObj)

              data map {
                case (id, successType, funcName, result, inputs) =>

                  val evaluated = funcName + "(" + inputs.sortBy(_._1).map(_._2).mkString(", ") + ")"

                  val consoleOutput: Option[String] = readAll(targetDir / s"output$id.txt") map Some.apply getOrElse None

                  val testData: CompleteSampleTestData = exercise.sampleTestData find (_.sampleTestData.id == id) match {
                    case None    => throw new Exception("FEHLER!")
                    case Some(x) => x
                  }

                  val executionResult = AExecutionResult(successType, evaluated, testData.sampleTestData.output, result, consoleOutput)

                  executionResult
              }

            })

            results match {
              case Some(res) => ProgCompleteResult(learnerSolution, res)
              case None      => throw new Exception("TODO!")
            }
        }
    }
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