package model.programming

import model.{ExerciseState, User}
import play.api.libs.json._
import model.programming.ProgConsts._
import play.api.libs.functional.syntax._

//noinspection ConvertibleToMethodValue
final case class ProgSolutionJsonFormat(exercise: ProgCompleteEx, user: User) {

  def readProgSolutionFromJson(exPart: ProgExPart, solutionJson: JsValue): JsResult[_ <: ProgSolution] = exPart match {
    case ProgExParts.TestdataCreation => testDataCreationSolutionReads.reads(solutionJson)
    case _                            => progStringSolutionReads.reads(solutionJson)
  }

  private implicit val commitedTestDataReads: Reads[CommitedTestData] = (
    (__ \ idName).read[Int] and
      (__ \ inputName).read[JsValue] and
      (__ \ outputName).read[JsValue]
    ) (CommitedTestData.apply(_, exercise.ex.id, exercise.ex.semanticVersion, _, _, user.username, ExerciseState.RESERVED))

  private val testDataCreationSolutionReads: Reads[ProgTestDataSolution] = (
    (__ \ testdataName).read[Seq[CommitedTestData]] and
      (__ \ languageName).read[ProgLanguage](ProgLanguages.jsonFormat)
    ) (ProgTestDataSolution.apply(_, _))


  private val progStringSolutionReads: Reads[ProgStringSolution] = (
    (__ \ implementationName).read[String] and
      (__ \ extendedUnitTestsName).read[Boolean] and
      (__ \ languageName).read[ProgLanguage](ProgLanguages.jsonFormat)
    ) (ProgStringSolution.apply(_, _, _))

}
