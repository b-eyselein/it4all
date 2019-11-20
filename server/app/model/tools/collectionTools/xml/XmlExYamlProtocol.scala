package model.tools.collectionTools.xml

import model.core.{LongText, LongTextYamlProtocol}
import model.{ExerciseState, MyYamlProtocol, SemanticVersion}
import net.jcazevedo.moultingyaml._

object XmlExYamlProtocol extends MyYamlProtocol {

    val xmlExerciseYamlFormat: YamlFormat[XmlExercise] = {
      implicit val svyf: YamlFormat[SemanticVersion] = semanticVersionYamlFormat

      implicit val ltyf: YamlFormat[ LongText] = LongTextYamlProtocol.longTextYamlFormat

      implicit val esyf: YamlFormat[ExerciseState] = exerciseStateYamlFormat

      implicit val xssyf: YamlFormat[XmlSampleSolution] = {
        implicit val xsyf: YamlFormat[XmlSolution] = yamlFormat2(XmlSolution)

        yamlFormat2(XmlSampleSolution)
      }

      yamlFormat11(XmlExercise)
    }

}
