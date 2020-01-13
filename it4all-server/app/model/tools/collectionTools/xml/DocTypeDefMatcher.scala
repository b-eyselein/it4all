package model.tools.collectionTools.xml

import de.uniwue.dtd.model.ElementLine
import model.core.matching.{AnalysisResult, MatchType, Matcher}
import model.points._
import play.api.libs.json.JsValue

final case class ElementLineAnalysisResult(
  matchType: MatchType,
  contentCorrect: Boolean,
  correctContent: String,
  attributesCorrect: Boolean,
  correctAttributes: String
) extends AnalysisResult {

  override def toJson: JsValue = XmlToolJsonProtocol.elementLineAnalysisResultWrites.writes(this)

  def points: Points = addUp(Seq(contentCorrect, attributesCorrect).map {
    case false => zeroPoints
    case true  => singlePoint
  })

}

object DocTypeDefMatcher extends Matcher[ElementLine, ElementLineAnalysisResult, ElementLineMatch] {

  override protected val matchName: String = "DTD-Zeilen"

  override protected val matchSingularName: String = "DTD-Zeile"

  override protected def canMatch(el1: ElementLine, el2: ElementLine): Boolean = el1.elementName == el2.elementName

  override protected def matchInstantiation(ua: Option[ElementLine], sa: Option[ElementLine]): ElementLineMatch =
    ElementLineMatch(ua, sa)

}
