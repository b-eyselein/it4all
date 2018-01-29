package model.questions

import model.MyYamlProtocol._
import model.questions.QuestionConsts._
import model.questions.QuestionEnums.{Correctness, QuestionType}
import model.{BaseValues, MyYamlProtocol}
import net.jcazevedo.moultingyaml._

import scala.language.postfixOps

object QuestionYamlProtocol extends MyYamlProtocol {

  implicit object QuizYamlFormat extends HasBaseValuesYamlFormat[CompleteQuiz] {

    override protected def readRest(yamlObject: YamlObject, baseValues: BaseValues): CompleteQuiz = CompleteQuiz(
      Quiz(baseValues, yamlObject.stringField(ThemeName)),
      yamlObject.arrayField(EXERCISES_NAME, _ convertTo[CompleteQuestion] QuestionYamlFormat(baseValues.id))
    )

    override protected def writeRest(completeEx: CompleteQuiz): Map[YamlValue, YamlValue] = Map(
      YamlString(ThemeName) -> completeEx.coll.theme,
      YamlString(EXERCISES_NAME) -> YamlArray(completeEx.exercises map (_ toYaml QuestionYamlFormat(completeEx.id)) toVector)
    )
  }

  case class QuestionYamlFormat(quizId: Int) extends HasBaseValuesYamlFormat[CompleteQuestion] {

    override protected def readRest(yamlObject: YamlObject, baseValues: BaseValues): CompleteQuestion = CompleteQuestion(
      Question(baseValues, quizId, yamlObject.enumField(ExerciseTypeName, QuestionType.valueOf, QuestionType.CHOICE), yamlObject.intField(MAX_POINTS)),
      yamlObject.arrayField(ANSWERS_NAME, _ convertTo[Answer] QuestionAnswerYamlFormat(quizId, baseValues.id))
    )

    override protected def writeRest(completeEx: CompleteQuestion): Map[YamlValue, YamlValue] = Map(
      YamlString(ExerciseTypeName) -> completeEx.ex.questionType.name,
      YamlString(MAX_POINTS) -> completeEx.ex.maxPoints,
      YamlString(ANSWERS_NAME) -> YamlArray(completeEx.answers map (_ toYaml QuestionAnswerYamlFormat(completeEx.ex.collectionId, completeEx.id)) toVector)
    )

  }

  case class QuestionAnswerYamlFormat(quizId: Int, questionId: Int) extends MyYamlFormat[Answer] {

    override def readObject(yamlObject: YamlObject): Answer = Answer(yamlObject.intField(ID_NAME), questionId, quizId,
      yamlObject.stringField(TEXT_NAME),
      yamlObject.enumField(CorrectnessName, Correctness.valueOf, Correctness.OPTIONAL),
      yamlObject.optStringField("explanation"))

    override def write(obj: Answer): YamlValue = {
      val values: Map[YamlValue, YamlValue] = Map(
        YamlString(TEXT_NAME) -> obj.text,
        YamlString(CorrectnessName) -> obj.correctness.name
      )

      new YamlObject(values ++ obj.explanation.map(e => YamlString("explanation") -> YamlString(e)))
    }

  }

}