package model.tools.collectionTools.programming

import model._
import model.tools.collectionTools.programming.ProgDataTypes.NonGenericProgDataType
import model.tools.collectionTools.uml.UmlClassDiagram
import model.tools.collectionTools.{ExerciseFile, ExerciseFileYamlProtocol, SampleSolution}
import net.jcazevedo.moultingyaml._
import play.api.libs.json.JsValue

object ProgExYamlProtocol extends MyYamlProtocol {

  import DefaultYamlProtocol._

  //  private val basePath: Path = Paths.get("conf", "resources", "programming")

  val progDataTypeYamlFormat: YamlFormat[ProgDataType] = new YamlFormat[ProgDataType] {

    override def read(yaml: YamlValue): ProgDataType = yaml match {
      case YamlString(value)  => NonGenericProgDataType.withNameInsensitive(value)
      case YamlObject(fields) =>
        fields.get(YamlString("genericType")) match {
          case Some(YamlString(value)) =>
            val subType: ProgDataType = progDataTypeYamlFormat.read(fields.getOrElse(YamlString("subType"), ???))

            value match {
              case "LIST"  => ProgDataTypes.LIST(subType)
              case "TUPLE" => ProgDataTypes.TUPLE(??? /*Seq(subType)*/)
              case "DICT"  => ProgDataTypes.DICTIONARY(???, ??? /*subType*/)
            }
          case _                       => ???
        }
      case _                  => deserializationError("Expected YamlObject or YamlString!")
    }

    override def write(obj: ProgDataType): YamlValue = ???

  }

  private val progInputYamlFormat: YamlFormat[ProgInput] = {
    implicit val pdtyf: YamlFormat[ProgDataType] = progDataTypeYamlFormat

    yamlFormat3(ProgInput)
  }

  private val unitTestPartYamlFormat: YamlFormat[UnitTestPart] = {
    implicit val efyf  : YamlFormat[ExerciseFile]       = ExerciseFileYamlProtocol.exerciseFileYamlFormat
    implicit val uttyf : YamlFormat[UnitTestType]       = new EnumYamlFormat(UnitTestTypes)
    implicit val uttcyf: YamlFormat[UnitTestTestConfig] = yamlFormat4(UnitTestTestConfig)

    yamlFormat6(UnitTestPart)
  }

  private val implementationPartYamlFormat: YamlFormat[ImplementationPart] = {
    implicit val efyf: YamlFormat[ExerciseFile] = ExerciseFileYamlProtocol.exerciseFileYamlFormat

    yamlFormat4(ImplementationPart)
  }

  private val progTestDataYamlFormat: YamlFormat[ProgTestData] = {
    implicit val jyf: YamlFormat[JsValue] = jsonValueYamlFormat

    yamlFormat3(ProgTestData)
  }

  private val progSolutionYamlFormat: YamlFormat[ProgSolution] = {
    implicit val efyf  : YamlFormat[ExerciseFile] = ExerciseFileYamlProtocol.exerciseFileYamlFormat
    implicit val putdyf: YamlFormat[ProgTestData] = progTestDataYamlFormat

    yamlFormat2(ProgSolution)
  }

  val programmingExerciseYamlFormat: YamlFormat[ProgExerciseContent] = {
    implicit val piyf  : YamlFormat[ProgInput]                    = progInputYamlFormat
    implicit val pdtyf : YamlFormat[ProgDataType]                 = progDataTypeYamlFormat
    implicit val jvyf  : YamlFormat[JsValue]                      = jsonValueYamlFormat
    implicit val utpyf : YamlFormat[UnitTestPart]                 = unitTestPartYamlFormat
    implicit val ipyf  : YamlFormat[ImplementationPart]           = implementationPartYamlFormat
    implicit val pssyf : YamlFormat[SampleSolution[ProgSolution]] = sampleSolutionYamlFormat[ProgSolution](progSolutionYamlFormat)
    implicit val pstdyf: YamlFormat[ProgTestData]                 = progTestDataYamlFormat

    implicit val ucdyf: YamlFormat[UmlClassDiagram] = new YamlFormat[UmlClassDiagram] {

      override def read(yaml: YamlValue): UmlClassDiagram = ???

      override def write(obj: UmlClassDiagram): YamlValue = ???

    }

    implicit val petyf: YamlFormat[ProgrammingExerciseTag] = new EnumYamlFormat(ProgrammingExerciseTag)

    yamlFormat12(ProgExerciseContent)
  }

}
