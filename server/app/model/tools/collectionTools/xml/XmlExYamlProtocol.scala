package model.tools.collectionTools.xml

import model.MyYamlProtocol
import model.core.{LongText, LongTextYamlProtocol}
import net.jcazevedo.moultingyaml.{DefaultYamlProtocol, YamlFormat}

object XmlExYamlProtocol extends MyYamlProtocol {

  import DefaultYamlProtocol._

  val xmlExerciseYamlFormat: YamlFormat[XmlExerciseContent] = {
    implicit val ltyf: YamlFormat[LongText] = LongTextYamlProtocol.longTextYamlFormat

    implicit val xssyf: YamlFormat[XmlSampleSolution] = {
      implicit val xsyf: YamlFormat[XmlSolution] = yamlFormat2(XmlSolution)

      yamlFormat2(XmlSampleSolution)
    }

    yamlFormat3(XmlExerciseContent)
  }

}
