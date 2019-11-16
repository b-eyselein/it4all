package model.tools.regex

import model.{ExerciseState, MyYamlProtocol, SemanticVersion, StringSampleSolution}
import net.jcazevedo.moultingyaml.YamlFormat

object RegexToolYamlProtocol extends MyYamlProtocol {

  val regexExerciseYamlFormat: YamlFormat[RegexExercise] = {
    implicit val svyf: YamlFormat[SemanticVersion] = semanticVersionYamlFormat

    implicit val esyf: YamlFormat[ExerciseState] = exerciseStateYamlFormat

    implicit val rctyf: YamlFormat[RegexCorrectionType] = new EnumYamlFormat(RegexCorrectionTypes)

    implicit val rmtdyf: YamlFormat[RegexMatchTestData] = yamlFormat3(RegexMatchTestData)

    implicit val retdyf: YamlFormat[RegexExtractionTestData] = yamlFormat2(RegexExtractionTestData)

    implicit val sssyf: YamlFormat[StringSampleSolution] = stringSampleSolutionYamlFormat

    yamlFormat12(RegexExercise)
  }

}
