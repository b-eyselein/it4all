package model.learningPath

import model.MyYamlProtocol
import model.MyYamlProtocol._
import model.core.CoreConsts._
import model.learningPath.LearningPathSectionType._
import net.jcazevedo.moultingyaml._
import play.api.Logger

import scala.util.Try

object LearningPathYamlProtocol extends MyYamlProtocol {

  object LearningPathYamlFormat extends MyYamlObjectFormat[LearningPath] {

    override protected def readObject(yamlObject: YamlObject): Try[LearningPath] = for {
      id <- yamlObject.intField(idName)
      title <- yamlObject.stringField(titleName)
      sections <- yamlObject.arrayField("sections", LearningPathSectionYamlFormat(id).read)
    } yield {

      for (sectionError <- sections._2) {
        // TODO: solve!
        Logger.error(sectionError.toString)
      }

      LearningPath(id, title, sections._1)
    }

    override def write(obj: LearningPath): YamlValue = ???

  }

  case class LearningPathSectionYamlFormat(pathId: Int) extends MyYamlObjectFormat[LearningPathSection] {

    override protected def readObject(yamlObject: YamlObject): Try[LearningPathSection] = for {
      id: Int <- yamlObject.intField(idName)
      sectionType <- yamlObject.enumField(typeName, LearningPathSectionType.withNameInsensitiveOption(_) getOrElse LearningPathSectionType.TextSectionType)
      title <- yamlObject.stringField(titleName)

      section: LearningPathSection <- sectionType match {
        case QuestionSectionType => readQuestionSection(yamlObject, id, sectionType, title)
        case TextSectionType     => readTextSection(yamlObject, id, sectionType, title)
      }
    } yield section

    private def readTextSection(yamlObject: YamlObject, id: Int, sectionType: LearningPathSectionType, title: String): Try[TextSection] = for {
      content: String <- yamlObject.stringField(contentName)
    } yield TextSection(id, pathId, sectionType, title, content)

    private def readQuestionSection(yamlObject: YamlObject, id: Int, sectionType: LearningPathSectionType, title: String): Try[QuestionSection] = for {
      questions <- yamlObject.arrayField(questionsName, readLPQuestions)
    } yield {

      for (failure <- questions._2) {
        Logger.error("Error: ", failure.exception)
      }

      QuestionSection(id, pathId, sectionType, title, questions._1)
    }

    private def readLPQuestions(yamlValue: YamlValue): Try[LPQuestion] = for {
      yamlObj <- Try(yamlValue.asYamlObject)
      text <- yamlObj.stringField(textName)
      solution <- yamlObj.jsonField(solutionName)
    } yield LPQuestion(text, solution)

    override def write(obj: LearningPathSection): YamlValue = ???

  }

}
