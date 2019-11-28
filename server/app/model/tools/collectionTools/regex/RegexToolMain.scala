package model.tools.collectionTools.regex

import model.User
import model.tools.collectionTools.{CollectionToolMain, Exercise, ExerciseCollection, StringSampleSolutionToolJsonProtocol}
import net.jcazevedo.moultingyaml.YamlFormat

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object RegexToolMain extends CollectionToolMain(RegexConsts) {

  override type PartType = RegexExPart
  override type ExContentType = RegexExerciseContent
  override type SolType = String
  override type CompResultType = RegexCompleteResult

  // Members

  override val exParts: Seq[RegexExPart] = RegexExParts.values

  // Yaml, Html forms, Json

  override protected val toolJsonProtocol: StringSampleSolutionToolJsonProtocol[RegexExerciseContent, RegexCompleteResult] =
    RegexToolJsonProtocol

  override protected val exerciseContentYamlFormat: YamlFormat[RegexExerciseContent] = RegexToolYamlProtocol.regexExerciseYamlFormat

  // Correction

  override protected def correctEx(
    user: User,
    sol: String,
    coll: ExerciseCollection,
    exercise: Exercise,
    content: RegexExerciseContent,
    part: RegexExPart,
    solutionSaved: Boolean
  )(implicit executionContext: ExecutionContext): Future[Try[RegexCompleteResult]] =
    Future.successful(RegexCorrector.correct(sol, content, solutionSaved))

}
