package model.tools.sql

import model.{ExerciseState, MyYamlProtocol, SemanticVersion, StringSampleSolution}
import net.jcazevedo.moultingyaml.YamlFormat

object SqlYamlProtocol extends MyYamlProtocol {

   val sqlExerciseYamlFormat: YamlFormat[SqlExercise] = {
      implicit val svyf: YamlFormat[SemanticVersion] = semanticVersionYamlFormat

      implicit val esyf: YamlFormat[ExerciseState] = exerciseStateYamlFormat

      implicit val eTypeYf: YamlFormat[SqlExerciseType] = new EnumYamlFormat(SqlExerciseType)

      implicit val eTagYf: YamlFormat[SqlExerciseTag] = new EnumYamlFormat(SqlExerciseTag)

      implicit val sssyf: YamlFormat[StringSampleSolution] = stringSampleSolutionYamlFormat

      yamlFormat11(SqlExercise)
    }

}
