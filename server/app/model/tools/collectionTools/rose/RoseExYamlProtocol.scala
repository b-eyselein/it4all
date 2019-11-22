package model.tools.collectionTools.rose

import model.MyYamlProtocol
import model.tools.collectionTools.programming.{ProgDataType, ProgExYamlProtocol, ProgLanguage, ProgLanguages}
import net.jcazevedo.moultingyaml.{DefaultYamlProtocol, YamlFormat}

object RoseExYamlProtocol extends MyYamlProtocol {

  import DefaultYamlProtocol._

  private val roseInputTypeYamlFormat: YamlFormat[RoseInputType] = {
    implicit val pdtyf: YamlFormat[ProgDataType] = ProgExYamlProtocol.progDataTypeYamlFormat

    yamlFormat3(RoseInputType)
  }

  private val roseSampleSolutionYamlFormat: YamlFormat[RoseSampleSolution] = {
    implicit val plyf: YamlFormat[ProgLanguage] = new EnumYamlFormat(ProgLanguages)

    yamlFormat3(RoseSampleSolution)
  }

  val roseExerciseYamlFormat: YamlFormat[RoseExerciseContent] = {
    implicit val rityf: YamlFormat[RoseInputType]      = roseInputTypeYamlFormat
    implicit val rssyf: YamlFormat[RoseSampleSolution] = roseSampleSolutionYamlFormat

    yamlFormat5(RoseExerciseContent)
  }

}
