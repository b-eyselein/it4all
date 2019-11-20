package model.tools.collectionTools.regex

import model.{ExerciseState, LongText, LongTextYamlProtocol, MyYamlProtocol, SemanticVersion, StringSampleSolution}
import net.jcazevedo.moultingyaml.YamlFormat

object RegexToolYamlProtocol extends MyYamlProtocol {

  val regexExerciseYamlFormat: YamlFormat[RegexExercise] = {
    implicit val svyf  : YamlFormat[SemanticVersion]         = semanticVersionYamlFormat
    implicit val ltyf  : YamlFormat[LongText]                = LongTextYamlProtocol.longTextYamlFormat
    implicit val esyf  : YamlFormat[ExerciseState]           = exerciseStateYamlFormat
    implicit val rctyf : YamlFormat[RegexCorrectionType]     = new EnumYamlFormat(RegexCorrectionTypes)
    implicit val rmtdyf: YamlFormat[RegexMatchTestData]      = yamlFormat3(RegexMatchTestData)
    implicit val retdyf: YamlFormat[RegexExtractionTestData] = yamlFormat2(RegexExtractionTestData)
    implicit val sssyf : YamlFormat[StringSampleSolution]    = stringSampleSolutionYamlFormat

    yamlFormat13(RegexExercise)
  }

}
