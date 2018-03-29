package model.questions

import model.Enums.ExerciseState
import model.MyYamlProtocol._
import model.questions.QuestionConsts._
import model.questions.QuestionEnums.{Correctness, QuestionType}
import model.{MyYamlProtocol, YamlArr}
import net.jcazevedo.moultingyaml._
import play.api.Logger

import scala.language.postfixOps
import scala.util.Try

object QuestionYamlProtocol extends MyYamlProtocol {

  implicit object QuizYamlFormat extends HasBaseValuesYamlFormat[CompleteQuiz] {

    override protected def readRest(yamlObject: YamlObject, baseValues: (Int, String, String, String, ExerciseState)): Try[CompleteQuiz] = for {
      theme <- yamlObject.stringField(themeName)
      questionTries <- yamlObject.arrayField(exercisesName, QuestionYamlFormat(baseValues._1).read)
    } yield {
      for (questionFailure <- questionTries._2)
      // FIXME: return...
        Logger.error("Could not read question", questionFailure.exception)

      CompleteQuiz(new Quiz(baseValues, theme), questionTries._1)
    }

    override protected def writeRest(completeEx: CompleteQuiz): Map[YamlValue, YamlValue] = Map(
      YamlString(themeName) -> completeEx.coll.theme,
      YamlString(exercisesName) -> YamlArr(completeEx.exercises map QuestionYamlFormat(completeEx.coll.id).write)
    )
  }

  case class QuestionYamlFormat(quizId: Int) extends HasBaseValuesYamlFormat[CompleteQuestion] {

    override protected def readRest(yamlObject: YamlObject, baseValues: (Int, String, String, String, ExerciseState)): Try[CompleteQuestion] = for {
      questionType <- yamlObject.enumField(exerciseTypeName, QuestionType.valueOf)
      maxPoints <- yamlObject.intField(maxPointsName)
      answerTries <- yamlObject.arrayField(answersName, QuestionAnswerYamlFormat(quizId, baseValues._1).read)
    } yield {
      for (answerFailure <- answerTries._2)
      // FIXME: return...
        Logger.error("Could not read answer", answerFailure.exception)

      CompleteQuestion(new Question(baseValues, quizId, questionType, maxPoints), answerTries._1)
    }

    override protected def writeRest(completeEx: CompleteQuestion): Map[YamlValue, YamlValue] = Map(
      YamlString(exerciseTypeName) -> completeEx.ex.questionType.name,
      YamlString(maxPointsName) -> completeEx.ex.maxPoints,
      YamlString(answersName) -> YamlArr(completeEx.answers map QuestionAnswerYamlFormat(completeEx.ex.collectionId, completeEx.ex.id).write)
    )

  }

  case class QuestionAnswerYamlFormat(quizId: Int, questionId: Int) extends MyYamlObjectFormat[Answer] {

    override def readObject(yamlObject: YamlObject): Try[Answer] = for {
      id <- yamlObject.intField(idName)
      text <- yamlObject.stringField(textName)
      correctness <- yamlObject.enumField(correctnessName, Correctness.valueOf)
      maybeExplanation <- yamlObject.optStringField(explanationName)
    } yield {


      Answer(id, questionId, quizId, text, correctness, maybeExplanation)
    }

    override def write(obj: Answer): YamlValue = {
      val values: Map[YamlValue, YamlValue] = Map(
        YamlString(textName) -> obj.text,
        YamlString(correctnessName) -> obj.correctness.name
      )

      new YamlObject(values ++ obj.explanation.map(e => YamlString("explanation") -> YamlString(e)))
    }

  }

}
