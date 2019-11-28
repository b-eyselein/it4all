package model.tools.collectionTools.sql

import model.MyYamlProtocol
import model.tools.collectionTools.SampleSolution
import net.jcazevedo.moultingyaml.{DefaultYamlProtocol, YamlFormat}

object SqlYamlProtocol extends MyYamlProtocol {

  import DefaultYamlProtocol._

  val sqlExerciseYamlFormat: YamlFormat[SqlExerciseContent] = {
    implicit val eTypeYf: YamlFormat[SqlExerciseType]        = new EnumYamlFormat(SqlExerciseType)
    implicit val eTagYf : YamlFormat[SqlExerciseTag]         = new EnumYamlFormat(SqlExerciseTag)
    implicit val sssyf  : YamlFormat[SampleSolution[String]] = sampleSolutionYamlFormat(DefaultYamlProtocol.StringYamlFormat)

    yamlFormat4(SqlExerciseContent)
  }

}
