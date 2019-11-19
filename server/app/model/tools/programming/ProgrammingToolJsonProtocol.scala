package model.tools.programming

import model._
import model.tools.ToolJsonProtocol
import model.tools.programming.ProgConsts._
import model.tools.programming.ProgDataTypes.{GenericProgDataType, NonGenericProgDataType}
import model.tools.uml.{UmlClassDiagram, UmlClassDiagramJsonFormat}
import play.api.libs.json._

object ProgrammingToolJsonProtocol extends ToolJsonProtocol[ProgExercise, ProgSampleSolution, ProgCompleteResult] {

  lazy val progDataTypeFormat: Format[ProgDataType] = {

    val genericProgDataTypeFormat: Format[GenericProgDataType] = {
      implicit lazy val lf: Format[ProgDataTypes.LIST] = {
        implicit lazy val pdtf: Format[ProgDataType] = progDataTypeFormat

        Json.format[ProgDataTypes.LIST]
      }

      implicit lazy val tf: Format[ProgDataTypes.TUPLE] = {
        implicit lazy val pdtf: Format[ProgDataType] = progDataTypeFormat

        Json.format[ProgDataTypes.TUPLE]
      }

      implicit lazy val df: Format[ProgDataTypes.DICTIONARY] = {
        implicit lazy val pdtf: Format[ProgDataType] = progDataTypeFormat

        Json.format[ProgDataTypes.DICTIONARY]
      }

      Json.format[GenericProgDataType]
    }

    val reads: Reads[ProgDataType] = {
      case jsString: JsString => NonGenericProgDataType.jsonFormat.reads(jsString)
      case jsObject: JsObject => genericProgDataTypeFormat.reads(jsObject)
      case other              => JsError(s"Wrong json type: ${other.getClass}")
    }

    val writes: Writes[ProgDataType] = {
      case nonGenericProgDataType: NonGenericProgDataType => NonGenericProgDataType.jsonFormat.writes(nonGenericProgDataType)
      case genericProgDataType: GenericProgDataType       => genericProgDataTypeFormat.writes(genericProgDataType)
    }

    Format(reads, writes)
  }

  // Exercise

  private val unitTestTestConfigFormat: Format[UnitTestTestConfig] = Json.format[UnitTestTestConfig]

  private val progUserTestDataFormat: Format[ProgUserTestData] = Json.format[ProgUserTestData]

  val sampleSolutionJsonFormat: Format[ProgSampleSolution] = {
    implicit val psf: Format[ProgSolution] = {
      implicit val eff  : Format[ExerciseFile]     = ExerciseFileJsonProtocol.exerciseFileFormat
      implicit val putdf: Format[ProgUserTestData] = progUserTestDataFormat

      Json.format[ProgSolution]
    }

    Json.format[ProgSampleSolution]
  }

  val progSampleTestDataFormat: Format[ProgSampleTestData] = Json.format[ProgSampleTestData]

  val progInputFormat: Format[ProgInput] = {
    implicit val pdtf: Format[ProgDataType] = progDataTypeFormat

    Json.format[ProgInput]
  }

  val unitTestPartFormat: Format[UnitTestPart] = {
    implicit val uttf: Format[UnitTestType] = UnitTestTypes.jsonFormat

    implicit val eff: Format[ExerciseFile] = ExerciseFileJsonProtocol.exerciseFileFormat

    implicit val uttcf: Format[UnitTestTestConfig] = unitTestTestConfigFormat

    Json.format[UnitTestPart]
  }

  override val exerciseFormat: Format[ProgExercise] = {
    implicit val svf  : Format[SemanticVersion]    = SemanticVersionHelper.format
    implicit val ltf  : Format[LongText]           = LongTextJsonProtocol.format
    implicit val pif  : Format[ProgInput]          = progInputFormat
    implicit val pdtf : Format[ProgDataType]       = progDataTypeFormat
    implicit val utf  : Format[UnitTestPart]       = unitTestPartFormat
    implicit val pssf : Format[ProgSampleSolution] = sampleSolutionJsonFormat
    implicit val pstdf: Format[ProgSampleTestData] = progSampleTestDataFormat
    implicit val ucdf : Format[UmlClassDiagram]    = UmlClassDiagramJsonFormat.umlClassDiagramJsonFormat

    implicit val ipf  : Format[ImplementationPart] = {
      implicit val eff: Format[ExerciseFile] = ExerciseFileJsonProtocol.exerciseFileFormat
      Json.format[ImplementationPart]
    }

    Json.format[ProgExercise]
  }

  override val sampleSolutionFormat: Format[ProgSampleSolution] = sampleSolutionFormat


  // Result

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
    implicit val serw  : Writes[SimplifiedExecutionResult] = simplifiedExecutionResultFormat
    implicit val uttcrf: Writes[UnitTestCorrectionResult]  = unitTestCorrectionResultFormat
    implicit val nerw  : Writes[NormalExecutionResult]     = Json.format[NormalExecutionResult]

    Json.writes[ProgCompleteResult]
  }

  // Simplified ProgUserTestData

  private val progTestDataFormat: Format[ProgTestData] = {
    implicit val putdf: Format[ProgUserTestData]   = progUserTestDataFormat
    implicit val pstdw: Format[ProgSampleTestData] = progSampleTestDataFormat

    Json.format[ProgTestData]
  }

  def dumpCompleteTestDataToJson(exercise: ProgExercise, testData: Seq[ProgTestData]): JsValue = Json.obj(
    testDataName -> JsArray(testData.map(progTestDataFormat.writes)),
    baseDataName -> exercise.baseData
  )

}
