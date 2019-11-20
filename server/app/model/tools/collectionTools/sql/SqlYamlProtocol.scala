package model.tools.collectionTools.sql

import model.{ExerciseState, LongText, LongTextYamlProtocol, MyYamlProtocol, SemanticVersion, StringSampleSolution}
import net.jcazevedo.moultingyaml.YamlFormat

object SqlYamlProtocol extends MyYamlProtocol {

  val sqlExerciseYamlFormat: YamlFormat[SqlExercise] = {
    implicit val svyf   : YamlFormat[SemanticVersion]      = semanticVersionYamlFormat
    implicit val ltyf   : YamlFormat[LongText]             = LongTextYamlProtocol.longTextYamlFormat
    implicit val esyf   : YamlFormat[ExerciseState]        = exerciseStateYamlFormat
    implicit val eTypeYf: YamlFormat[SqlExerciseType]      = new EnumYamlFormat(SqlExerciseType)
    implicit val eTagYf : YamlFormat[SqlExerciseTag]       = new EnumYamlFormat(SqlExerciseTag)
    implicit val sssyf  : YamlFormat[StringSampleSolution] = stringSampleSolutionYamlFormat

    yamlFormat12(SqlExercise)
  }

}
