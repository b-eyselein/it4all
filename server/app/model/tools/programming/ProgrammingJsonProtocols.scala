package model.tools.programming

import model._
import model.core.result.CompleteResultJsonProtocol
import model.tools.programming.ProgConsts._
import model.tools.uml.{UmlClassDiagram, UmlClassDiagramJsonFormat}
import play.api.libs.json._

object ProgrammingJsonProtocols extends CompleteResultJsonProtocol[ProgEvalResult, ProgCompleteResult] {

  // Exercise

  private val unitTestTestConfigFormat: Format[UnitTestTestConfig] = Json.format[UnitTestTestConfig]

  private val progUserTestDataFormat: Format[ProgUserTestData] = Json.format[ProgUserTestData]

  val sampleSolutionJsonFormat: Format[ProgSampleSolution] = {
    implicit val psf: Format[ProgSolution] = {
      implicit val eff: Format[ExerciseFile] = FilesSampleSolutionJsonProtocol.exerciseFileJsonFormat

      implicit val putdf: Format[ProgUserTestData] = progUserTestDataFormat

      Json.format[ProgSolution]
    }

    Json.format[ProgSampleSolution]
  }

  val progSampleTestDataFormat: Format[ProgSampleTestData] = Json.format[ProgSampleTestData]

  val progInputFormat: Format[ProgInput] = {
    implicit val pdtf: Format[ProgDataType] = ProgDataTypes.jsonFormat

    Json.format[ProgInput]
  }

  val unitTestPartFormat: Format[UnitTestPart] = {
    implicit val uttf: Format[UnitTestType] = UnitTestTypes.jsonFormat

    implicit val eff: Format[ExerciseFile] = ExerciseFileJsonProtocol.exerciseFileFormat

    implicit val uttcf: Format[UnitTestTestConfig] = unitTestTestConfigFormat

    Json.format[UnitTestPart]
  }

  val exerciseFormat: Format[ProgExercise] = {
    implicit val semVerFormat: Format[SemanticVersion] = SemanticVersionHelper.format

    implicit val pif: Format[ProgInput] = progInputFormat

    implicit val pdtf: Format[ProgDataType] = ProgDataTypes.jsonFormat

    implicit val utf: Format[UnitTestPart] = unitTestPartFormat

    implicit val ipf: Format[ImplementationPart] = {
      implicit val eff: Format[ExerciseFile] = ExerciseFileJsonProtocol.exerciseFileFormat

      Json.format[ImplementationPart]
    }

    implicit val pssf: Format[ProgSampleSolution] = sampleSolutionJsonFormat

    implicit val pstdf: Format[ProgSampleTestData] = progSampleTestDataFormat

    implicit val ucdf: Format[UmlClassDiagram] = UmlClassDiagramJsonFormat.umlClassDiagramJsonFormat

    Json.format[ProgExercise]
  }

  // Simplified execution

  private val simplifiedExecutionResultFormat: Format[SimplifiedExecutionResult] = Json.format[SimplifiedExecutionResult]

  val simplifiedResultsFileJsonReads: Reads[SimplifiedResultFileContent] = {
    implicit val serf: Format[SimplifiedExecutionResult] = simplifiedExecutionResultFormat

    Json.reads[SimplifiedResultFileContent]
  }

  // Normal execution


  final case class UnitTestTestData(foldername: String, filename: String, testFilename: String, testConfigs: Seq[UnitTestTestConfig])

  val unitTestDataWrites: Writes[UnitTestTestData] = {
    implicit val uttcf: Format[UnitTestTestConfig] = unitTestTestConfigFormat

    Json.writes[UnitTestTestData]
  }


  private val unitTestCorrectionResultFormat: Format[UnitTestCorrectionResult] = {
    implicit val uttcf: Format[UnitTestTestConfig] = unitTestTestConfigFormat

    Json.format[UnitTestCorrectionResult]
  }

  val unitTestCorrectionResultsFileJsonReads: Reads[UnitTestCorrectionResultFileContent] = {
    implicit val utcrf: Format[UnitTestCorrectionResult] = unitTestCorrectionResultFormat

    Json.reads[UnitTestCorrectionResultFileContent]
  }

  override val completeResultWrites: Writes[ProgCompleteResult] = {
    implicit val serw: Writes[SimplifiedExecutionResult] = simplifiedExecutionResultFormat

    implicit val nerw: Writes[NormalExecutionResult] = Json.format[NormalExecutionResult]

    implicit val uttcrf: Writes[UnitTestCorrectionResult] = unitTestCorrectionResultFormat

    Json.writes[ProgCompleteResult]
  }

  // Simplified ProgUserTestData

  private val progTestDataFormat: Format[ProgTestData] = {
    implicit val putdf: Format[ProgUserTestData] = progUserTestDataFormat

    implicit val pstdw: Format[ProgSampleTestData] = progSampleTestDataFormat

    Json.format[ProgTestData]
  }

  def dumpCompleteTestDataToJson(exercise: ProgExercise, testData: Seq[ProgTestData]): JsValue = Json.obj(
    testDataName -> JsArray(testData.map(progTestDataFormat.writes)),
    baseDataName -> exercise.baseData
  )

}
