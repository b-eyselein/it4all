package model.questions

import model.MyYamlProtocol._
import model.questions.QuestionConsts._
import model.questions.QuestionEnums.{Correctness, QuestionType}
import model.{BaseValues, MyYamlProtocol, YamlArr}
import net.jcazevedo.moultingyaml._

import scala.language.postfixOps
import scala.util.Try

object QuestionYamlProtocol extends MyYamlProtocol {

  implicit object QuizYamlFormat extends HasBaseValuesYamlFormat[CompleteQuiz] {

    override protected def readRest(yamlObject: YamlObject, baseValues: BaseValues): Try[CompleteQuiz] = for {
      theme <- yamlObject.stringField(ThemeName)
      questions <- yamlObject.arrayField(EXERCISES_NAME, QuestionYamlFormat(baseValues.id).read)
    } yield CompleteQuiz(Quiz(baseValues, theme), questions)

    override protected def writeRest(completeEx: CompleteQuiz): Map[YamlValue, YamlValue] = Map(
      YamlString(ThemeName) -> completeEx.coll.theme,
      YamlString(EXERCISES_NAME) -> YamlArr(completeEx.exercises map QuestionYamlFormat(completeEx.id).write)
    )
  }

  case class QuestionYamlFormat(quizId: Int) extends HasBaseValuesYamlFormat[CompleteQuestion] {

    override protected def readRest(yamlObject: YamlObject, baseValues: BaseValues): Try[CompleteQuestion] = for {
      questionType <- yamlObject.enumField(ExerciseTypeName, QuestionType.valueOf)
      maxPoints <- yamlObject.intField(MAX_POINTS)
      answers <- yamlObject.arrayField(ANSWERS_NAME, QuestionAnswerYamlFormat(quizId, baseValues.id).read)
    } yield CompleteQuestion(Question(baseValues, quizId, questionType, maxPoints), answers)

    override protected def writeRest(completeEx: CompleteQuestion): Map[YamlValue, YamlValue] = Map(
      YamlString(ExerciseTypeName) -> completeEx.ex.questionType.name,
      YamlString(MAX_POINTS) -> completeEx.ex.maxPoints,
      YamlString(ANSWERS_NAME) -> YamlArr(completeEx.answers map QuestionAnswerYamlFormat(completeEx.ex.collectionId, completeEx.id).write)
    )

  }

  case class QuestionAnswerYamlFormat(quizId: Int, questionId: Int) extends MyYamlObjectFormat[Answer] {

    override def readObject(yamlObject: YamlObject): Try[Answer] = for {
      id <- yamlObject.intField(ID_NAME)
      text <- yamlObject.stringField(TEXT_NAME)
      correctness <- yamlObject.enumField(CorrectnessName, Correctness.valueOf)
      maybeExplanation <- yamlObject.optStringField("explanation")
    } yield Answer(id, questionId, quizId, text, correctness, maybeExplanation)

    override def write(obj: Answer): YamlValue = {
      val values: Map[YamlValue, YamlValue] = Map(
        YamlString(TEXT_NAME) -> obj.text,
        YamlString(CorrectnessName) -> obj.correctness.name
      )

      new YamlObject(values ++ obj.explanation.map(e => YamlString("explanation") -> YamlString(e)))
    }

  }

}
