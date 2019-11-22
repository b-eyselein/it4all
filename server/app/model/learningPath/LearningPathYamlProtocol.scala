package model.learningPath

import net.jcazevedo.moultingyaml.{DefaultYamlProtocol, YamlFormat, YamlValue}

object LearningPathYamlProtocol {

  import DefaultYamlProtocol._

  private val learningPathSectionYamlFormat = new YamlFormat[LearningPathSection] {

    override def write(obj: LearningPathSection): YamlValue = ???

    override def read(yaml: YamlValue): LearningPathSection = ???

  }

  val learningPathYamlFormat: YamlFormat[LearningPath] = {
    implicit val lpsyf: YamlFormat[LearningPathSection] = learningPathSectionYamlFormat

    yamlFormat4(LearningPath)
  }


}
