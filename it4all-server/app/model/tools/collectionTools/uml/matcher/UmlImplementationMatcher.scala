package model.tools.collectionTools.uml.matcher

import model.core.matching._
import model.tools.collectionTools.uml.UmlImplementation

final case class UmlImplementationMatch(
  userArg: Option[UmlImplementation],
  sampleArg: Option[UmlImplementation],
  analysisResult: GenericAnalysisResult
) extends Match[UmlImplementation, GenericAnalysisResult] {

  override val maybeAnalysisResult: Option[GenericAnalysisResult] = Some(analysisResult)

}


object UmlImplementationMatcher extends Matcher[UmlImplementation, GenericAnalysisResult, UmlImplementationMatch] {

  override protected val matchName: String = "Vererbungen"

  override protected val matchSingularName: String = "der Vererbung"

  override protected def canMatch(i1: UmlImplementation, i2: UmlImplementation): Boolean =
    (i1.subClass == i2.subClass && i1.superClass == i2.superClass) || (i1.subClass == i2.superClass && i1.superClass == i2.subClass)

  override protected def instantiateOnlySampleMatch(sa: UmlImplementation): UmlImplementationMatch =
    UmlImplementationMatch(None, Some(sa), GenericAnalysisResult(MatchType.ONLY_SAMPLE))

  override protected def instantiateOnlyUserMatch(ua: UmlImplementation): UmlImplementationMatch =
    UmlImplementationMatch(Some(ua), None, GenericAnalysisResult(MatchType.ONLY_USER))

  override protected def instantiateCompleteMatch(ua: UmlImplementation, sa: UmlImplementation): UmlImplementationMatch = {
    val ar = GenericAnalysisResult(
      if (ua.subClass == sa.subClass && ua.superClass == sa.superClass) {
        MatchType.SUCCESSFUL_MATCH
      } else {
        MatchType.PARTIAL_MATCH
      }
    )

    UmlImplementationMatch(Some(ua), Some(sa), ar)
  }
}
