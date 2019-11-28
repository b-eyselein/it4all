package model.tools.collectionTools.rose

import model.MyYamlProtocol
import model.tools.collectionTools.SampleSolution
import model.tools.collectionTools.programming.{ProgDataType, ProgExYamlProtocol}
import net.jcazevedo.moultingyaml.{DefaultYamlProtocol, YamlFormat}

object RoseExYamlProtocol extends MyYamlProtocol {

  import DefaultYamlProtocol._

  private val roseInputTypeYamlFormat: YamlFormat[RoseInputType] = {
    implicit val pdtyf: YamlFormat[ProgDataType] = ProgExYamlProtocol.progDataTypeYamlFormat

    yamlFormat3(RoseInputType)
  }

  val roseExerciseYamlFormat: YamlFormat[RoseExerciseContent] = {
    implicit val rityf: YamlFormat[RoseInputType]          = roseInputTypeYamlFormat
    implicit val rssyf: YamlFormat[SampleSolution[String]] = sampleSolutionYamlFormat(DefaultYamlProtocol.StringYamlFormat)

    yamlFormat5(RoseExerciseContent)
  }

}
