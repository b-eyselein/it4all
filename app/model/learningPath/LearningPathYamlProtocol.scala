package model.learningPath

import model.MyYamlProtocol
import model.MyYamlProtocol._
import model.core.CoreConsts._
import model.learningPath.LearningPathSectionType._
import net.jcazevedo.moultingyaml._
import play.api.Logger

import scala.util.Try

object LearningPathYamlProtocol extends MyYamlProtocol {

  case class LearningPathYamlFormat(toolUrl: String) extends MyYamlObjectFormat[LearningPath] {

    override protected def readObject(yamlObject: YamlObject): Try[LearningPath] = for {
      id <- yamlObject.intField(idName)
      title <- yamlObject.stringField(titleName)
      sections <- yamlObject.arrayField("sections", LearningPathSectionYamlFormat(toolUrl, id).read)
    } yield {

      for (sectionError <- sections._2) {
        // TODO: solve!
        Logger.error(sectionError.toString)
      }

      LearningPath(toolUrl, id, title, sections._1)
    }

    override def write(obj: LearningPath): YamlValue = ???

  }

  case class LearningPathSectionYamlFormat(toolUrl: String, pathId: Int) extends MyYamlObjectFormat[LearningPathSection] {

    override protected def readObject(yamlObject: YamlObject): Try[LearningPathSection] = for {
      id: Int <- yamlObject.intField(idName)
      sectionType <- yamlObject.enumField(typeName, LearningPathSectionType.withNameInsensitiveOption(_) getOrElse LearningPathSectionType.TextSectionType)
      title <- yamlObject.stringField(titleName)

      section: LearningPathSection <- sectionType match {
        case QuestionSectionType => readQuestionSection(yamlObject, id, title)
        case TextSectionType     => readTextSection(yamlObject, id, title)
      }
    } yield section

    private def readTextSection(yamlObject: YamlObject, id: Int, title: String): Try[TextSection] = for {
      content: String <- yamlObject.stringField(contentName)
    } yield TextSection(id, toolUrl, pathId, title, content)

    private def readQuestionSection(yamlObject: YamlObject, id: Int, title: String): Try[QuestionSection] = for {
      questions <- yamlObject.arrayField(questionsName, readLPQuestions)
    } yield {

      for (failure <- questions._2) {
        Logger.error("Error: ", failure.exception)
      }

      QuestionSection(id, toolUrl, pathId, title, questions._1)
    }

    private def readLPQuestions(yamlValue: YamlValue): Try[LPQuestion] = for {
      yamlObj <- Try(yamlValue.asYamlObject)
      text <- yamlObj.stringField(textName)
      solution <- yamlObj.jsonField(solutionName)
    } yield LPQuestion(text, solution)

    override def write(obj: LearningPathSection): YamlValue = ???

  }

}
