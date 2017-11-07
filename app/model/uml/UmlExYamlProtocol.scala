package model.uml

import model.Enums.ExerciseState
import model.core.StringConsts._
import net.jcazevedo.moultingyaml.{DefaultYamlProtocol, YamlArray, YamlFormat, YamlNumber, YamlObject, YamlString, YamlValue, deserializationError}

object UmlExYamlProtocol extends DefaultYamlProtocol {

  implicit def string2YamlString(str: String): YamlString = YamlString(str)

  implicit object UmlExYamlFormat extends YamlFormat[UmlCompleteEx] {
    override def write(completeEx: UmlCompleteEx): YamlValue = {
      val ex = completeEx.ex
      YamlObject(
        YamlString(ID_NAME) -> YamlNumber(ex.id),
        YamlString(TITLE_NAME) -> YamlString(ex.title),
        YamlString(AUTHOR_NAME) -> YamlString(ex.author),
        YamlString(TEXT_NAME) -> YamlString(ex.text),
        YamlString(STATE_NAME) -> YamlString(ex.state.name),

        // Exercise specific values
        // TODO: solution: String, mappings: Map[String, String], ignoreWords: List[String]
        YamlString(SOLUTION_NAME) -> YamlString(ex.solution)
        //      YamlString(MAPPINGS_NAME) -> YamlObject(ex.mappings),
        //      YamlString(IGNORE_WORDS_NAME) -> YamlString(ex.refernenceFile)
      )
    }

    override def read(yaml: YamlValue): UmlCompleteEx =
      yaml.asYamlObject.getFields(ID_NAME, TITLE_NAME, AUTHOR_NAME, TEXT_NAME, STATE_NAME, SOLUTION_NAME, MAPPINGS_NAME, IGNORE_WORDS_NAME) match {
        case Seq(YamlNumber(id), YamlString(title), YamlString(author), YamlString(text), YamlString(state), YamlObject(solution), YamlObject(mappings), YamlArray(toIngore)) =>

          val textParse = new UmlExTextParser(text, mappings.map(yamlVals => (yamlVals._1.toString, yamlVals._2.toString)), toIngore.toList.map(_.toString))
          UmlCompleteEx(UmlExercise(id.intValue, title, author, text, ExerciseState.valueOf(state),
            textParse.parseTextForClassSel, textParse.parseTextForDiagDrawing, solution.mkString, mappings.mkString, toIngore.toList.map(_.toString).mkString))

        case other => /* FIXME: Fehlerbehandlung... */
          other.foreach(value => println(value + "\n"))
          deserializationError("UmlExercise expected!")
      }
  }

}