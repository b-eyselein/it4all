package model.questions

import javax.inject.{Inject, Singleton}
import model._
import model.questions.QuestionConsts._
import model.questions.QuestionEnums.QuestionType
import model.toolMains.CollectionToolMain
import model.yaml.MyYamlFormat
import play.api.data.Form
import play.api.mvc._
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.language.{implicitConversions, postfixOps}
import scala.util.{Failure, Try}

@Singleton
class QuestionToolMain @Inject()(override val tables: QuestionTableDefs)(implicit ec: ExecutionContext)
  extends CollectionToolMain("Allgemeine Fragen", "question") with JsonFormat {

  // Abstract types

  override type ExType = Question

  override type CompExType = CompleteQuestion

  override type CollType = Quiz

  override type CompCollType = CompleteQuiz

  override type Tables = QuestionTableDefs

  override type SolType = Seq[GivenAnswer]

  override type DBSolType = QuestionSolution

  override type R = IdAnswerMatch

  override type CompResult = QuestionResult

  // Other members

  override val collectionSingularName: String = "Quiz"
  override val collectionPluralName  : String = "Quizze"

  override val consts: Consts = QuestionConsts

  // DB

  override protected def saveSolution(solution: QuestionSolution): Future[Boolean] = ???

  // Reading from requests

  override def readSolutionFromPostRequest(user: User, collId: Int, id: Int)(implicit request: Request[AnyContent]): Option[SolType] =
    request.body.asJson flatMap (_.asObj) flatMap { jsObj =>
      val maybeGivenAnswers: Option[Seq[IdGivenAnswer]] = jsObj.stringField(questionTypeName) flatMap QuestionType.byString flatMap {
        case QuestionType.CHOICE   => jsObj.arrayField("chosen", jsValue => Some(IdGivenAnswer(jsValue.asInt getOrElse -1)))
        case QuestionType.FREETEXT => ??? // Some(Seq.empty)
      }

      maybeGivenAnswers // map (givenAnswers => QuestionSolution(user.username, collId, id, givenAnswers))
    }

  override protected def compExTypeForm(collId: Int): Form[CompleteQuestion] = ???

  // Yaml

  override implicit val yamlFormat: MyYamlFormat[CompleteQuiz] = QuestionYamlProtocol.QuizYamlFormat

  // Views

  override def adminRenderEditRest(collOpt: Option[CompleteQuiz]): Html = new Html(
    s"""<div class="form-group row">
       |  <div class="col-sm-12">
       |    <label for="${QuestionConsts.themeName}">Thema:</label>
       |    <input class="form-control" name="${QuestionConsts.themeName}" id="${QuestionConsts.themeName}" required ${collOpt map (coll => s"""value="${coll.coll.theme}"""") getOrElse ""})>
       |  </div>
       |</div>""".stripMargin)

  override def renderExercise(user: User, quiz: Quiz, exercise: CompleteQuestion, numOfExes: Int): Future[Html] = Future {
    views.html.collectionExercises.questions.question(user, quiz, exercise, numOfExes, None /* FIXME: old answer... UserAnswer.finder.byId(new UserAnswerKey(user.name, exercise.id))*/)
  }

  override def renderEditRest(exercise: CompleteQuestion): Html = ???

  // Result handlers

  override def onSubmitCorrectionError(user: User, error: Throwable): Html = ???

  override def onSubmitCorrectionResult(user: User, result: QuestionResult): Html = ???

  //  override def onLiveCorrectionResult(result: QuestionResult): JsValue = result.forJson

  // Correction

  def correctEx(user: User, answers: Seq[GivenAnswer], quiz: Quiz, exercise: CompleteQuestion): Future[Try[QuestionResult]] = Future {
    exercise.ex.questionType match {
      case QuestionType.FREETEXT => Failure(new Exception("Not yet implemented..."))
      case QuestionType.CHOICE   => Try {
        val idAnswers: Seq[IdGivenAnswer] = answers flatMap {
          case idA: IdGivenAnswer => Some(idA)
          case _                  => None
        }

        QuestionResult(idAnswers, exercise)
        // ???
      }
    }
  }

  // Helper methods

  override def instantiateCollection(id: Int, state: ExerciseState): CompleteQuiz = CompleteQuiz(
    Quiz(id, SemanticVersion(0, 1, 0), title = "", author = "", text = "", state, theme = ""), exercises = Seq.empty)

  override def instantiateExercise(collId: Int, id: Int, state: ExerciseState): CompleteQuestion = CompleteQuestion(
    Question(id, SemanticVersion(0, 1, 0), title = "", author = "", text = "", state, collId, collSemVer = SemanticVersion(0, 1, 0), QuestionType.FREETEXT, -1), answers = Seq.empty)

  override def instantiateSolution(username: String, coll: Quiz, exercise: CompleteQuestion, solution: Seq[GivenAnswer], points: Points, maxPoints: Points): QuestionSolution =
    QuestionSolution(username, exercise.ex.id, exercise.ex.semanticVersion, coll.id, coll.semanticVersion, solution, points, maxPoints)

}
