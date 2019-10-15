package model.tools.regex

import model.core.matching._
import model.points._
import play.api.libs.json.{JsValue, Json}

import scala.util.Try
import scala.util.matching.Regex.{Match => RegexMatch}

final case class RegexMatchMatch(userArg: Option[RegexMatch], sampleArg: Option[RegexMatch]) extends Match {

  override type T = RegexMatch

  override type AR = AnalysisResult

  override protected def analyze(arg1: RegexMatch, arg2: RegexMatch): AnalysisResult =
    GenericAnalysisResult(MatchType.SUCCESSFUL_MATCH)

  override protected def descArgForJson(arg: RegexMatch): JsValue = Json.obj(
    "start" -> arg.start,
    "end" -> arg.end,
    "content" -> arg.group(0)
  )

}

object RegexMatchMatcher extends Matcher[RegexMatchMatch] {

  override type T = RegexMatch

  override protected val matchName        : String = "RegexMatches"
  override protected val matchSingularName: String = "RegexMatch"

  override protected def canMatch(t1: RegexMatch, t2: RegexMatch): Boolean =
    t1.source == t2.source && t1.start == t2.start && t1.end == t2.end

  override protected def matchInstantiation(ua: Option[RegexMatch], sa: Option[RegexMatch]): RegexMatchMatch = RegexMatchMatch(ua, sa)

}

object RegexCorrector {

  def correct(sol: String, exercise: RegexExercise): Try[RegexCompleteResult] = Try(sol.r) map { userRegex =>

    exercise.correctionType match {
      case RegexCorrectionTypes.MATCHING =>

        val matchResults = exercise.matchTestData.map { matchTestData =>
          val classificationResultType: BinaryClassificationResultType = matchTestData.data match {
            case userRegex(_*) =>
              if (matchTestData.isIncluded) BinaryClassificationResultTypes.TruePositive
              else BinaryClassificationResultTypes.FalsePositive
            case _             =>
              if (matchTestData.isIncluded) BinaryClassificationResultTypes.FalseNegative
              else BinaryClassificationResultTypes.TrueNegative
          }

          RegexMatchingEvaluationResult(matchTestData.data, matchTestData.isIncluded, classificationResultType)
        }

        val correctResultsCount: Int = matchResults.count(_.resultType.correct)

        val points: Points = (correctResultsCount.toDouble / exercise.matchTestData.size.toDouble * exercise.maxPoints * 4).toInt.quarterPoints

        RegexCompleteResult(exercise.correctionType, matchResults, Seq.empty, points, exercise.maxPoints.points)


      case RegexCorrectionTypes.EXTRACTION =>

        val extractionResults = exercise.extractionTestData.map { extractionTestData =>

          val sampleRegex = exercise.sampleSolutions.headOption.map(_.sample).getOrElse(???).r

          val sampleExtracted: Seq[RegexMatch] = sampleRegex.findAllMatchIn(extractionTestData.base).toList


          val userExtracted: Seq[RegexMatch] = userRegex.findAllMatchIn(extractionTestData.base).toList


          val regexMatchMatchingResult = RegexMatchMatcher.doMatch(userExtracted, sampleExtracted)

          val correct = regexMatchMatchingResult.allMatches.forall(_.matchType == MatchType.SUCCESSFUL_MATCH)


          RegexExtractionEvaluationResult(extractionTestData.base, regexMatchMatchingResult, correct)
        }

        val correctResultsCount: Int = extractionResults.count(_.correct)

        val points: Points = (correctResultsCount.toDouble / exercise.extractionTestData.size.toDouble * exercise.maxPoints * 4).toInt.quarterPoints

        RegexCompleteResult(exercise.correctionType, Seq.empty, extractionResults, points, exercise.maxPoints.points)

    }

  }

}
