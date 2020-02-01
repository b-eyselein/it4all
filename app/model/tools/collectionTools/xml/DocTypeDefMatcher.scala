package model.tools.collectionTools.xml

import de.uniwue.dtd.model.ElementLine
import model.core.matching.{MatchType, Matcher}

final case class ElementLineAnalysisResult(
  contentCorrect: Boolean,
  correctContent: String,
  attributesCorrect: Boolean,
  correctAttributes: String
) {

  //  override def toJson: JsValue = XmlToolJsonProtocol.elementLineAnalysisResultWrites.writes(this)
  /*
    def points: Points = addUp(Seq(contentCorrect, attributesCorrect).map {
      case false => zeroPoints
      case true  => singlePoint
    })
   */

}

object DocTypeDefMatcher extends Matcher[ElementLine, ElementLineMatch] {

  override protected val matchName: String = "DTD-Zeilen"

  override protected val matchSingularName: String = "DTD-Zeile"

  override protected def canMatch(el1: ElementLine, el2: ElementLine): Boolean = el1.elementName == el2.elementName

  override protected def instantiateOnlySampleMatch(sa: ElementLine): ElementLineMatch =
    ElementLineMatch(MatchType.ONLY_SAMPLE, None, Some(sa), None)

  override protected def instantiateOnlyUserMatch(ua: ElementLine): ElementLineMatch =
    ElementLineMatch(MatchType.ONLY_USER, Some(ua), None, None)

  override protected def instantiateCompleteMatch(ua: ElementLine, sa: ElementLine): ElementLineMatch = {

    val contentCorrect = ua.elementDefinition.contentAsString == sa.elementDefinition.contentAsString

    val arg1Def           = ua.attributeLists.headOption.map(_.asString)
    val arg2Def           = sa.attributeLists.headOption.map(_.asString)
    val attributesCorrect = arg1Def == arg2Def

    val matchType: MatchType = if (contentCorrect) {
      if (attributesCorrect) {
        MatchType.SUCCESSFUL_MATCH
      } else {
        MatchType.PARTIAL_MATCH
      }
    } else {
      MatchType.UNSUCCESSFUL_MATCH
    }

    val ar = ElementLineAnalysisResult(contentCorrect, sa.elementDefinition.contentAsString, attributesCorrect, arg2Def.getOrElse("FEHLER!"))

    ElementLineMatch(matchType, Some(ua), Some(sa), Some(ar))
  }
}
