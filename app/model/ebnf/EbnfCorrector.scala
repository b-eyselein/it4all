package model.ebnf

import model.core.CompleteResult
import model.core.matching.{Match, Matcher, MatchingResult}
import play.twirl.api.Html


case class EbnfCompleteResult(learnerSolution: Grammar, result: EbnfTestdataMatchingResult) extends CompleteResult[EbnfTestdataMatchingResult] {

  override type SolType = Grammar

  override def results: Seq[EbnfTestdataMatchingResult] = Seq(result)

  override def renderLearnerSolution: Html = views.html.ebnf.ebnfGrammar.render(learnerSolution)

}


case class EbnfTestdataMatch(userArg: Option[EbnfTestData], sampleArg: Option[EbnfTestData]) extends Match[EbnfTestData] {

  override def descArg(arg: EbnfTestData): String = arg.testData

}


object EbnfCorrector extends Matcher[EbnfTestData, EbnfTestdataMatch, EbnfTestdataMatchingResult] {

  override def canMatch: (EbnfTestData, EbnfTestData) => Boolean = _.testData == _.testData

  override def matchInstantiation: (Option[EbnfTestData], Option[EbnfTestData]) => EbnfTestdataMatch = EbnfTestdataMatch

  override def resultInstantiation: Seq[EbnfTestdataMatch] => EbnfTestdataMatchingResult = EbnfTestdataMatchingResult

}


case class EbnfTestdataMatchingResult(allMatches: Seq[EbnfTestdataMatch]) extends MatchingResult[EbnfTestData, EbnfTestdataMatch] {

  override val matchName: String = "erzeugten Worte"

}