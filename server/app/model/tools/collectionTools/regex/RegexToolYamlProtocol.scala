package model.tools.collectionTools.regex

import model.{MyYamlProtocol, StringSampleSolution}
import net.jcazevedo.moultingyaml.{DefaultYamlProtocol, YamlFormat}

object RegexToolYamlProtocol extends MyYamlProtocol {

  import DefaultYamlProtocol._

  val regexExerciseYamlFormat: YamlFormat[RegexExerciseContent] = {
    implicit val rctyf : YamlFormat[RegexCorrectionType]     = new EnumYamlFormat(RegexCorrectionTypes)
    implicit val rmtdyf: YamlFormat[RegexMatchTestData]      = yamlFormat3(RegexMatchTestData)
    implicit val retdyf: YamlFormat[RegexExtractionTestData] = yamlFormat2(RegexExtractionTestData)
    implicit val sssyf : YamlFormat[StringSampleSolution]    = stringSampleSolutionYamlFormat

    yamlFormat5(RegexExerciseContent)
  }

}
