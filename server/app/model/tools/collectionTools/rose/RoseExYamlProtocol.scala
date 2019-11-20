package model.tools.collectionTools.rose

import model.tools.collectionTools.programming.{ProgDataType, ProgExYamlProtocol, ProgLanguage, ProgLanguages}
import model.{ExerciseState, LongText, LongTextYamlProtocol, MyYamlProtocol, SemanticVersion}
import net.jcazevedo.moultingyaml._

object RoseExYamlProtocol extends MyYamlProtocol {

  private val roseInputTypeYamlFormat: YamlFormat[RoseInputType] = {
    implicit val pdtyf: YamlFormat[ProgDataType] = ProgExYamlProtocol.progDataTypeYamlFormat

    yamlFormat3(RoseInputType)
  }

  private val roseSampleSolutionYamlFormat: YamlFormat[RoseSampleSolution] = {
    implicit val plyf: YamlFormat[ProgLanguage] = new EnumYamlFormat(ProgLanguages)

    yamlFormat3(RoseSampleSolution)
  }

  val roseExerciseYamlFormat: YamlFormat[RoseExercise] = {
    implicit val svyf : YamlFormat[SemanticVersion]    = semanticVersionYamlFormat
    implicit val ltyf : YamlFormat[LongText]           = LongTextYamlProtocol.longTextYamlFormat
    implicit val esyf : YamlFormat[ExerciseState]      = exerciseStateYamlFormat
    implicit val rityf: YamlFormat[RoseInputType]      = roseInputTypeYamlFormat
    implicit val rssyf: YamlFormat[RoseSampleSolution] = roseSampleSolutionYamlFormat

    yamlFormat13(RoseExercise)
  }

}
