package model.tools.collectionTools.sql

import model.{MyYamlProtocol, StringSampleSolution}
import net.jcazevedo.moultingyaml.{DefaultYamlProtocol, YamlFormat}

object SqlYamlProtocol extends MyYamlProtocol {

  import DefaultYamlProtocol._

  val sqlExerciseYamlFormat: YamlFormat[SqlExerciseContent] = {
    implicit val eTypeYf: YamlFormat[SqlExerciseType]      = new EnumYamlFormat(SqlExerciseType)
    implicit val eTagYf : YamlFormat[SqlExerciseTag]       = new EnumYamlFormat(SqlExerciseTag)
    implicit val sssyf  : YamlFormat[StringSampleSolution] = stringSampleSolutionYamlFormat

    yamlFormat4(SqlExerciseContent)
  }

}
