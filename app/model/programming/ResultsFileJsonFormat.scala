package model.programming

import java.nio.file.Path

import model.Enums.SuccessType
import model.JsonFormat
import model.core.FileUtils
import model.programming.ProgConsts._
import play.api.libs.json.{JsObject, JsValue, Json}

import scala.util.Try

object ResultsFileJsonFormat extends JsonFormat with FileUtils {

  def readResultFile(targetFile: Path, completeTestData: Seq[TestData]): Try[Seq[ExecutionResult]] = readAll(targetFile) map { resultFileContent =>

    // FIXME: refactor...

    val res: Option[Seq[ExecutionResult]] = Json.parse(resultFileContent).asArray {
      jsValue: JsValue =>
        jsValue.asObj flatMap {
          jsObj => matchDataWithJson(jsObj, completeTestData)
        }
    }

    res getOrElse Seq.empty
  }

  private def matchDataWithJson(jsObj: JsObject, completeTestData: Seq[TestData]): Option[ExecutionResult] = readDataFromJson(jsObj) map {
    case (id, successType, consoleOutput, gotten) =>

      val testData: TestData = completeTestData find (_.id == id) match {
        case None    => throw new Exception("FEHLER!")
        case Some(x) => x
      }

      val maybeConsoleOutput = if (consoleOutput.isEmpty) None else Some(consoleOutput)

      ExecutionResult(successType, testData, gotten, maybeConsoleOutput)
  }

  private def readDataFromJson(jsObj: JsObject): Option[(Int, SuccessType, String, String)] = for {

    id <- jsObj.intField(idName)

    success <- jsObj.enumField(successName, str => Try(SuccessType.valueOf(str)) getOrElse SuccessType.NONE)

    consoleOutput <- jsObj.stringField(stdoutName)

    gotten <- jsObj.forgivingStringField(gottenName)

  } yield (id, success, consoleOutput, gotten)

}
