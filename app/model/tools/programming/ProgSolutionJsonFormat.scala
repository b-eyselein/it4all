package model.tools.programming

import model.{ExerciseState, User}
import play.api.libs.json._
import model.tools.programming.ProgConsts._
import play.api.libs.functional.syntax._

//noinspection ConvertibleToMethodValue
object ProgSolutionJsonFormat {

  private implicit val progLanguageJsonFormat: Format[ProgLanguage] = ProgLanguages.jsonFormat

  val sampleSolutionJsonFormat: Format[ProgSampleSolution] = Json.format[ProgSampleSolution]

  //  def readProgSolutionFromJson(exPart: ProgExPart, solutionJson: JsValue): JsResult[ProgSolution] = exPart match {
  //    case ProgExParts.TestdataCreation => testDataCreationSolutionReads.reads(solutionJson)
  //    case _                            => progSolutionReads.reads(solutionJson)
  //  }

  private implicit val commitedTestDataReads: Reads[ProgUserTestData] = (
    (__ \ idName).read[Int] and
      (__ \ inputName).read[JsValue] and
      (__ \ outputName).read[JsValue]
    ) (ProgUserTestData.apply(_, _, _, ExerciseState.RESERVED))

  //  private val testDataCreationSolutionReads: Reads[ProgSolution] = (
  //    (__ \ testdataName).read[Seq[ProgUserTestData]] and
  //      (__ \ languageName).read[ProgLanguage](ProgLanguages.jsonFormat)
  //    ) (ProgTestDataSolution.apply(_, _))


  val progSolutionReads: Reads[ProgSolution] = Json.reads[ProgSolution]

}
