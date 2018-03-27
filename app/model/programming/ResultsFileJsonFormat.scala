package model.programming

import java.nio.file.Path

import model.Enums.SuccessType
import model.JsonFormat
import model.core.FileUtils
import model.programming.ProgConsts.{InputsName, idName}
import play.api.libs.json.{JsObject, JsValue, Json}

import scala.util.Try

object ResultsFileJsonFormat extends JsonFormat with FileUtils {

  def readResultFile(targetDir: Path, resultFileName: String, completeTestData: Seq[CompleteTestData]): Try[Seq[ExecutionResult]] =
    readAll(targetDir / resultFileName) map { resultFileContent =>

      // FIXME: refactor...

      val res: Option[Seq[ExecutionResult]] = Json.parse(resultFileContent).asArray {
        jsValue: JsValue => jsValue.asObj flatMap (matchDataWithJson(_, completeTestData, targetDir))
      }

      res getOrElse Seq.empty
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
    id <- jsObj.intField(idName)
    success <- jsObj.enumField("success", str => Try(SuccessType.valueOf(str)) getOrElse SuccessType.NONE)
    functionName <- jsObj.stringField("functionName")
    result <- jsObj.forgivingStringField("result")
    inputs <- jsObj.arrayField(InputsName, _.asObj flatMap readInputsFromJson)
  } yield (id, success, functionName, result, inputs)

  private def readInputsFromJson(inputJsObj: JsObject): Option[(String, String)] = for {
    maybeVariable <- inputJsObj.stringField("variable")
    maybeValue <- inputJsObj.forgivingStringField("value")
  } yield (maybeVariable, maybeValue)


}
