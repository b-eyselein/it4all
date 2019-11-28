package model.tools.collectionTools.xml

import model.MyYamlProtocol
import model.core.{LongText, LongTextYamlProtocol}
import model.tools.collectionTools.SampleSolution
import net.jcazevedo.moultingyaml.{DefaultYamlProtocol, YamlFormat}

object XmlExYamlProtocol extends MyYamlProtocol {

  import DefaultYamlProtocol._

  val xmlExerciseYamlFormat: YamlFormat[XmlExerciseContent] = {
    implicit val ltyf: YamlFormat[LongText] = LongTextYamlProtocol.longTextYamlFormat

    implicit val xssyf: YamlFormat[SampleSolution[XmlSolution]] = sampleSolutionYamlFormat(yamlFormat2(XmlSolution))

    yamlFormat3(XmlExerciseContent)
  }

}
