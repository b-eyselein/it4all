package model.questions

import model.MyYamlProtocol._
import model.questions.QuestionConsts._
import model.{BaseValues, MyYamlProtocol, YamlArr}
import net.jcazevedo.moultingyaml._
import play.api.Logger

import scala.language.postfixOps
import scala.util.Try

object QuestionYamlProtocol extends MyYamlProtocol {

  implicit object QuizYamlFormat extends MyYamlObjectFormat[CompleteQuiz] {

    override protected def readObject(yamlObject: YamlObject): Try[CompleteQuiz] = for {
      baseValues <- readBaseValues(yamlObject)

      theme <- yamlObject.stringField(themeName)
      questionTries <- yamlObject.arrayField(exercisesName, QuestionYamlFormat(baseValues).read)
    } yield {
      for (questionFailure <- questionTries._2)
      // FIXME: return...
        Logger.error("Could not read question", questionFailure.exception)

      CompleteQuiz(Quiz(baseValues.id, baseValues.semanticVersion, baseValues.title, baseValues.author, baseValues.text, baseValues.state, theme), questionTries._1)
    }

    override def write(completeEx: CompleteQuiz) = YamlObject(
      writeBaseValues(completeEx.coll) ++
        Map(
          YamlString(themeName) -> YamlString(completeEx.coll.theme),
          YamlString(exercisesName) -> YamlArr(completeEx.exercises map QuestionYamlFormat(completeEx.coll.baseValues).write)
        )
    )

  }

  final case class QuestionYamlFormat(quizBaseValues: BaseValues) extends MyYamlObjectFormat[CompleteQuestion] {

    override protected def readObject(yamlObject: YamlObject): Try[CompleteQuestion] = for {
      baseValues <- readBaseValues(yamlObject)

      questionType <- yamlObject.enumField(exerciseTypeName, QuestionTypes.withNameInsensitive)
      maxPoints <- yamlObject.intField(maxPointsName)
      answerTries <- yamlObject.arrayField(answersName, QuestionAnswerYamlFormat(quizBaseValues, baseValues).read)
    } yield {
      for (answerFailure <- answerTries._2)
      // FIXME: return...
        Logger.error("Could not read answer", answerFailure.exception)

      CompleteQuestion(Question(baseValues.id, baseValues.semanticVersion, baseValues.title, baseValues.author, baseValues.text, baseValues.state,
        quizBaseValues.id, quizBaseValues.semanticVersion, questionType, maxPoints), answerTries._1)
    }

    override def write(completeEx: CompleteQuestion): YamlValue = YamlObject(
      writeBaseValues(completeEx.ex) ++
        Map(
          YamlString(exerciseTypeName) -> YamlString(completeEx.ex.questionType.entryName),
          YamlString(maxPointsName) -> YamlNumber(completeEx.ex.maxPoints),
          YamlString(answersName) -> YamlArr(completeEx.answers map QuestionAnswerYamlFormat(quizBaseValues, completeEx.ex.baseValues).write)
        )
    )

  }

  final case class QuestionAnswerYamlFormat(quizBaseValues: BaseValues, questionBaseValues: BaseValues) extends MyYamlObjectFormat[Answer] {

    override def readObject(yamlObject: YamlObject): Try[Answer] = for {
      id <- yamlObject.intField(idName)
      text <- yamlObject.stringField(textName)
      correctness <- yamlObject.enumField(correctnessName, Correctnesses.withNameInsensitive)
      maybeExplanation <- yamlObject.optStringField(explanationName)
    } yield {


      Answer(id, questionBaseValues.id, questionBaseValues.semanticVersion, quizBaseValues.id, quizBaseValues.semanticVersion, text, correctness, maybeExplanation)
    }

    override def write(obj: Answer): YamlValue = {
      val values: Map[YamlValue, YamlValue] = Map(
        YamlString(textName) -> obj.text,
        YamlString(correctnessName) -> obj.correctness.entryName
      )

      new YamlObject(values ++ obj.explanation.map(e => YamlString("explanation") -> YamlString(e)))
    }

  }

}
