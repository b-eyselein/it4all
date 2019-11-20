package model.tools.collectionTools.programming

import java.nio.file.{Path, Paths}

import model._
import model.core.{LongText, LongTextYamlProtocol}
import model.tools.collectionTools.ExerciseFile
import model.tools.collectionTools.programming.ProgDataTypes.NonGenericProgDataType
import model.tools.collectionTools.uml.UmlClassDiagram
import net.jcazevedo.moultingyaml._
import play.api.libs.json.JsValue

import scala.language.implicitConversions

object ProgExYamlProtocol extends MyYamlProtocol {

  private val basePath: Path = Paths.get("conf", "resources", "programming")

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
    implicit val efyf: YamlFormat[ExerciseFile] = exerciseFileYamlFormat

    implicit val uttyf: YamlFormat[UnitTestType] = new EnumYamlFormat(UnitTestTypes)

    implicit val uttcyf: YamlFormat[UnitTestTestConfig] = yamlFormat4(UnitTestTestConfig)

    yamlFormat6(UnitTestPart)
  }

  private val implementationPartYamlFormat: YamlFormat[ImplementationPart] = {
    implicit val efyf: YamlFormat[ExerciseFile] = exerciseFileYamlFormat

    yamlFormat4(ImplementationPart)
  }

  private val progSampleSolutionYamlFormat: YamlFormat[ProgSampleSolution] = {

    implicit val progSolutionYamlFormat: YamlFormat[ProgSolution] = {
      implicit val efyf: YamlFormat[ExerciseFile] = exerciseFileYamlFormat

      implicit val putdyf: YamlFormat[ProgUserTestData] = {
        implicit val jyf: YamlFormat[JsValue] = jsonValueYamlFormat

        implicit val esyf: YamlFormat[ExerciseState] = exerciseStateYamlFormat

        yamlFormat4(ProgUserTestData)
      }

      yamlFormat2(ProgSolution)
    }

    yamlFormat2(ProgSampleSolution)

  }

  private val progSampleTestDataYamlFormat: YamlFormat[ProgSampleTestData] = {
    implicit val jyf: YamlFormat[JsValue] = jsonValueYamlFormat

    yamlFormat3(ProgSampleTestData)
  }

  val programmingExerciseYamlFormat: YamlFormat[ProgExercise] = {
    implicit val svyf: YamlFormat[SemanticVersion] = semanticVersionYamlFormat
    implicit val ltyf: YamlFormat[LongText] = LongTextYamlProtocol.longTextYamlFormat
    implicit val esyf: YamlFormat[ExerciseState] = exerciseStateYamlFormat

    implicit val piyf: YamlFormat[ProgInput] = progInputYamlFormat
    implicit val pdtyf: YamlFormat[ProgDataType] = progDataTypeYamlFormat
    implicit val jvyf: YamlFormat[JsValue] = jsonValueYamlFormat
    implicit val utpyf: YamlFormat[UnitTestPart] = unitTestPartYamlFormat
    implicit val ipyf: YamlFormat[ImplementationPart] = implementationPartYamlFormat
    implicit val pssyf: YamlFormat[ProgSampleSolution] = progSampleSolutionYamlFormat
    implicit val pstdyf: YamlFormat[ProgSampleTestData] = progSampleTestDataYamlFormat

    implicit val ucdyf: YamlFormat[UmlClassDiagram] = new YamlFormat[UmlClassDiagram] {

      override def read(yaml: YamlValue): UmlClassDiagram = ???

      override def write(obj: UmlClassDiagram): YamlValue = ???

    }

    implicit val petyf: YamlFormat[ProgrammingExerciseTag] = new EnumYamlFormat(ProgrammingExerciseTag)

    yamlFormat20(ProgExercise)
  }

}
