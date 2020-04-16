package model.tools.programming

import model.tools.programming.ProgConsts._
import model.tools.programming.ProgDataTypes.{GenericProgDataType, NonGenericProgDataType}
import model.tools.uml.{UmlClassDiagram, UmlClassDiagramJsonFormat}
import model.tools.{ExerciseFile, SampleSolution, SemanticVersion, ToolJsonProtocol}
import play.api.libs.json._

object ProgConsts {

  val baseDataName: String = "baseData"
  val testDataName: String = "testData"

}

object ProgrammingToolJsonProtocol extends ToolJsonProtocol[ProgrammingExercise, ProgSolution, ProgExPart] {

  private val progTestDataFormat: Format[ProgTestData] = Json.format[ProgTestData]

  override val solutionFormat: Format[ProgSolution] = {
    implicit val eff: Format[ExerciseFile]   = ToolJsonProtocol.exerciseFileFormat
    implicit val putdf: Format[ProgTestData] = progTestDataFormat

    Json.format
  }

  private val sampleSolutionFormat: Format[SampleSolution[ProgSolution]] = {
    implicit val psf: Format[ProgSolution] = solutionFormat

    Json.format
  }

  lazy val progDataTypeFormat: Format[ProgDataType] = {

    val genericProgDataTypeFormat: Format[GenericProgDataType] = {
      implicit lazy val lf: Format[ProgDataTypes.LIST] = {
        implicit lazy val pdtf: Format[ProgDataType] = progDataTypeFormat

        Json.format
      }

      implicit lazy val tf: Format[ProgDataTypes.TUPLE] = {
        implicit lazy val pdtf: Format[ProgDataType] = progDataTypeFormat

        Json.format
      }

      implicit lazy val df: Format[ProgDataTypes.DICTIONARY] = {
        implicit lazy val pdtf: Format[ProgDataType] = progDataTypeFormat

        Json.format
      }

      Json.format
    }

    val reads: Reads[ProgDataType] = {
      case jsString: JsString => NonGenericProgDataType.jsonFormat.reads(jsString)
      case jsObject: JsObject => genericProgDataTypeFormat.reads(jsObject)
      case other              => JsError(s"Wrong json type: ${other.getClass}")
    }

    val writes: Writes[ProgDataType] = {
      case nonGenericProgDataType: NonGenericProgDataType =>
        NonGenericProgDataType.jsonFormat.writes(nonGenericProgDataType)
      case genericProgDataType: GenericProgDataType => genericProgDataTypeFormat.writes(genericProgDataType)
    }

    Format(reads, writes)
  }

  // Exercise

  private val unitTestTestConfigFormat: Format[UnitTestTestConfig] = {
    implicit val eff: Format[ExerciseFile] = ToolJsonProtocol.exerciseFileFormat

    Json.format
  }

  val progInputFormat: Format[ProgInput] = {
    implicit val pdtf: Format[ProgDataType] = progDataTypeFormat

    Json.format
  }

  val unitTestPartFormat: Format[UnitTestPart] = {
    implicit val uttf: Format[UnitTestType] = UnitTestTypes.jsonFormat

    implicit val eff: Format[ExerciseFile] = ToolJsonProtocol.exerciseFileFormat

    implicit val uttcf: Format[UnitTestTestConfig] = unitTestTestConfigFormat

    Json.format
  }

  val implementationPartFormat: Format[ImplementationPart] = {
    implicit val eff: Format[ExerciseFile] = ToolJsonProtocol.exerciseFileFormat

    Json.format
  }

  override val exerciseFormat: Format[ProgrammingExercise] = {
    implicit val svf: Format[SemanticVersion]               = ToolJsonProtocol.semanticVersionFormat
    implicit val pif: Format[ProgInput]                     = progInputFormat
    implicit val pdtf: Format[ProgDataType]                 = progDataTypeFormat
    implicit val utf: Format[UnitTestPart]                  = unitTestPartFormat
    implicit val ipf: Format[ImplementationPart]            = implementationPartFormat
    implicit val pssf: Format[SampleSolution[ProgSolution]] = sampleSolutionFormat
    implicit val pstdf: Format[ProgTestData]                = progTestDataFormat
    implicit val ucdf: Format[UmlClassDiagram]              = UmlClassDiagramJsonFormat.umlClassDiagramJsonFormat

    Json.format
  }

  // Result

  // Simplified execution

  val simplifiedExecutionResultFormat: Format[SimplifiedExecutionResult] = Json.format[SimplifiedExecutionResult]

  // Normal execution

  final case class UnitTestTestData(
    folderName: String,
    filename: String,
    testFilename: String,
    testConfigs: Seq[UnitTestTestConfig]
  )

  val unitTestDataWrites: Writes[UnitTestTestData] = {
    implicit val uttcf: Format[UnitTestTestConfig] = unitTestTestConfigFormat

    Json.writes
  }

  private val unitTestCorrectionResultFormat: Format[UnitTestCorrectionResult] = {
    implicit val uttcf: Format[UnitTestTestConfig] = unitTestTestConfigFormat

    Json.format
  }

  val unitTestCorrectionResultsFileJsonReads: Reads[UnitTestCorrectionResultFileContent] = {
    implicit val utcrf: Format[UnitTestCorrectionResult] = unitTestCorrectionResultFormat

    Json.reads
  }

  /*
  override val completeResultWrites: Writes[ProgCompleteResult] = {
    implicit val serw: Writes[SimplifiedExecutionResult]  = simplifiedExecutionResultFormat
    implicit val uttcrf: Writes[UnitTestCorrectionResult] = unitTestCorrectionResultFormat
    implicit val nerw: Writes[NormalExecutionResult]      = Json.format[NormalExecutionResult]

    Json.writes[ProgCompleteResult]
  }
   */

  // Simplified ProgUserTestData

  def dumpCompleteTestDataToJson(baseData: Option[JsValue], testData: Seq[ProgTestData]): JsValue = Json.obj(
    testDataName -> JsArray(testData.map(progTestDataFormat.writes)),
    baseDataName -> baseData
  )

  override val partTypeFormat: Format[ProgExPart] = ProgExPart.jsonFormat

}