package model.tools.collectionTools.programming

import model.points.Points
import model.tools.collectionTools.programming.ProgConsts._
import model.tools.collectionTools.programming.ProgDataTypes.{GenericProgDataType, NonGenericProgDataType}
import model.tools.collectionTools.uml.{UmlClassDiagram, UmlClassDiagramJsonFormat}
import model.tools.collectionTools.{ExerciseFile, ExerciseFileJsonProtocol, ToolJsonProtocol}
import play.api.libs.json._

object ProgrammingToolJsonProtocol extends ToolJsonProtocol[ProgExPart, ProgExerciseContent,  ProgSolution,  ProgUserSolution, ProgCompleteResult] {

  private val progSolutionFormat: Format[ProgSolution] = {
    implicit val eff  : Format[ExerciseFile]     = ExerciseFileJsonProtocol.exerciseFileFormat
    implicit val putdf: Format[ProgTestData] = progTestDataFormat

    Json.format[ProgSolution]
  }

  private val sampleSolutionFormat: Format[ProgSampleSolution] = {
    implicit val psf: Format[ProgSolution] = progSolutionFormat

    Json.format[ProgSampleSolution]
  }

  override val userSolutionFormat: Format[ProgUserSolution] = {
    implicit val ptf: Format[ProgExPart]   = ProgExParts.jsonFormat
    implicit val pf : Format[Points]       = ToolJsonProtocol.pointsFormat
    implicit val psf: Format[ProgSolution] = progSolutionFormat

    Json.format[ProgUserSolution]
  }


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

  private val progTestDataFormat: Format[ProgTestData] = Json.format[ProgTestData]



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

  val implementationPartFormat: Format[ImplementationPart] = {
    implicit val eff: Format[ExerciseFile] = ExerciseFileJsonProtocol.exerciseFileFormat

    Json.format[ImplementationPart]
  }

  override val exerciseContentFormat: Format[ProgExerciseContent] = {
    implicit val pif  : Format[ProgInput]          = progInputFormat
    implicit val pdtf : Format[ProgDataType]       = progDataTypeFormat
    implicit val utf  : Format[UnitTestPart]       = unitTestPartFormat
    implicit val ipf  : Format[ImplementationPart] = implementationPartFormat
    implicit val pssf : Format[ProgSampleSolution] = sampleSolutionFormat
    implicit val pstdf: Format[ProgTestData]       = progTestDataFormat
    implicit val ucdf : Format[UmlClassDiagram]    = UmlClassDiagramJsonFormat.umlClassDiagramJsonFormat

    Json.format[ProgExerciseContent]
  }


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

  def dumpCompleteTestDataToJson(exercise: ProgExerciseContent, testData: Seq[ProgTestData]): JsValue = Json.obj(
    testDataName -> JsArray(testData.map(progTestDataFormat.writes)),
    baseDataName -> exercise.baseData
  )

}
